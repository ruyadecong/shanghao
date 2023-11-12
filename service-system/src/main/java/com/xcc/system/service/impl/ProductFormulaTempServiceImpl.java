package com.xcc.system.service.impl;

import com.xcc.model.product.ProductFormulaTemp;
import com.xcc.system.mapper.ProductFormulaTempMapper;
import com.xcc.system.service.ProductFormulaTempService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品配方构成信息临时表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
@Service
public class ProductFormulaTempServiceImpl extends ServiceImpl<ProductFormulaTempMapper, ProductFormulaTemp> implements ProductFormulaTempService {

    @Autowired
    ProductFormulaTempMapper productFormulaTempMapper;

    //根据产品编码获取其在产品的配方临时表中的数据
    @Override
    public List<ProductFormulaTemp> getByProductCode(String productCode) {

        List<ProductFormulaTemp> list = productFormulaTempMapper.getByProductCode(productCode);

        return list;
    }
}
