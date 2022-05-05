package com.zzw.service.utils;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

public class rocketMQUtils {


    //发送方法-同步
    public static  void syncSendMsg(DefaultMQProducer producer, Message msg) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        SendResult result = producer.send(msg);
        System.out.println(result);
    };

    //发送方法-异步
    public static  void asyncSendMsg(DefaultMQProducer producer, Message msg) throws Exception {

        //发送2次
        int count = 1000;

        CountDownLatch2 countDownLatch = new CountDownLatch2(count);

        for(int i=0;i<count;i++){

            //发消息
            producer.send(msg, new SendCallback() {

                @Override
                public void onSuccess(SendResult sendResult) {//成功发送消息
                    countDownLatch.countDown();
                    System.out.println(sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable throwable) {//发送消息-异常
                    countDownLatch.countDown();
                    System.out.println("发送消息-异常:"+throwable);
                }
            });

        }



        countDownLatch.await(1, TimeUnit.SECONDS);

    };



}
