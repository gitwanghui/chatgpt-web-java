package com.hncboy.chatgpt.front.controller;

import com.hncboy.chatgpt.base.annotation.FrontPreAuth;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023/3/22 19:47
 * 聊天相关接口
 */
@FrontPreAuth
@AllArgsConstructor
@Tag(name = "聊天相关接口")
@RestController
@RequestMapping
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "消息处理")
    @PostMapping("/chat-process")
    @CrossOrigin
    public ResponseBodyEmitter chatProcess(@RequestBody @Validated ChatProcessRequest chatProcessRequest, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        log.info("chatProcessRequest={}", ObjectMapperUtil.toJson(chatProcessRequest));
        return chatService.chatProcess(chatProcessRequest);
    }
}
