package com.zzw.service;

import com.zzw.Entity.auth.authRole;
import com.zzw.Entity.auth.authRoleElementOperation;
import com.zzw.Entity.auth.authRoleMenu;
import com.zzw.dao.authRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class authRoleService {

    @Autowired
    private com.zzw.dao.authRoleDao authRoleDao;

    @Autowired
    private authRoleElementOperationService authRoleElementOperationService;

    @Autowired
    private authRoleMenuService authRoleMenuService;

    public List<authRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getRoleElementOperationByRoleIds(roleIdSet);
    }

    public List<authRoleMenu> getauthRoleMenuByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getauthRoleMenuByRoleIds(roleIdSet);
    }

    public authRole getRoleByCode(String code) {
        return authRoleDao.getRoleByCode(code);
    }
}
