package com.hncboy.chatgpt.front.controller;

import com.hncboy.chatgpt.base.annotation.FrontPreAuth;
import com.hncboy.chatgpt.base.handler.response.R;
import com.hncboy.chatgpt.front.domain.bo.UserProfile;
import com.hncboy.chatgpt.front.domain.request.UserQueryRequest;
import com.hncboy.chatgpt.front.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 */
@FrontPreAuth
@AllArgsConstructor
@Tag(name = "聊天相关接口")
@RestController
@RequestMapping("/userProfile")
public class UserController {

    @Resource
    private final UserService userService;

    @Operation(summary = "用户设置")
    @PostMapping("/submit")
    public R<Boolean> submitProfile(@RequestBody @Validated UserProfile userProfile, HttpServletResponse response) {
        return R.data(userService.createOrUpdate(userProfile));
    }

    @Operation(summary = "用户查询")
    @PostMapping("/query")
    public R<UserProfile> queryProfile(@RequestBody @Validated UserQueryRequest userQueryRequest, HttpServletResponse response) {
        return R.data(userService.query(userQueryRequest));
    }
}
