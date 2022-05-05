package com.zzw.dao;

import com.alibaba.fastjson.JSONObject;
import com.zzw.Entity.RefreshTokenDetail;
import com.zzw.Entity.User;
import com.zzw.Entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Mapper
public interface userDao {
    Integer addUser(User user);
    User getUserByPhone(String phone);
    Integer addUser_Info(UserInfo userInfo);
    User getUserByID(Long userID);
    UserInfo getUserInfoByID(Long userID);
    List<UserInfo> getUserInfoByIds(Set<Long> userIdList);
    Integer pageContUserInfos(Map<String,Object> params);
    List<UserInfo> pageListUserInfos(Map<String,Object> params);

    Integer deleterRefreshToken(@Param("refreshToken") String refreshToken,@Param("userId") Long userId);

    Integer addRefreshToken(@Param("refreshToken")String refreshToken,@Param("userId") Long userId, @Param("createTime")Date createTime);

    RefreshTokenDetail refreshAccessToken(String refreshToken);

    List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdSet);
}
