package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user_profile")
public class UserProfileDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（外部）
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 宠物名称
     */
    private String petName;

    /**
     * 宠物种类
     */
    private String petType;

    /**
     * 宠物头像
     */
    private String petAvatar;

    /**
     * 聊天室ID
     */
    private String chatInfo;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
