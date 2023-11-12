package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.purchase.SupplierData;
import com.xcc.model.qc.Equipment;
import com.xcc.model.vo.SupplierVo;
import com.xcc.system.mapper.SupplierDataMapper;
import com.xcc.system.service.SupplierDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 供应商信息 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-07-18
 */
@Service
public class SupplierDataServiceImpl extends ServiceImpl<SupplierDataMapper, SupplierData> implements SupplierDataService {

    @Autowired
    SupplierDataMapper supplierDataMapper;

    @Override
    public SupplierData getByCode(String suCode) {

        QueryWrapper<SupplierData> wrapper = new QueryWrapper<>();
        wrapper.eq("supplier_code",suCode);
        SupplierData supplier = supplierDataMapper.selectOne(wrapper);

        return supplier;
    }

    @Override
    public List<String> getAllSort() {
        List<String> sort = new ArrayList<>();

        sort = supplierDataMapper.getAllPSort();

        return sort;
    }

    @Override
    public IPage<SupplierData> selectPage(Page<SupplierData> pageParam, SupplierVo vo) {

        IPage<SupplierData> pageModel = baseMapper.selectPage(pageParam,vo);

        return pageModel;

    }
}
