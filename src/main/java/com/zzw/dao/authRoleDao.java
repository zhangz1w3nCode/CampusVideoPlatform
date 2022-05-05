package com.zzw.dao;

import com.zzw.Entity.auth.authRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface authRoleDao {
    authRole getRoleByCode(String code);
}
