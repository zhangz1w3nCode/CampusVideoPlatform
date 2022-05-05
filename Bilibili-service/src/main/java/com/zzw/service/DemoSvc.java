package com.zzw.service;

import com.zzw.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public interface DemoSvc {
    public Long query(Long id);
    public String getNameByID(Long id);
    public Map<String,Object> getAll(Long id);
    Date getTime(Long id);
    Integer getScoreByName(String name);
}
