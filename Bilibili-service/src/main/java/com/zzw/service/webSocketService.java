package com.zzw.service;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.zzw.Entity.Danmu.Danmu;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.zzw.service.utils.tokenUtil;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.zzw.service.utils.rocketMQUtils;

import static com.zzw.constant.userMomentsConstant.TOPIC_DANMU;

@Component
@ServerEndpoint("/imserver/{token}")
public class webSocketService {

    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    public static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    //每个客户端都需要一个webSocketService-因为

    //ConcurrentHashMap
    public static final ConcurrentHashMap<String,webSocketService> WEBSOCKET_MAP = new ConcurrentHashMap();

    private Session session;

    private String sessionId;

    private Long userId;

    public Session getSession() {
        return session;
    }

    public String getSessionId() {
        return sessionId;
    }

    private static ApplicationContext Application_Context;

    public static void setApplicationContext(ApplicationContext applicationContext){
        webSocketService.Application_Context =applicationContext;
    }

    /*
       连接成功建立 调用的方法
     */
    @OnOpen //加注解才有效------
    public void openConnection(Session session, @PathParam("token") String token){

        try {
            this.userId = tokenUtil.verifyToken(token);
        }catch (Exception e){

        }


        this.sessionId = session.getId();
        this.session = session;

        if(WEBSOCKET_MAP.containsKey(sessionId)){
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId,this);
        }else{
            WEBSOCKET_MAP.put(sessionId,this);
            ONLINE_COUNT.getAndIncrement();
        }

        logger.info("用户连接成功："+sessionId+",当前在线人数:"+ONLINE_COUNT.get());

        try{
            this.sendMessage("--------连接成功！------");
        }catch (Exception e){
            logger.error("连接异常");
        }

    }






    //发送信息方法----发送信息到服务器
    public void sendMessage(String message) throws Exception {
        this.session.getBasicRemote().sendText(message); //当前用户 发送信息message 给服务器
    }







    @OnClose//断开连接
    public void closeConnection(Session session){

        if(WEBSOCKET_MAP.containsKey(sessionId)){
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }

        logger.info("用户退出成功："+sessionId+",当前在线人数:"+ONLINE_COUNT.get());
    }









    //服务器 收到客户端信息 后 执行的方法
    @OnMessage
    public void OnMessage(String message){ //前端调用 OnMessage方法 发送弹幕 message


        logger.info("用户信息："+sessionId+",报文："+message);


        if(!StringUtils.isNullOrEmpty(message)){
            try{

                //信息群发给连接的人--优化(如果发送消息大该怎么办?)
                for(Map.Entry<String,webSocketService> entry:WEBSOCKET_MAP.entrySet() ){


                    //拿到 每一个连接到服务器的 用户 webSocketService
                    webSocketService webSocketService = entry.getValue();



                 /*
                    //优化前
                    if(webSocketService.session.isOpen()){
                        webSocketService.sendMessage(message);//给每个连接的人发送一个消息   每个连接的客户端 发送弹幕 服务器
                    }
                    //这里的发送消息由 rocketMq去实现
                  */

                 //优化后:先把弹幕发送到 mq 然后 mq去做削峰  再去用mq去 调用webSocketService.sendMessage(message)发送消息

                //要发送的消息加入到MQ中

                    //获取弹幕生产者
                    DefaultMQProducer danmuProducer = (DefaultMQProducer)Application_Context.getBean("danmuProducer");

                    //构建 发送的信息
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("message",message);

                    jsonObject.put("sessionId",webSocketService.getSessionId());

                    byte[] info = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);

                    Message mes = new Message(TOPIC_DANMU,info);


                    //异步-发送信息到MQ
                    rocketMQUtils.asyncSendMsg(danmuProducer,mes);
                }

                //弹幕持久化
                if(this.userId!=null){
                    Danmu danmu = JSONObject.parseObject(message,Danmu.class);//message转换成danmu实体类
                    danmu.setUserId(userId);
                    danmu.setCreateTime(new Date());

                    danmuService danmuService = (danmuService)Application_Context.getBean("danmuService");
                    //存到mysql
                    danmuService.asycAddDanmu(danmu);

                    //!!!!!!还可以用mq去优化 -如果保存的数据量大  持久化弹幕请求发送到mq再 在mq中调用 方法 去保存到数据库去持久化!!!!!

                    //存到redis
                    danmuService.addDanmuToRedis(danmu);
                }


            }catch (Exception e){
                logger.error("弹幕接收出现问题!");
                e.printStackTrace();
            }
        }
    }

    @OnError//发送错误时候的情况
    public void OnError(Throwable error){

    }







    //定时任务 - 在主程序中(Blibili_App.class)@EnableScheduling要开启
    @Scheduled(fixedRate = 5000)//fixedRate = 5000毫秒------每隔5s执行一次定时任务
    public void noticeOnlineCount() throws Exception{
        //信息群发给连接的人--优化(如果发送消息大该怎么办?)
        for(Map.Entry<String,webSocketService> entry:WEBSOCKET_MAP.entrySet() ){

            webSocketService webSocketService = entry.getValue();

            if(webSocketService.getSession().isOpen()){


                JSONObject jsonObject = new JSONObject();

                jsonObject.put("onlineCount",ONLINE_COUNT.get());

                jsonObject.put("msg:","当前在线的人数为: "+ONLINE_COUNT.get());

                String mess =jsonObject.toJSONString();

                webSocketService.sendMessage(mess);

            }



        }
    }


}
