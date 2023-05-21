package com.hncboy.chatgpt.front.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hncboy.chatgpt.base.domain.entity.PetUserProfileDO;
import org.springframework.stereotype.Repository;

@Repository("FrontPetUserProfileMapper")
public interface PetUserProfileMapper extends BaseMapper<PetUserProfileDO> {
}
