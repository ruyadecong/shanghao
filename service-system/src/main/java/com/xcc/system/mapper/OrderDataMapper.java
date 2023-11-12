package com.xcc.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.order.OrderData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.order.OrderSearchVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 订单信息 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Repository
public interface OrderDataMapper extends BaseMapper<OrderData> {

    //根据查询条件分页获取订单数据
    IPage<OrderData> selectPage(Page<OrderData> pageParam, @Param("vo") OrderSearchVo vo);

    //获取所有（包括被逻辑删除的）order_code的记录数
    Integer countByOrderCode(@Param("orderCode") String orderCode);
}
