package com.zzw.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;

@Mapper
@Repository
public interface DemoDao {
    public Long query (Long id);
    public String getNameByID(Long id);
    public Map<String,Object> getAll(Long id);
    public Date getTime(Long id);
    public Integer getScoreByName(String name);
}
