package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 *用于创建测试用的交换机和消息队列
 */
public class CodeMain {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("124.222.39.70");
//        factory.setPort(6379);
        factory.setUsername("admin");
        factory.setPassword("admin");

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();

            String EXCHANGE_NAME = "code_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String QUEUE_NAME = "code_queue";
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "my_routingKey");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
