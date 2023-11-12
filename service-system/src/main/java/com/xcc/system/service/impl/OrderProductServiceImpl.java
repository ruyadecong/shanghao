package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.model.order.OrderProduct;
import com.xcc.model.order.OrderProductFormula;
import com.xcc.system.mapper.OrderProductMapper;
import com.xcc.system.service.OrderProductFormulaService;
import com.xcc.system.service.OrderProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单产品数据表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Service
public class OrderProductServiceImpl extends ServiceImpl<OrderProductMapper, OrderProduct> implements OrderProductService {

    @Autowired
    OrderProductMapper orderProductMapper;

    @Autowired
    OrderProductFormulaService orderProductFormulaService;

    @Autowired
    OrderProductService orderProductService;


    //根据订单id获取订单的产品
    @Override
    public List<OrderProduct> getProductByOrderId(Integer orderId) {
        List<OrderProduct> productList = orderProductMapper.getProductByOrderId(orderId);
        return productList;
    }

    //根据产品编码前几位获取建议列表
    @Override
    public List<String> getSuggestListOfProductCode(String productCode) {

        //提前给productCode加上%，用于select语句
        productCode = "%" + productCode + "%";
        List<String> stringList = orderProductMapper.getSuggestListOfProductCode(productCode);
        return stringList;
    }


    //根据产品编码前几位，从order_product表中获取产品编码的建议列表
    @Override
    public List<String> getSuggestListOfOrderProductCode(String productCode,String orderCode) {

        //提前给productCode加上%，用于select语句
        productCode = "%" + productCode + "%";
        List<OrderProduct> list = orderProductMapper.getSuggestListOfOrderProductCode(productCode,orderCode);
        List<String> stringList = new ArrayList<>();
        for (OrderProduct orderProduct : list) {
            stringList.add(orderProduct.getOrderCode() + "$#$" + orderProduct.getProductCode());
        }
        for (String s : stringList) {
            System.out.println("//////////////////////////////////////////////////");
            System.out.println(s);
        }
        return stringList;
    }

    @Override
    public boolean removeProduct(Integer id) {
        QueryWrapper<OrderProductFormula> wrapper = new QueryWrapper<>();
        wrapper.eq("order_product_id",id);
        List<OrderProductFormula> list = orderProductFormulaService.list(wrapper);
        boolean b = orderProductService.removeById(id);
        if(!b){
            return false;
        }else {
            if(list.size() > 0) {
                for (OrderProductFormula orderProductFormula : list) {
                    boolean b1 = orderProductFormulaService.removeFormula(orderProductFormula.getId());
                    b = b & b1;
                }
            }
            return b;
        }
    }
}
