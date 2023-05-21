package com.hncboy.chatgpt.front.domain.vo;

import lombok.Data;

@Data
public class PetUserProfileVO {

    private Integer id;

    private Integer roleType;

    private String roleName;

    private String nickName;

    private Integer gender;

    private Integer avatarType;

    private String avatar;

    private Integer petType;

    private String birthday;

    private String keywords;
}
