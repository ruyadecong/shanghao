package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.product.ProductData;
import com.xcc.model.product.ProductVo;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.system.mapper.ProductDataMapper;
import com.xcc.system.service.ProductDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品信息表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
@Service
public class ProductDataServiceImpl extends ServiceImpl<ProductDataMapper, ProductData> implements ProductDataService {

    @Autowired
    ProductDataMapper productDataMapper;

    //分页列表
    @Override
    public IPage<ProductData> selectPage(Page<ProductData> pageParam, ProductVo vo) {
        IPage<ProductData> pageData = productDataMapper.selectPage(pageParam,vo);
        return pageData;
    }

    //根据产品编码从product_data表中获取记录
    @Override
    public List<ProductData> getByProductCode(String productCode) {

        List<ProductData> list = productDataMapper.getByProductCode(productCode);
        return list;
    }


    //分页获取产品组件数据
    @Override
    public IPage<ProductData> selectPageForComposition(Page<ProductData> pageParam, ProductVo vo) {
        IPage<ProductData> pageModel = baseMapper.selectPageForComposition(pageParam,vo);

        return pageModel;
    }
}
