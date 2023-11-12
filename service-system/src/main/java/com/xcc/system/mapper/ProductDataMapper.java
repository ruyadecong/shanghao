package com.xcc.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.product.ProductData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.product.ProductVo;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.vo.MaterialBasedataVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 产品信息表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
@Repository
public interface ProductDataMapper extends BaseMapper<ProductData> {
    //条件分页查询
    IPage<ProductData> selectPage(Page<ProductData> pageParam, @Param("vo") ProductVo vo);

    //根据产品编码从product_data表中获取记录
    List<ProductData> getByProductCode(@Param("productCode") String productCode);


    //分页获取产品组件数据
    IPage<ProductData> selectPageForComposition(Page<ProductData> pageParam,@Param("vo") ProductVo vo);
}
