package com.zzw.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Mapper
@Repository
public interface UserCoinDao {

    Long getUserCoinsAmount(Long userId);

    void updateUserAmount(@Param("userId") Long userId, @Param("amount")long amount, @Param("updateTime")Date updateTime);
}
