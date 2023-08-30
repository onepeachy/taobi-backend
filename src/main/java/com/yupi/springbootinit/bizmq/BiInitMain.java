package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class BiInitMain {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("124.222.39.70");
//        factory.setPort(6379);
        factory.setUsername("admin");
        factory.setPassword("admin");

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();


            channel.exchangeDeclare(BiMqConstant.BI_EXCHANGE_NAME, "direct");

            channel.queueDeclare(BiMqConstant.BI_QUEUE_NAME, true, false, false, null);
            channel.queueBind(BiMqConstant.BI_QUEUE_NAME, BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
