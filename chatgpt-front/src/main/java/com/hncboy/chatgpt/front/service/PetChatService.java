package com.hncboy.chatgpt.front.service;

public interface PetChatService {


    boolean saveChatInfo(Integer userId, Integer chatUserId, String chatContext);

    String queryChatInfo(Integer userId, Integer chatUserId);

    String querySystemMessage(Integer userId, Integer chatUserId);
}
