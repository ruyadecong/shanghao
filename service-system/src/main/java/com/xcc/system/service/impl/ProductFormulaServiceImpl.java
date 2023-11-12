package com.xcc.system.service.impl;

import com.xcc.model.product.ProductFormula;
import com.xcc.system.mapper.ProductFormulaMapper;
import com.xcc.system.service.FormulaDataService;
import com.xcc.system.service.ProductFormulaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品配方构成信息表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
@Service
public class ProductFormulaServiceImpl extends ServiceImpl<ProductFormulaMapper, ProductFormula> implements ProductFormulaService {

    @Autowired
    ProductFormulaMapper productFormulaMapper;

    @Autowired
    FormulaDataService formulaDataService;

    //根据产品id获取其配方数据
    @Override
    public List<ProductFormula> getFormulaListByProductId(Integer productId) {
        List<ProductFormula> productFormulaList = productFormulaMapper.getFormulaListByProductId(productId);
        for (ProductFormula productFormula : productFormulaList) {
            productFormula.setColorNumber(formulaDataService.getByCode(productFormula.getFormulaCode()).getColorNumber());
        }

        return productFormulaList;
    }

    //根据产品编码获取其所有的配方数据
    @Override
    public List<ProductFormula> getByProductCode(String productCode) {

        List<ProductFormula> productFormulaList = productFormulaMapper.getByProductCode(productCode);

        return productFormulaList;
    }
}
