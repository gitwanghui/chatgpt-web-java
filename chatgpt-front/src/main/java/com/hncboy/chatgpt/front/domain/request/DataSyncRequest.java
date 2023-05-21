package com.hncboy.chatgpt.front.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/3/23 13:17
 * 数据同步请求
 */
@Data
@Schema(title = "数据同步请求")
public class DataSyncRequest {

    @Size(min = 1, max = 99999999, message = "数据非法")
    @Schema(title = "数据")
    private String data;

}
