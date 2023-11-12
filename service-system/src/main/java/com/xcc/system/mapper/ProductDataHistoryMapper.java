package com.xcc.system.mapper;

import com.xcc.model.product.ProductDataHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 产品信息表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
@Repository
public interface ProductDataHistoryMapper extends BaseMapper<ProductDataHistory> {

    //getLastOneByProductCode
    ProductDataHistory getLastOneByProductCode(@Param("productCode") String productCode);
}
