package com.zzw.service;

import com.zzw.Entity.User;
import com.zzw.Entity.UserInfo;
import com.zzw.Entity.followingGroup;
import com.zzw.Entity.userFollowing;
import com.zzw.constant.userConstant;
import com.zzw.dao.UserFollowingDao;
import com.zzw.dao.followingGroupDao;
import com.zzw.dao.userDao;
import com.zzw.exception.conditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
//用户关注 服务
public class userFollowingService {
    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private followingGroupService followingGroupService;


    @Autowired
    private userService userService;


    //添加 用户关注 功能
    @Transactional
    public void addUserFollowings(userFollowing userFollow){

        Long userId = userFollow.getGroupId();

        if(userId==null){ //第一次添加关注
            followingGroup followingGroup = followingGroupService.getByType(userConstant.Following_group_Default);
            Long userID = followingGroup.getId();
            userFollow.setGroupId(userID);
        }else{
            followingGroup followingGroup = followingGroupService.getById(userId);
            if(followingGroup==null){
                throw  new conditionException("关注分组不存在");
            }
        }

        Long followingId = userFollow.getFollowingId();// 关注了谁 那个人的id

        User user = userService.getUserByID(followingId);

        if(user==null) throw  new conditionException("关注用户不存在");

        userFollowingDao.deleteUserFollowing(userFollow.getUserId(),followingId);

        userFollow.setCreateTime(new Date());

        userFollowingDao.addUserFollowing(userFollow);
    }

    //获取用户关注列表
    public List<followingGroup> getUserFolloings(Long userId){

        // 用户关注列表
        List<userFollowing> list = userFollowingDao.getUserFolloing(userId);

        Set<Long> followingIdSet = list.stream().map(userFollowing::getFollowingId).collect(Collectors.toSet());

        //userInfoList => 所有关注的人的个人信息表
        List<UserInfo> userInfoList = new ArrayList<>();

        if(userInfoList.size()>0){
            userInfoList = userService.getUserInfoByIds(followingIdSet);
        }

        for(userFollowing userFollowing:list){
            for(UserInfo userInfo :userInfoList){
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }


        List<followingGroup> followingList = followingGroupService.getByUserId(userId);

        followingGroup allGroup = new followingGroup();

        allGroup.setName(userConstant.Following_group_All_group);

        allGroup.setFollowingUserInfoList(userInfoList);

        List<followingGroup> result = new ArrayList<>();

        result.add(allGroup);

        for (followingGroup group :followingList ) {
            List<UserInfo> infoList = new ArrayList<>();
            for (userFollowing userFollowing : list) {
                if(group.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }

        return result;


    }

    //获取用户粉丝列表
    public List<userFollowing> getUserFans(Long userId){
        //fanlist 所有关注userId 的 用户
        List<userFollowing> fanlist = userFollowingDao.getUserFans(userId);
        // 所有关注userId 的 用户的 UserId
        Set<Long> fanIdSet = fanlist.stream().map(userFollowing::getUserId).collect(Collectors.toSet());

        //userInfoList => 所有关注userId 的 用户的 个人信息表  粉丝个人信息 集合
        List<UserInfo> userInfoList = new ArrayList<>();

        if(userInfoList.size()>0){
            userInfoList = userService.getUserInfoByIds(fanIdSet);
        }
        //folloingList 用户关注的所有人 列表
        List<userFollowing> folloingList = userFollowingDao.getUserFolloing(userId);
        
        for (userFollowing fan: fanlist) {
            for (UserInfo userInfo : userInfoList) {
                if(fan.getUserId().equals(userInfo.getUserId())){

                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }

            for (userFollowing following : folloingList) {
                if(fan.getUserId().equals(following.getUserId())){
                    fan.getUserInfo().setFollowed(true);//互粉
                }
            }



        }
        return fanlist;

    }

    public Long addUserFollowingGroups(followingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(userConstant.Following_group_User);
        followingGroupService.addFollowingGroup(followingGroup);
        Long id = followingGroup.getId();
        return id;
    }

    public List<followingGroup> getUserFollowingGroups(Long userID) {
        return followingGroupService.getUserFollowingGroups(userID);
    }

    public List<UserInfo> cheackFollowingStatus(List<UserInfo> userInfoList, Long userId) {

        List<userFollowing> userFollowingList = userFollowingDao.getUserFolloing(userId);

        for (UserInfo userInfo :userInfoList ) {
            userInfo.setFollowed(false);
            for(userFollowing userFollowing:userFollowingList){
                    if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                        userInfo.setFollowed(true);
                    }
            }
        }

        return userInfoList;
    }
}
