package com.xcc.system.service;

import com.xcc.model.product.ProductFormulaTemp;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 产品配方构成信息临时表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
public interface ProductFormulaTempService extends IService<ProductFormulaTemp> {

    //根据产品编码获取其在产品的配方临时表中的数据
    List<ProductFormulaTemp> getByProductCode(String productCode);
}
