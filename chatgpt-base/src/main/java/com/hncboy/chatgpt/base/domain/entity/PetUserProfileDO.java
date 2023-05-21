package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("pet_user_profile")
public class PetUserProfileDO {

    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    private Integer roleType;

    private Integer userId;

    private String profile;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModified;
}
