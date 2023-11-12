package com.xcc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.order.OrderData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.order.OrderSearchVo;

/**
 * <p>
 * 订单信息 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
public interface OrderDataService extends IService<OrderData> {


    //根据查询条件分页获取订单数据
    IPage<OrderData> selectPage(Page<OrderData> pageParam, OrderSearchVo vo);

    Integer countByOrderCode(String orderCode);

    boolean removeOrder(Integer id);
}
