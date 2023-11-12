package com.xcc.system.service.impl;

import com.xcc.model.order.OrderSemiFormula;
import com.xcc.system.mapper.OrderSemiFormulaMapper;
import com.xcc.system.service.OrderSemiFormulaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单产品的半成品配方表（隔离于产品的配方表） 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Service
public class OrderSemiFormulaServiceImpl extends ServiceImpl<OrderSemiFormulaMapper, OrderSemiFormula> implements OrderSemiFormulaService {

}
