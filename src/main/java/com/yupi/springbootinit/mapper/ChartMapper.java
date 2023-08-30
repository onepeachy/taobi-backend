package com.yupi.springbootinit.mapper;

import com.yupi.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author taojiaqi
 * @description 针对表【chart(图表信息表)】的数据库操作Mapper
 * @createDate 2023-08-14 22:31:40
 * @Entity com.yupi.springbootinit.model.entity.Chart
 */
public interface ChartMapper extends BaseMapper<Chart> {
    List<Map<String, Object>> queryChartData(String sql);
}




