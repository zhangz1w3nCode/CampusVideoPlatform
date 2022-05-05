package com.zzw.service;

import com.zzw.Entity.auth.userRole;
import com.zzw.dao.userRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class userRoleService {

    @Autowired
    private com.zzw.dao.userRoleDao userRoleDao;

    public List<userRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);
    }

    public void addUserRole(userRole userRole) {
        userRole.setCreateTime(new Date());
        userRoleDao.addUserRole(userRole);
    }
}
