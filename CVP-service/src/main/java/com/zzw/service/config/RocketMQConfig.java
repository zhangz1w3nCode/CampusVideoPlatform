package com.zzw.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.zzw.Entity.userFollowing;
import com.zzw.Entity.userMoment;
import com.zzw.constant.userMomentsConstant;
import com.zzw.service.userFollowingService;
import com.zzw.service.webSocketService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RocketMQConfig {



        @Value("${rocketmq.name.server.address}")
        private String nameServerAddr;


        //Redis

        @Autowired
        private RedisTemplate<String,String> redisTemplate;

        @Autowired
        private userFollowingService userFollowingService;

        //消息 生产者
        @Bean("momentsProducer")
        public DefaultMQProducer momentsProducer() throws MQClientException {
                    DefaultMQProducer Producer = new DefaultMQProducer(userMomentsConstant.GROUP_MOMENS);
                    Producer.setNamesrvAddr(nameServerAddr);
                     Producer.start();
                    return Producer;
                }


        //消息 消费者
         @Bean("momentsConsumer")
        public DefaultMQPushConsumer momentsConsumer() throws Exception {
             DefaultMQPushConsumer Consumer = new DefaultMQPushConsumer(userMomentsConstant.GROUP_MOMENS);
             Consumer.setNamesrvAddr(nameServerAddr);

             Consumer.subscribe(userMomentsConstant.TOPIC_MOMENS,"*");//订阅 主题

             //监听器
             Consumer.registerMessageListener(new MessageListenerConcurrently() {
                 @Override
                 public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                     MessageExt msg = msgs.get(0);

                     if(msg==null){
                         return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                     }

                     String bodyStr = new String(msg.getBody());
                     //把字符串恢复成对象
                    userMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), com.zzw.Entity.userMoment.class);

                    //获取用户ID
                    Long userId = userMoment.getUserId();

                    //获取粉丝列表
                     List<userFollowing> fanList = userFollowingService.getUserFans(userId);



                     for (userFollowing fan : fanList) {

                         String key = "subscribed-"+fan.getUserId();//redis的key

                         String subscribedStrList = redisTemplate.opsForValue().get(key);

                         List<userMoment> subscribedList;

                         if(StringUtils.isNullOrEmpty(subscribedStrList)){
                             subscribedList = new ArrayList<>();
                         }else{
                             subscribedList = JSONArray.parseArray(subscribedStrList, com.zzw.Entity.userMoment.class);
                         }

                        subscribedList.add(userMoment);//

                         redisTemplate.opsForValue().set(key,JSONObject.toJSONString(subscribedList));
                     }
                     return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;//消费成功-返回
                 }
             });


             Consumer.start();
             return Consumer;
    }



    //弹幕-生产者
    @Bean("danmuProducer")
    public DefaultMQProducer danmuProducer() throws MQClientException {
        DefaultMQProducer Producer = new DefaultMQProducer(userMomentsConstant.GROUP_DANMU);
        Producer.setNamesrvAddr(nameServerAddr);
        Producer.start();
        return Producer;
    }


    //弹幕-消费者
    @Bean("danmuConsumer")
    public DefaultMQPushConsumer danmuConsumer() throws Exception {
        DefaultMQPushConsumer Consumer = new DefaultMQPushConsumer(userMomentsConstant.GROUP_DANMU);
        Consumer.setNamesrvAddr(nameServerAddr);

        Consumer.subscribe(userMomentsConstant.TOPIC_DANMU,"*");//订阅 主题

        //监听器
        Consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                MessageExt msg = msgs.get(0);

                String bodyStr = new String(msg.getBody());

                JSONObject jsonObject = JSONObject.parseObject(bodyStr);

                String sessionId = jsonObject.getString("sessionId");

                String message = jsonObject.getString("message");//弹幕

                webSocketService webSocketService = com.zzw.service.webSocketService.WEBSOCKET_MAP.get(sessionId);

                Session session = webSocketService.getSession();

                if(session.isOpen()){
                    try {
                        webSocketService.sendMessage(message);//
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;//消费成功-返回
            }
        });


        Consumer.start();
        return Consumer;
    }





}
