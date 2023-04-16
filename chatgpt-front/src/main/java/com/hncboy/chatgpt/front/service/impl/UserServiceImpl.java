package com.hncboy.chatgpt.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.UserProfileDO;
import com.hncboy.chatgpt.front.domain.bo.UserProfile;
import com.hncboy.chatgpt.front.domain.request.UserQueryRequest;
import com.hncboy.chatgpt.front.mapper.UserProfileMapper;
import com.hncboy.chatgpt.front.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserProfileMapper, UserProfileDO> implements UserService {

    @Override
    public boolean createOrUpdate(UserProfile userProfile) {
        UserProfileDO userProfileDO = this.getOne(new LambdaQueryWrapper<UserProfileDO>()
                .eq(UserProfileDO::getUserId, userProfile.getUserId()));
        if(userProfileDO == null) {
            userProfileDO = new UserProfileDO();
            BeanUtils.copyProperties(userProfile, userProfileDO);
            return save(userProfileDO);
        } else {
            return update(new LambdaUpdateWrapper<UserProfileDO>()
                    .set(UserProfileDO::getUserName, fieldValue(userProfile.getUserName(), userProfileDO.getUserName()))
                    .set(UserProfileDO::getPetName, fieldValue(userProfile.getPetName(), userProfileDO.getPetName()))
                    .set(UserProfileDO::getPetType, fieldValue(userProfile.getPetType(), userProfileDO.getPetType()))
                    .set(UserProfileDO::getPetAvatar, fieldValue(userProfile.getPetAvatar(), userProfileDO.getPetAvatar()))
                    .set(UserProfileDO::getChatInfo, fieldValue(userProfile.getChatInfo(), userProfileDO.getChatInfo()))
                    .eq(UserProfileDO::getUserId, userProfile.getUserId()));
        }
    }

    @Override
    public UserProfile query(UserQueryRequest userQueryRequest) {
        UserProfileDO userProfileDO = this.getOne(new LambdaQueryWrapper<UserProfileDO>()
                .eq(UserProfileDO::getUserId, userQueryRequest.getUserId()));
        UserProfile userProfile = new UserProfile();
        if (userProfileDO != null) {
            BeanUtils.copyProperties(userProfileDO, userProfile);
        }
        return userProfile;
    }

    private String fieldValue(String newData, String oldData) {
        return StringUtils.isBlank(newData) ? newData : oldData;
    }
}
