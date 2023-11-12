package com.xcc.system.service;

import com.xcc.model.order.OrderProduct;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 订单产品数据表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
public interface OrderProductService extends IService<OrderProduct> {

    //根据订单id获取订单的产品
    List<OrderProduct> getProductByOrderId(Integer orderId);

    //根据产品编码前几位，从product_data表中获取产品编码的建议列表
    List<String> getSuggestListOfProductCode(String productCode);

    //根据产品编码前几位，从order_product表中获取产品编码的建议列表
    List<String> getSuggestListOfOrderProductCode(String productCode,String orderCode);

    boolean removeProduct(Integer id);
}
