package com.xcc.system.mapper;

import com.xcc.model.order.OrderProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * <p>
 * 订单产品数据表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Repository
public interface OrderProductMapper extends BaseMapper<OrderProduct> {

    //根据订单id获取订单的产品
    List<OrderProduct> getProductByOrderId(@Param("orderId") Integer orderId);

    //根据产品编码前几位获取建议列表
    List<String> getSuggestListOfProductCode(@Param("productCode") String productCode);

    //根据产品编码前几位，从order_product表中获取产品编码的建议列表
    List<OrderProduct> getSuggestListOfOrderProductCode(@Param("productCode") String productCode,@Param("orderCode") String orderCode);
}
