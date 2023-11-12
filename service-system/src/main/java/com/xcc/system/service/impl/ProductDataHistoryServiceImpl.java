package com.xcc.system.service.impl;

import com.xcc.model.product.ProductDataHistory;
import com.xcc.system.mapper.ProductDataHistoryMapper;
import com.xcc.system.service.ProductDataHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品信息表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
@Service
public class ProductDataHistoryServiceImpl extends ServiceImpl<ProductDataHistoryMapper, ProductDataHistory> implements ProductDataHistoryService {

    @Autowired
    ProductDataHistoryMapper productDataHistoryMapper;

    //根据productCode获取最新的history数据
    @Override
    public ProductDataHistory getLastOneByProductCode(String productCode) {
        ProductDataHistory lastHistory = productDataHistoryMapper.getLastOneByProductCode(productCode);
        return lastHistory;
    }
}
