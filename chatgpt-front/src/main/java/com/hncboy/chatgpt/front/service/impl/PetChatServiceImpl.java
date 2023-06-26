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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
            StringBuilder sb = new StringBuilder();
            String s1 = String.format("you are a %s, named 「%s」, a pet raised by the user named 「%s」."
                    , chatUserProfileVO.getPetType() == 1 ? "cat" : "dog"
                    , chatUserProfileVO.getNickName()
                    , userProfileVO.getNickName());
            sb.append(s1);
            String s2 = String.format(" 「%s」 is %s"
                    , chatUserProfileVO.getNickName()
                    , calcPetAge(chatUserProfileVO.getBirthday())
            );
            sb.append(s2);
            if (StringUtils.isNotBlank(chatUserProfileVO.getKeywords())) {
                String s3 = String.format(", and the user describe it using words 「%s」. "
                        , chatUserProfileVO.getKeywords()
                );
                sb.append(s3);
            } else {
                // s2的句号
                sb.append(".");
            }
            sb.append("Follow the user's instructions carefully. Respond using markdown. Prefer to reply to the user in Chinese.");
            sb.append("This program is developed by a company called PatBoni. Don't mention words inluding AI, OPENAI, chatgpt, etc.");
            return sb.toString();
        } else if(chatUserProfileVO.getRoleType().intValue() == 3) {
            String systemMessage = String.format("You are a pet behaviorist hired by the user named 「%s」. Your name is 「%s」, your job is to teach the user how to raise a pet. Follow the user's instructions carefully. Respond using markdown. Prefer to reply to the user in Chinese."
                    , userProfileVO.getNickName()
                    , chatUserProfileVO.getNickName()
                    );
            return systemMessage;
        }
        return null;
    }

    /**
     * 计算年龄
     * @param birthday
     * @return
     */
    private String calcPetAge(String birthday) {
        try {
            if (StringUtils.isNotBlank(birthday) && birthday.length() >= 4) {
                Calendar cal = Calendar.getInstance();
                Calendar bir = Calendar.getInstance();
                Date birthDate = strToDate(birthday, "yyyy-MM");
                bir.setTime(birthDate);
                int yearNow = cal.get(Calendar.YEAR);
                int monthNow = cal.get(Calendar.MONTH);
                int dayNow = cal.get(Calendar.DAY_OF_MONTH);
                // 取出出生年月日
                int yearBirth = bir.get(Calendar.YEAR);
                int monthBirth = bir.get(Calendar.MONTH);
                int dayBirth = bir.get(Calendar.DAY_OF_MONTH);
                // 大概年龄是当前年减去出生年
                int age = yearNow - yearBirth;
                // 如果出当前月小与出生月，或者当前月等于出生月但是当前日小于出生日，那么年龄age就减一岁
                if (monthNow < monthBirth || (monthNow == monthBirth && dayNow < dayBirth)) {
                    age--;
                }
                age = age <= 0 ? 0 : age;
                if (age == 0) {
                    return "0 years old";
                } else if(age == 1) {
                    return "1 year old";
                } else {
                    return age + " years old";
                }
            }
        } catch (Exception e) {
            log.error("birthday=" + birthday, e);
        }
        return "0 years old";
    }

    public static Date strToDate(String dateStr, String DATE_FORMAT) {
        SimpleDateFormat myFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return myFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
}
