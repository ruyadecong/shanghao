package com.xcc.system.service;

import com.xcc.model.order.OrderProductFormula;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 订单产品的配方表（隔离于产品的配方表） 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
public interface OrderProductFormulaService extends IService<OrderProductFormula> {

    boolean removeFormula(Integer id);

    //给订单产品添加新配方
    Map<String, String> addNewFormula(Integer idForAdd, String newCode, boolean isFromDataBase);
}
