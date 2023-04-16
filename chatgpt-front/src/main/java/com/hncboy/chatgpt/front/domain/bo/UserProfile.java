package com.hncboy.chatgpt.front.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户信息
 */
@Data
@Schema(title = "用户信息")
public class UserProfile {

    @NotNull(message = "用户ID不能为空")
    @Schema(title = "用户ID")
    private String userId;

    @NotNull(message = "用户名称不能为空")
    @Schema(title = "用户名称")
    private String userName;

    @NotNull(message = "宠物种类不能为空")
    @Schema(title = "宠物种类")
    private String petType;

    @NotNull(message = "宠物名称不能为空")
    @Schema(title = "宠物名称")
    private String petName;

    @NotNull(message = "宠物头像不能为空")
    @Schema(title = "宠物头像")
    private String petAvatar;

    @Schema(title = "聊天室")
    private String chatInfo;
}
