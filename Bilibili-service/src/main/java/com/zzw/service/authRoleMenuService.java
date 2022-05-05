package com.zzw.service;

import com.zzw.Entity.auth.authRoleMenu;
import com.zzw.dao.authRoleMenuDao;
import org.springframework.data.repository.query.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class authRoleMenuService {

    @Autowired
    private com.zzw.dao.authRoleMenuDao authRoleMenuDao;

    public List<authRoleMenu> getauthRoleMenuByRoleIds(@Param("roleIdSet") Set<Long> roleIdSet) {
        return authRoleMenuDao.getauthRoleMenuByRoleIds(roleIdSet);
    }
}
