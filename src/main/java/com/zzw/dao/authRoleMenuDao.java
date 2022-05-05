package com.zzw.dao;

import com.zzw.Entity.auth.authRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Set;

@Mapper
@Repository
public interface authRoleMenuDao {
    List<authRoleMenu> getauthRoleMenuByRoleIds(@Param("roleIdSet") Set<Long> roleIdSet);
}
