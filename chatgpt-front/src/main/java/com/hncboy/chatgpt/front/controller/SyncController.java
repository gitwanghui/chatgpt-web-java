package com.hncboy.chatgpt.front.controller;

import com.hncboy.chatgpt.base.domain.entity.PetUserProfileDO;
import com.hncboy.chatgpt.base.handler.response.R;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.domain.request.DataSyncRequest;
import com.hncboy.chatgpt.front.service.PetUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Tag(name = "数据同步接口")
@RestController
@RequestMapping
@Slf4j
public class SyncController {

    private final PetUserService petUserService;

    @Operation(summary = "数据同步")
    @PostMapping("/data-sync")
    public R<Boolean> dataSync(@RequestBody @Validated DataSyncRequest dataSyncRequest, HttpServletResponse response) {
        log.info("dataSyncRequest={}", ObjectMapperUtil.toJson(dataSyncRequest));
        PetUserProfileDO petUserProfileDO = ObjectMapperUtil.fromJson(dataSyncRequest.getData(), PetUserProfileDO.class);
        return R.data(petUserService.saveOrUpdate0(petUserProfileDO));
    }
}
