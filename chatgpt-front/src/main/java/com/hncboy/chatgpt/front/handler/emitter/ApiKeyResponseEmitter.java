package com.hncboy.chatgpt.front.handler.emitter;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import com.hncboy.chatgpt.base.enums.ChatMessageStatusEnum;
import com.hncboy.chatgpt.base.enums.ChatMessageTypeEnum;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.api.apikey.ApiKeyChatClientBuilder;
import com.hncboy.chatgpt.front.api.listener.ConsoleStreamListener;
import com.hncboy.chatgpt.front.api.listener.ParsedEventSourceListener;
import com.hncboy.chatgpt.front.api.listener.ResponseBodyEmitterStreamListener;
import com.hncboy.chatgpt.front.api.parser.ChatCompletionResponseParser;
import com.hncboy.chatgpt.front.api.storage.ApiKeyDatabaseDataStorage;
import com.hncboy.chatgpt.front.domain.bo.UserProfile;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.domain.request.UserQueryRequest;
import com.hncboy.chatgpt.front.service.ChatMessageService;
import com.hncboy.chatgpt.front.service.PetChatService;
import com.hncboy.chatgpt.front.service.UserService;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import jakarta.annotation.Resource;

import java.util.*;

/**
 * @author hncboy
 * @date 2023/3/24 15:51
 * ApiKey 响应处理
 */
@Component
@Slf4j
public class ApiKeyResponseEmitter implements ResponseEmitter {

    @Resource
    private ChatConfig chatConfig;

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private ChatCompletionResponseParser parser;

    @Resource
    private ApiKeyDatabaseDataStorage dataStorage;

    @Resource
    private UserService userService;

    @Resource
    private PetChatService petChatService;

    private List<String> keywordBlackList = Arrays.asList("openai","chatgpt","gpt","gpt-2","gpt-3","transformer","ai language model","人工智能","机器学习","深度学习","自然语言处理","对话系统","聊天机器人","文本生成","语音交互","语义理解","智能客服","聊天助手","智能语音助手","智能问答","智能聊天","智能语音识别");

    @Override
    public ResponseBodyEmitter requestToResponseEmitter(ChatProcessRequest chatProcessRequest, ResponseBodyEmitter emitter) {
        // 初始化聊天消息
        ChatMessageDO chatMessageDO = chatMessageService.initChatMessage(chatProcessRequest, ApiTypeEnum.API_KEY);

        // 所有消息
        LinkedList<Message> messages = new LinkedList<>();
        // TODO 需要包含上下文 tokens 计算
        // 添加用户上下文消息
        addContextChatMessage(chatMessageDO, messages);

        // 系统角色消息
        String systemMessageStr = null;
        if(StrUtil.isNotBlank(chatProcessRequest.getUserId())
                && StrUtil.isNotBlank(chatProcessRequest.getChatUserId())) {
//            UserQueryRequest userQueryRequest = new UserQueryRequest();
//            userQueryRequest.setUserId(chatProcessRequest.getUserId());
//            UserProfile userProfile = userService.query(userQueryRequest);
//            if(userProfile != null) {
//                systemMessageStr = String.format("你是一只%s，你的名字是%s，你是%s的宠物，你要认真听从%s的指令。"
//                        , userProfile.getPetType()
//                        , userProfile.getPetName()
//                        , userProfile.getUserName()
//                        , userProfile.getUserName());
//            }

            systemMessageStr = petChatService.querySystemMessage(Integer.valueOf(chatProcessRequest.getUserId())
                    , Integer.valueOf(chatProcessRequest.getChatUserId()));
        }
        if (systemMessageStr == null && StrUtil.isNotBlank(chatProcessRequest.getSystemMessage())) {
            systemMessageStr = chatProcessRequest.getSystemMessage();
        }
        // 系统消息
        Message systemMessage = Message.builder()
                .role(Message.Role.SYSTEM)
                .content(systemMessageStr)
                .build();
        messages.addFirst(systemMessage);
        log.info("messages={}", ObjectMapperUtil.toJson(messages));

        // 构建聊天参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .maxTokens(1000)
                .model(chatConfig.getOpenaiApiModel())
                // [0, 2] 越低越精准
                .temperature(1)
                .topP(1.0)
                // 每次生成一条
                .n(1)
                .presencePenalty(1)
                .messages(messages)
                .stream(true)
                .build();

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
//                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setParser(parser)
                .setDataStorage(dataStorage)
                .setOriginalRequestData(ObjectMapperUtil.toJson(chatCompletion))
                .setChatMessageDO(chatMessageDO)
                .setUserId(chatProcessRequest.getUserId())
                .setChatUserId(chatProcessRequest.getChatUserId())
                .build();

        ApiKeyChatClientBuilder.buildOpenAiStreamClient().streamChatCompletion(chatCompletion, parsedEventSourceListener);
        return emitter;
    }

    /**
     * 添加上下文问题消息
     *
     * @param chatMessageDO 当前消息
     * @param messages      消息列表
     */
    private void addContextChatMessage(ChatMessageDO chatMessageDO, LinkedList<Message> messages) {
        if (Objects.isNull(chatMessageDO)) {
            return;
        }
        // 最多N条消息
        if(messages.size() >= chatConfig.getMaxRecentContextCount()) {
            return;
        }
        // 父级消息id为空，表示是第一条消息，直接添加到message里
        if (Objects.isNull(chatMessageDO.getParentMessageId())) {
            messages.addFirst(Message.builder().role(Message.Role.USER)
              .content(chatMessageDO.getContent())
              .build());
            return;
        }

        // 根据消息类型去选择角色，需要添加问题和回答到上下文
        Message.Role role = (chatMessageDO.getMessageType() == ChatMessageTypeEnum.ANSWER) ?
                Message.Role.ASSISTANT : Message.Role.USER;

        // 回答不成功的情况下，不添加回答消息记录和该回答的问题消息记录
        if (chatMessageDO.getMessageType() == ChatMessageTypeEnum.ANSWER
                && chatMessageDO.getStatus() != ChatMessageStatusEnum.PART_SUCCESS
                && chatMessageDO.getStatus() != ChatMessageStatusEnum.COMPLETE_SUCCESS) {
            // 没有父级回答消息直接跳过
            if (Objects.isNull(chatMessageDO.getParentAnswerMessageId())) {
                return;
            }
            ChatMessageDO parentMessage = chatMessageService.getOne(new LambdaQueryWrapper<ChatMessageDO>()
                    .eq(ChatMessageDO::getMessageId, chatMessageDO.getParentAnswerMessageId()));
            addContextChatMessage(parentMessage, messages);
            return;
        }

        if (chatMessageDO.getMessageType() == ChatMessageTypeEnum.ANSWER) {
            int totalLength = messages.stream().mapToInt(a -> StringUtils.length(a.getContent())).sum();
            int length = chatMessageDO.getContent().length();
            if (totalLength + length > 2048) {
                log.warn("terminate addContextChatMessage, totalLength={}, length={}, threshold", totalLength, length, 2048);
                return;
            }
            Optional<String> black = keywordBlackList.stream()
                    .filter(t -> chatMessageDO.getContent().toLowerCase().contains(t))
                    .findFirst();
            if (black.isPresent()) {
                log.warn("terminate addContextChatMessage, match keyword={}", black.get());
                return;
            }
        }

        // 从下往上找并添加，越上面的数据放越前面
        messages.addFirst(Message.builder().role(role)
                .content(chatMessageDO.getContent())
                .build());
        ChatMessageDO parentMessage = chatMessageService.getOne(new LambdaQueryWrapper<ChatMessageDO>()
            .eq(ChatMessageDO::getMessageId, chatMessageDO.getParentMessageId()));
        addContextChatMessage(parentMessage, messages);
    }
}
