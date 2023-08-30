package com.yupi.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    void doChat() {
        String answer = aiManager.doChat(1654785040361893889L,
                "分析需求：\n"+
                "分析用户的增长情况\n"+
                "原始数据：\n"+
                "日期,用户数\n" +
                "10号,10\n" +
                "11号,11\n" +
                "12号,12");
        System.out.println(answer);
     }
}