package com.xcc.system.mapper;

import com.xcc.model.order.OrderSemiFormula;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 订单产品的半成品配方表（隔离于产品的配方表） Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Repository
public interface OrderSemiFormulaMapper extends BaseMapper<OrderSemiFormula> {

}
