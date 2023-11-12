package com.xcc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.product.ProductData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.product.ProductVo;

import java.util.List;

/**
 * <p>
 * 产品信息表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
public interface ProductDataService extends IService<ProductData> {

    //分页列表
    IPage<ProductData> selectPage(Page<ProductData> pageParam, ProductVo vo);

    //根据产品编码从product_data表中获取记录
    List<ProductData> getByProductCode(String productCode);

    //分页获取产品组件数据
    IPage<ProductData> selectPageForComposition(Page<ProductData> pageParam, ProductVo vo);
}
