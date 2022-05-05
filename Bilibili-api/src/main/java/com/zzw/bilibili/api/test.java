package com.zzw.bilibili.api;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.zzw.Entity.Danmu.Danmu;
import com.zzw.service.danmuService;
import com.zzw.service.utils.rocketMQUtils;
import com.zzw.service.utils.tokenUtil;
import com.zzw.service.webSocketService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zzw.constant.userMomentsConstant.TOPIC_DANMU;

public class test {

    public static void main(String[] args) {
        String s="01",t="0011";

        System.out.println(Integer.parseInt(t));


    }

}
