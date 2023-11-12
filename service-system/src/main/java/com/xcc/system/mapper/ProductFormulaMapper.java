package com.xcc.system.mapper;

import com.xcc.model.product.ProductFormula;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 产品配方构成信息表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
@Repository
public interface ProductFormulaMapper extends BaseMapper<ProductFormula> {

    //根据产品id获取其配方数据
    List<ProductFormula> getFormulaListByProductId(@Param("productId") Integer productId);

    //根据产品编码获取其所有的配方数据
    List<ProductFormula> getByProductCode(@Param("productCode") String productCode);
}
