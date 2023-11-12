package com.xcc.system.service;

import com.xcc.model.product.ProductDataHistory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 产品信息表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
public interface ProductDataHistoryService extends IService<ProductDataHistory> {

    //根据productCode获取最新的history数据
    ProductDataHistory getLastOneByProductCode(String productCode);
}
