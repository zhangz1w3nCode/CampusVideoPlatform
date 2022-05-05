package com.zzw.service;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.zzw.Entity.PageResult;
import com.zzw.Entity.RefreshTokenDetail;
import com.zzw.Entity.User;
import com.zzw.Entity.UserInfo;
import com.zzw.constant.userConstant;
import com.zzw.dao.userDao;
import com.zzw.exception.conditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzw.service.utils.MD5Util;
import com.zzw.service.utils.RSAUtil;
import com.zzw.service.utils.tokenUtil;

import java.util.*;

@Service

public class userService {

    @Autowired
    private userDao userDao;

    @Autowired
    private userAuthService userAuthService;

    public void addUser(User user) {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)){
            throw new conditionException("手机号不能为空!");
        }

        User dbUser = getUserByPhone(phone);
        if(dbUser!=null){
            throw new conditionException("手机号已注册!");
        }

        Date now = new Date();
        String salt = String.valueOf(now.getTime());//用户颜值

        String password = user.getPassword();//用户密码

        String rawPassword =null;
        try {
             rawPassword = RSAUtil.decrypt(password);
        }catch (Exception e){
            throw new conditionException("解密失败!");
        }

        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);

        userDao.addUser(user);

        //用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setNick(userConstant.DEFAULT_NICK);
        userInfo.setCreateTime(now);
        userInfo.setGender(userConstant.GENDER_UNKNOW);
        userInfo.setBirth(userConstant.DEFAULT_BIRTH);
        userDao.addUser_Info(userInfo);

        //添加用户默认的角色和权限

        userAuthService.addUserDefaultRole(user.getId());

    }

    public User getUserByPhone(String phone){
        return userDao.getUserByPhone(phone);
    }

    //登录方法
    public String login(User user) throws Exception {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)){
            throw new conditionException("手机号不能为空!");
        }

        User dbUser = getUserByPhone(phone);
        if(dbUser==null){
            throw new conditionException("用户不存在!");
        }

        String salt = dbUser.getSalt();//用户颜值
        String password = user.getPassword();//用户输入密码
        String rawPassword =null;

        try {
            rawPassword = RSAUtil.decrypt(password);
        }catch (Exception e){
            throw new conditionException("解密失败!");
        }

        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");

        if(!md5Password.equals(dbUser.getPassword())){
            throw new conditionException("密码错误!");
        }
        System.out.println("uid="+dbUser.getId());
        return tokenUtil.generateToken(dbUser.getId());
    }


    public Map<String, Object> loginForDts(User user) throws Exception {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)){
            throw new conditionException("手机号不能为空!");
        }

        User dbUser = getUserByPhone(phone);
        if(dbUser==null){
            throw new conditionException("用户不存在!");
        }

        String salt = dbUser.getSalt();//用户颜值
        String password = user.getPassword();//用户输入密码
        String rawPassword =null;

        try {
            rawPassword = RSAUtil.decrypt(password);
        }catch (Exception e){
            throw new conditionException("解密失败!");
        }

        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");

        if(!md5Password.equals(dbUser.getPassword())){
            throw new conditionException("密码错误!");
        }
        Long userId = dbUser.getId();

        String accessToken =  tokenUtil.generateToken(userId);
        String refreshToken = tokenUtil.generateRefreshToken(userId);

        //保存refreshToken到数据库
        userDao.deleterRefreshToken(refreshToken,userId);
        userDao.addRefreshToken(refreshToken,userId,new Date());
        Map<String,Object> map =new HashMap<>();
        map.put("accessToken",accessToken);
        map.put("refreshToken",refreshToken);
        return map;
    }

    public User getUserInfo(Long userID) {
        User user = userDao.getUserByID(userID);
        UserInfo userInfo =  userDao.getUserInfoByID(userID);

        user.setUserInfo(userInfo);

        return user;
    }



    public User getUserByID(Long followingId) {
        return userDao.getUserByID(followingId);
    }


    public List<UserInfo> getUserInfoByIds(Set<Long> userIdList) {
        return userDao.getUserInfoByIds(userIdList);
    }

    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = params.getInteger("no");
        Integer size = params.getInteger("size");
        params.put("start",(no-1)*size);
        params.put("limit",size);
        Integer total =userDao.pageContUserInfos(params);
        List<UserInfo> list = new ArrayList<>();

        if(total>0){
            list = userDao.pageListUserInfos(params);

        }

        return new PageResult<>(total,list);
    }


    public void logOut(String refreshToken, Long userId) {
        userDao.deleterRefreshToken(refreshToken,userId);
    }

    public String refreshAccessToken(String refreshToken) throws Exception {
        RefreshTokenDetail refreshTokenDetail = userDao.refreshAccessToken(refreshToken);
        if(refreshToken==null) throw new conditionException("555","token过期");
        Long userId = refreshTokenDetail.getUserId();
        return tokenUtil.generateToken(userId);
    }

    public List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdSet) {
        return userDao.batchGetUserInfoByUserIds(userIdSet);
    }
}
