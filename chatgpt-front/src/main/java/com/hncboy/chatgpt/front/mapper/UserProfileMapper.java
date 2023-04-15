package com.hncboy.chatgpt.front.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hncboy.chatgpt.base.domain.entity.UserProfileDO;
import org.springframework.stereotype.Repository;

@Repository("FrontUserProfileMapper")
public interface UserProfileMapper extends BaseMapper<UserProfileDO> {
}
