package com.hncboy.chatgpt.front.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hncboy.chatgpt.base.domain.entity.PetChatRoomDO;
import org.springframework.stereotype.Repository;

@Repository("FrontPetChatRoomMapper")
public interface PetChatRoomMapper extends BaseMapper<PetChatRoomDO> {
}
