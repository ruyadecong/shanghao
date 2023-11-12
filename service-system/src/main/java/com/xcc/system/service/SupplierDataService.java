package com.xcc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.purchase.SupplierData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.vo.SupplierVo;

import java.util.List;

/**
 * <p>
 * 供应商信息 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-07-18
 */
public interface SupplierDataService extends IService<SupplierData> {

    SupplierData getByCode(String suCode);


    List<String> getAllSort();

    //条件分页查询
    IPage<SupplierData> selectPage(Page<SupplierData> pageParam, SupplierVo vo);
}
