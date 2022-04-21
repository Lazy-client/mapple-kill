package com.mapple.consume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mapple.consume.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

    int deductBalance(String userId, BigDecimal payAmount);
}