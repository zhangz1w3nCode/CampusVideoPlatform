package com.zzw.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.zzw.Entity.*;
import com.zzw.Entity.Danmu.Danmu;
import com.zzw.dao.danmuDao;
import com.zzw.dao.videoDao;
import com.zzw.exception.conditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class danmuService {

    @Autowired
    private danmuDao danmuDao;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //弹幕数据添加到mysql--同步
    public void addDanmu(Danmu danmu){
        danmuDao.addDanmu(danmu);
    }

    //弹幕数据添加到mysql--异步发送--优化！--不占用主线程
    @Async
    public void asycAddDanmu(Danmu danmu){
        danmuDao.addDanmu(danmu);
    }

    public List<Danmu> getDanmu(Map<String, Object> params){
        return danmuDao.getDanmus(params);
    }

    //弹幕数据添加到redis--同步异步都可以-单机性能高
    public void addDanmuToRedis(Danmu danmu){
    String key = "danmu-video-"+danmu.getVideoId();

    String value = redisTemplate.opsForValue().get(key);

        List<Danmu> list = new ArrayList<>();

        if(!StringUtils.isNullOrEmpty(value)){
            list = JSONArray.parseArray(value,Danmu.class);
        }

        list.add(danmu);

        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(danmu));
    }





}
