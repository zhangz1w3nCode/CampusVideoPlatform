package com.zzw.service;

import com.zzw.Entity.auth.authRoleElementOperation;
import com.zzw.dao.authRoleElementOperationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class authRoleElementOperationService {
    @Autowired
    private com.zzw.dao.authRoleElementOperationDao authRoleElementOperationDao;

    public List<authRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIdSet) {
        return  authRoleElementOperationDao.getRoleElementOperationByRoleIds(roleIdSet);
    }
}
