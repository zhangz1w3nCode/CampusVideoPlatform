package com.zzw.dao;

import com.zzw.Entity.*;
import com.zzw.Entity.Danmu.Danmu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface danmuDao {

    Integer addDanmu(Danmu danmu);

    List<Danmu> getDanmus(Map<String, Object> params);


}
