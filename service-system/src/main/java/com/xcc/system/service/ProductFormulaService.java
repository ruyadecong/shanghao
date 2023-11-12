package com.xcc.system.service;

import com.xcc.model.product.ProductFormula;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 产品配方构成信息表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
public interface ProductFormulaService extends IService<ProductFormula> {

    //根据产品id获取其配方数据
    List<ProductFormula> getFormulaListByProductId(Integer productId);

    //根据产品编码获取其所有的配方数据
    List<ProductFormula> getByProductCode(String productCode);
}
