package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

public class SingleProducer {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("124.222.39.70");
//        factory.setPort(6379);
        factory.setUsername("admin");
        factory.setPassword("admin");

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "hello first massage";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("[x] 发送了：" + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
