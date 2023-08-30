package com.yupi.springbootinit.bizmq;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.impl.ChartService;
import com.yupi.springbootinit.utils.ExcelUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    //指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void recieveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表为空");
        }

//        先修改图表状态为“执行中”，执行成功后，修改为“已完成”，执行失败后状态修改为“失败”，记录任务失败信息
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            //消息拒绝
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表执行中状态失败1");
            return;
        }

        String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, buildUserInput(chart));
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "AI 生成错误2");
            return;
        }

        String genChart = splits[1].trim();
        String genResult = splits[2].trim();

        Chart updateChart2 = new Chart();
        updateChart2.setId(chart.getId());
        updateChart2.setStatus("succeed");
        updateChart2.setGenChart(genChart);
        updateChart2.setGenResult(genResult);
        boolean updateResult = chartService.updateById(updateChart2);
        if (!updateResult) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表执行中状态失败3");
            return;
        }

        //消息确认
        channel.basicAck(deliveryTag, false);
    }

    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String name = chart.getName();
        String csvData = chart.getChartData();

        //用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append(goal);
        if (StringUtils.isNotBlank(chartType)) {
            userInput.append(".请使用" + chartType);
        }
        if (StringUtils.isNotBlank(name)) {
            userInput.append(".图表标题为：" + name);
        }
        userInput.append("\n");
        //压缩后的数据

        userInput.append("原始数据:").append(csvData);

        return userInput.toString();
    }

    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setStatus("failed");
        updateChart.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChart);
        if (!updateResult) {
            log.error("更新图表状态为失败的失败:" + chartId + ".错误信息:" + execMessage);
        }
    }
}
