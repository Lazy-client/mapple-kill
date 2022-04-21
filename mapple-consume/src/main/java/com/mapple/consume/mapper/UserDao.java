package com.mapple.consume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mapple.consume.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface UserDao extends BaseMapper<UserEntity> {


    @Update("update mk_user set balance = balance - #{payAmount}, version = version + 1 " +
            "where user_id = #{userId} and version = #{version}")
    int deductMoney(BigDecimal payAmount, String userId, long version);
}
