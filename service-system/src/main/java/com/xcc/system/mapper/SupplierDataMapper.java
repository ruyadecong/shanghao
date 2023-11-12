package com.xcc.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.purchase.SupplierData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.vo.SupplierVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 供应商信息 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-07-18
 */
@Repository
public interface SupplierDataMapper extends BaseMapper<SupplierData> {

    //获取所有的供应商类别
    List<String> getAllPSort();

    IPage<SupplierData> selectPage(Page<SupplierData> pageParam, @Param("vo") SupplierVo vo);

}
