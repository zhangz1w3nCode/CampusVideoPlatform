package com.zzw.dao;

import com.zzw.Entity.userMoment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface userMomentDao {
    Integer addUserMoments(userMoment userMoment);
}
