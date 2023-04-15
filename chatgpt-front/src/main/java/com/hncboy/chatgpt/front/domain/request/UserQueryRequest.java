package com.hncboy.chatgpt.front.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "用户查询")
public class UserQueryRequest {

    @NotNull(message = "用户ID不能为空")
    @Schema(title = "用户ID")
    private String userId;
}
