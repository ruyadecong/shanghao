package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.order.OrderData;
import com.xcc.model.order.OrderProduct;
import com.xcc.model.order.OrderSearchVo;
import com.xcc.model.qc.Equipment;
import com.xcc.system.mapper.OrderDataMapper;
import com.xcc.system.service.OrderDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xcc.system.service.OrderProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 订单信息 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Service
public class OrderDataServiceImpl extends ServiceImpl<OrderDataMapper, OrderData> implements OrderDataService {

    @Autowired
    OrderDataMapper orderDataMapper;

    @Autowired
    OrderDataService orderDataService;

    @Autowired
    OrderProductService orderProductService;

    //根据查询条件分页获取订单数据
    @Override
    public IPage<OrderData> selectPage(Page<OrderData> pageParam, OrderSearchVo vo) {

        IPage<OrderData> pageModel = baseMapper.selectPage(pageParam,vo);
        return pageModel;

    }

    //由于order_code为unique，所以存在逻辑删除了的情况，此时list.size() == 0，但仍然不能往数据库中添加数据
    //获取所有（包括被逻辑删除的）order_code的记录数
    @Override
    public Integer countByOrderCode(String orderCode) {
        Integer num = orderDataMapper.countByOrderCode(orderCode);
        return num;
    }

    @Override
    public boolean removeOrder(Integer id) {
        List<OrderProduct> productList = orderProductService.getProductByOrderId(id);
        boolean b = orderDataService.removeById(id);
        if(!b) {
            return false;
        }else{
            if(productList.size() > 0) {
                for (OrderProduct orderProduct : productList) {
                    boolean b1 = orderProductService.removeProduct(orderProduct.getId());
                    b = b & b1;
                }
            }
            return b;
        }
    }
}
