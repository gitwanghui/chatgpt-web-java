package com.hncboy.chatgpt.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.PetChatRoomDO;
import com.hncboy.chatgpt.front.domain.vo.PetUserProfileVO;
import com.hncboy.chatgpt.front.mapper.PetChatRoomMapper;
import com.hncboy.chatgpt.front.service.PetChatService;
import com.hncboy.chatgpt.front.service.PetUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class PetChatServiceImpl extends ServiceImpl<PetChatRoomMapper, PetChatRoomDO> implements PetChatService {

    @Resource
    private PetUserService petUserService;

    @Override
    public boolean saveChatInfo(Integer userId, Integer chatUserId, String chatContext) {
        PetChatRoomDO petChatRoomDO = this.getOne(new LambdaQueryWrapper<PetChatRoomDO>()
                .eq(PetChatRoomDO::getUserId, userId)
                .eq(PetChatRoomDO::getChatUserId, chatUserId)
        );
        if(petChatRoomDO != null) {
            petChatRoomDO.setChatContext(chatContext);
            return this.update(petChatRoomDO, new LambdaQueryWrapper<PetChatRoomDO>()
                    .eq(PetChatRoomDO::getUserId, userId)
                    .eq(PetChatRoomDO::getChatUserId, chatUserId));
        } else {
            PetChatRoomDO newPetChatRoomDO = new PetChatRoomDO();
            newPetChatRoomDO.setUserId(userId);
            newPetChatRoomDO.setChatUserId(chatUserId);
            newPetChatRoomDO.setStatus(1);
            newPetChatRoomDO.setGmtCreate(new Date());
            newPetChatRoomDO.setGmtModified(new Date());
            newPetChatRoomDO.setChatContext(chatContext);
            return this.save(newPetChatRoomDO);
        }
    }

    @Override
    public String queryChatInfo(Integer userId, Integer chatUserId) {
        PetChatRoomDO petChatRoomDO = this.getOne(new LambdaQueryWrapper<PetChatRoomDO>()
                .eq(PetChatRoomDO::getUserId, userId)
                .eq(PetChatRoomDO::getChatUserId, chatUserId)
        );
        if(petChatRoomDO != null) {
            return petChatRoomDO.getChatContext();
        }
        return null;
    }

    @Override
    public String querySystemMessage(Integer userId, Integer chatUserId) {
        PetUserProfileVO userProfileVO = petUserService.queryById(userId);
        PetUserProfileVO chatUserProfileVO = petUserService.queryById(chatUserId);
        if(userProfileVO == null || chatUserProfileVO == null) {
            return null;
        }
        if (chatUserProfileVO.getRoleType().intValue() == 2) {
            String systemMessage = String.format("you are a %s, called 「%s」, a pet raised by a chinese called 「%s」. Follow the user's instructions carefully. Respond using markdown."
                    , chatUserProfileVO.getPetType() == 1 ? "cat" : "dog"
                    , chatUserProfileVO.getNickName()
                    , userProfileVO.getNickName());
            return systemMessage;
        } else if(chatUserProfileVO.getRoleType().intValue() == 3) {
            String systemMessage = String.format("You are a pet behaviorist hired by a chinese called 「%s」. your name is 「%s」. your job is to teach the user how to raise a pet. Follow the user's instructions carefully. Respond using markdown."
                    , userProfileVO.getNickName()
                    , chatUserProfileVO.getNickName()
                    );
            return systemMessage;
        }
        return null;
    }
}
