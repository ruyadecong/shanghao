package com.xcc.system.mapper;

import com.xcc.model.order.OrderFormulaComponent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 订单的配方表（隔离于配方表） Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Repository
public interface OrderFormulaComponentMapper extends BaseMapper<OrderFormulaComponent> {

}
