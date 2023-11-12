package com.xcc.system.service;

import com.xcc.model.order.OrderFormulaComponent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 订单的配方表（隔离于配方表） 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
public interface OrderFormulaComponentService extends IService<OrderFormulaComponent> {

    boolean removeFormulaComponent(Integer id);

    //给订单产品添加新组分
    Map<String, String> addNewFormulaComponent(Integer idForAdd, String newCode, boolean isFromDataBase, int classMark);

    public Map<String, String> addNewSemiFormula(Integer idForAdd, String newCode);
}
