package com.zzw.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.zzw.Entity.File;

@Mapper
@Repository
public interface fileDao {
    Integer addFile(File file);
    File getFileByMD5(String md5);
}
