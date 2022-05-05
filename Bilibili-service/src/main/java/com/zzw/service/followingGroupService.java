package com.zzw.service;

import com.zzw.Entity.followingGroup;
import com.zzw.dao.followingGroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//用户关注分组 服务
public class followingGroupService {
    @Autowired
   private followingGroupDao followingGroupDao;

    public followingGroup getByType(String type){
        return followingGroupDao.getByType(type);
    }

    public followingGroup getById(Long id){
        return followingGroupDao.getById(id);
    }

    public List<followingGroup> getByUserId(Long userId) {
        return followingGroupDao.getByUserId(userId);
    }

    public void addFollowingGroup(followingGroup followingGroup) {
        followingGroupDao.addFollowingGroup(followingGroup);
    }

    public List<followingGroup> getUserFollowingGroups(Long userID) {
        return  followingGroupDao.getUserFollowingGroups(userID);
    }
}
