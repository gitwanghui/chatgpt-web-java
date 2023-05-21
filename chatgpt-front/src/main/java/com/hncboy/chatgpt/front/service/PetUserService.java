package com.hncboy.chatgpt.front.service;

import com.hncboy.chatgpt.base.domain.entity.PetUserProfileDO;
import com.hncboy.chatgpt.front.domain.vo.PetUserProfileVO;

/**
 * 宠物接口
 */
public interface PetUserService {

    PetUserProfileVO queryById(Integer id);

    boolean saveOrUpdate0(PetUserProfileDO petUserProfileDO);
}
