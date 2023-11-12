package com.xcc.system.mapper;

import com.xcc.model.product.ProductFormulaTemp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 产品配方构成信息临时表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
@Repository
public interface ProductFormulaTempMapper extends BaseMapper<ProductFormulaTemp> {

    //根据产品编码获取其在产品的配方临时表中的数据
    List<ProductFormulaTemp> getByProductCode(@Param("productCode") String productCode);
}
