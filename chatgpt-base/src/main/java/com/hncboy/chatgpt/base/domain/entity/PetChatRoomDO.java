package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("pet_chat_room")
public class PetChatRoomDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer userId;

    private Integer chatUserId;

    private String chatContext;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModified;
}
