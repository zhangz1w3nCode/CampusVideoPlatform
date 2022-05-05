package com.zzw.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzw.Entity.userMoment;
import com.zzw.constant.userMomentsConstant;
import com.zzw.dao.userMomentDao;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzw.service.utils.rocketMQUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class userMomentService {



    @Autowired
    private userMomentDao userMomentDao;

    @Autowired
     ApplicationContext applicationContext;//上下文 - 获取所有配置和bean- 获取rocketMq配置类

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public void addUserMoments(userMoment userMoment) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {

        userMoment.setCreateTime(new Date());

        //把userMoment的信息存到 数据库表
        userMomentDao.addUserMoments(userMoment);
        System.out.println("-------------**");
        //添加完动态后 推送消息到 rocketmq


        //获取消息生产者
        DefaultMQProducer producer = (DefaultMQProducer)applicationContext.getBean("momentsProducer");


        //指定 发送的消息、消息主题、具体消息
        Message msg = new Message(userMomentsConstant.TOPIC_MOMENS, JSONObject.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));

        //发送消息
        rocketMQUtils.syncSendMsg(producer,msg);
    }

    public List<userMoment> getUserSubscribedMoments(Long userID) {
        String key = "subscribed-"+userID;//redis的key red去拿
        String listStr = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(listStr,userMoment.class);
    }
}
