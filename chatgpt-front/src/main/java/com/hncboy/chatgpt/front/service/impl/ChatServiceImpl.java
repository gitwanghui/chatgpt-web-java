package com.hncboy.chatgpt.front.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.domain.bo.UserProfile;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.domain.request.UserQueryRequest;
import com.hncboy.chatgpt.front.handler.emitter.ChatMessageEmitterChain;
import com.hncboy.chatgpt.front.handler.emitter.IpRateLimiterEmitterChain;
import com.hncboy.chatgpt.front.handler.emitter.ResponseEmitterChain;
import com.hncboy.chatgpt.front.handler.emitter.SensitiveWordEmitterChain;
import com.hncboy.chatgpt.front.service.ChatService;
import com.hncboy.chatgpt.front.service.PetChatService;
import com.hncboy.chatgpt.front.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023/3/22 19:41
 * 聊天相关业务实现类
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private UserService userService;

    @Resource
    private PetChatService petChatService;

    @Override
    public ResponseBodyEmitter chatProcess(ChatProcessRequest chatProcessRequest) {
        if(StringUtils.isBlank(chatProcessRequest.getUserId()) || StringUtils.isBlank(chatProcessRequest.getChatUserId())) {
            return new ResponseBodyEmitter(1L);
        }

        // 超时时间设置 3 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> log.debug("请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(chatProcessRequest)));
        emitter.onTimeout(() -> log.error("请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(chatProcessRequest)));

        if(chatProcessRequest.getUserId() != null && chatProcessRequest.getChatUserId() != null
                && !(chatProcessRequest.getOptions() != null
                    && StringUtils.isNoneBlank(chatProcessRequest.getOptions().getConversationId())
                    && StringUtils.isNoneBlank(chatProcessRequest.getOptions().getParentMessageId()))) {
            try {
//                UserQueryRequest userQueryRequest = new UserQueryRequest();
//                userQueryRequest.setUserId(chatProcessRequest.getUserId());
//                UserProfile userProfile = userService.query(userQueryRequest);
//                if(userProfile != null && userProfile.getChatInfo() != null) {
//                    JSONObject jsonObject = JSONUtil.parseObj(userProfile.getChatInfo());
//                    ChatProcessRequest.Options options = new ChatProcessRequest.Options();
//                    options.setConversationId(jsonObject.getStr("lastAnswerConversationId"));
//                    options.setParentMessageId(jsonObject.getStr("lastAnswerMessageId"));
//                    chatProcessRequest.setOptions(options);
//                }
                String chatContext = petChatService.queryChatInfo(Integer.valueOf(chatProcessRequest.getUserId())
                        , Integer.valueOf(chatProcessRequest.getChatUserId()));
                if(chatContext != null) {
                    JSONObject jsonObject = JSONUtil.parseObj(chatContext);
                    ChatProcessRequest.Options options = new ChatProcessRequest.Options();
                    options.setConversationId(jsonObject.getStr("lastAnswerConversationId"));
                    options.setParentMessageId(jsonObject.getStr("lastAnswerMessageId"));
                    chatProcessRequest.setOptions(options);
                }
                log.info("fulfill options, chatProcessRequest={}", JSONUtil.toJsonStr(chatProcessRequest));
            } catch (Exception e) {
                log.error("", e);
            }
        }

        // 构建 emitter 处理链路
        ResponseEmitterChain ipRateLimiterEmitterChain = new IpRateLimiterEmitterChain();
        ResponseEmitterChain sensitiveWordEmitterChain = new SensitiveWordEmitterChain();
        sensitiveWordEmitterChain.setNext(new ChatMessageEmitterChain());
        ipRateLimiterEmitterChain.setNext(sensitiveWordEmitterChain);
        ipRateLimiterEmitterChain.doChain(chatProcessRequest, emitter);
        return emitter;
    }
}
