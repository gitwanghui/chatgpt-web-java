package com.hncboy.chatgpt.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.PetUserProfileDO;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.domain.vo.PetUserProfileVO;
import com.hncboy.chatgpt.front.mapper.PetUserProfileMapper;
import com.hncboy.chatgpt.front.service.PetUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PetUserServiceImpl extends ServiceImpl<PetUserProfileMapper, PetUserProfileDO> implements PetUserService {

    @Override
    public PetUserProfileVO queryById(Integer id) {
        PetUserProfileDO petUserProfileDO = this.getOne(new LambdaQueryWrapper<PetUserProfileDO>()
                .eq(PetUserProfileDO::getId, id));
        if (petUserProfileDO != null) {
            PetUserProfileVO vo = ObjectMapperUtil.fromJson(petUserProfileDO.getProfile(), PetUserProfileVO.class);
            BeanUtils.copyProperties(petUserProfileDO, vo);
            return vo;
        }
        return null;
    }

    @Override
    public boolean saveOrUpdate0(PetUserProfileDO petUserProfileDO) {
        return this.saveOrUpdate(petUserProfileDO);
    }
}
