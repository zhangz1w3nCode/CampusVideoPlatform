package com.zzw.dao;

import com.zzw.Entity.userFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
//用户 关注 表
public interface UserFollowingDao {

    void deleteUserFollowing(@Param("userId") Long userId,@Param("followingId") Long followingId);

    void addUserFollowing(userFollowing userFollow);

    List<userFollowing> getUserFolloing(Long userId);

    List<userFollowing> getUserFans(Long followingId);
}
