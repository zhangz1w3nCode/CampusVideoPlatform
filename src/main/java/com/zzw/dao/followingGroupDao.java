package com.zzw.dao;

import com.zzw.Entity.followingGroup;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
//用户 关注 分组表
public interface followingGroupDao {
    //通过 type 查找 关注分组类
    followingGroup getByType(String type);

    //通过 id 查找 关注分组类
    followingGroup getById(Long id);

    List<followingGroup> getByUserId(Long userId);

    Integer addFollowingGroup(followingGroup followingGroup);

    List<followingGroup> getUserFollowingGroups(Long userID);
}
