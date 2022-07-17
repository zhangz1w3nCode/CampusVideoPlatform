package com.zzw.dao;

import com.zzw.Entity.auth.userRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface userRoleDao {
    List<userRole> getUserRoleByUserId(Long userId);

    Integer addUserRole(userRole userRole);
}
