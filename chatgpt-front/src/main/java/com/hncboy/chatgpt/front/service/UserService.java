package com.hncboy.chatgpt.front.service;

import com.hncboy.chatgpt.front.domain.bo.UserProfile;
import com.hncboy.chatgpt.front.domain.request.UserQueryRequest;

/**
 * 用户接口
 */
public interface UserService {

    boolean createOrUpdate(UserProfile userProfile);

    UserProfile query(UserQueryRequest userQueryRequest);
}
