package com.zzw.service;

import com.zzw.Entity.auth.*;
import com.zzw.constant.authRoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class userAuthService {

    @Autowired
    private userRoleService userRoleService; //用户-角色-连接服务

    @Autowired
    private authRoleService authRoleService;//角色-服务

    public userAuthorities getAuthorities(Long userId) {
        //获取用户角色列表
       List<userRole> userRoleList =  userRoleService.getUserRoleByUserId(userId);
       //查出角色对应的角色id
        Set<Long> roleIdSet = userRoleList.stream().map(userRole::getRoleId).collect(Collectors.toSet());
        //通过角色id查询对应操作权限
        List<authRoleElementOperation> authRoleElementOperationList = authRoleService.getRoleElementOperationByRoleIds(roleIdSet);
        //通过角色id查询对应修改元素权限
        List<authRoleMenu> authRoleMenuList = authRoleService.getauthRoleMenuByRoleIds(roleIdSet);

        userAuthorities userAuthorities = new userAuthorities();

        userAuthorities.setAuthRoleMenuList(authRoleMenuList);
        userAuthorities.setRoleElementOperationList(authRoleElementOperationList);
        return userAuthorities;
    }

    public void addUserDefaultRole(Long userId) {
        userRole userRole =new userRole();
        authRole role = authRoleService.getRoleByCode(authRoleConstant.ROLE_CODE_LV0);
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        userRoleService.addUserRole(userRole);

    }
}
