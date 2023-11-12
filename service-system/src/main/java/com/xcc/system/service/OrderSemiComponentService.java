package com.xcc.system.service;

import com.xcc.model.order.OrderSemiComponent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 订单的配方表（隔离于配方表） 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-20
 */
public interface OrderSemiComponentService extends IService<OrderSemiComponent> {

    boolean removeSemiComponent(Integer id);

    //给订单产品的中间料添加新组分
    Map<String, String> addNewSemiComponent(Integer idForAdd, String newCode, boolean isFromDataBase, int classMark);

    public Map<String, String> addNewSemiSemiComponent(Integer idForAdd, String newCode);
}
