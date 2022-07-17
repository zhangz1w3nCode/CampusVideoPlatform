package com.zzw.service;

import com.zzw.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DemoService implements DemoSvc {

    @Autowired
    public DemoDao demoDao;

    @Override
    public Long query(Long id) {
        return demoDao.query(id);
    }

    @Override
    public String getNameByID(Long id) {
        return demoDao.getNameByID(id);
    }

    @Override
    public Map<String, Object> getAll(Long id) {
        return demoDao.getAll(id);
    }

    @Override
    public Date getTime(Long id) {
        return demoDao.getTime(id);
    }

    @Override
    public Integer getScoreByName(String name) {
        return demoDao.getScoreByName(name);
    }


}
