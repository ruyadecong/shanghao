package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xcc.model.purchase.MaterialPurchase;
import com.xcc.model.qc.CallInspect;
import com.xcc.model.vo.MaterialBasedataVo;
import com.xcc.system.mapper.CallInspectMapper;
import com.xcc.system.service.CallInspectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xcc.system.service.MaterialPurchaseService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 原料报检信息表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-08-12
 */
@Service
public class CallInspectServiceImpl extends ServiceImpl<CallInspectMapper, CallInspect> implements CallInspectService {

    @Autowired
    CallInspectMapper callInspectMapper;

    @Autowired
    MaterialPurchaseService materialPurchaseService;

    //获取前limit条serial_number的未删除的报检数据的serial_number
    @Override
    public List<String> getListOfSerialNumber(Integer limit, MaterialBasedataVo vo) {

        List<String> serialNumberList = callInspectMapper.getListOfSerialNumber(limit,vo);
        return serialNumberList;
    }

    //根据serialNumber查询报检id和原料id，并获取原料的某些数据
    @Override
    public List<Object> getListOfInspect(String serialNumber) {

        DateFormat af = new SimpleDateFormat("yyyy-MM-dd");
        QueryWrapper<CallInspect> wrapper = new QueryWrapper<>();
        wrapper.eq("serial_number",serialNumber);
        wrapper.eq("is_deleted",0);
        List<CallInspect> callInspects = callInspectMapper.selectList(wrapper); //用于保存需要返回的列表
        List<Object> result = new ArrayList<>();
        for (CallInspect callInspect : callInspects) {
            Map<String,Object> item = new HashMap<>();                          //用于保存需要返回的列表的单个元素
            item.put("inspectId",callInspect.getId());                          //存入报检id
            Integer purchaseId = callInspect.getPurchaseId();                   //获得遍历条目的采购数据id
            MaterialPurchase byId = materialPurchaseService.getById(purchaseId);//根据采购数据id获得采购数据
            //保存采购记录中的id
            item.put("purchaseId",purchaseId);
            item.put("materialCode",byId.getMaterialCode());
            item.put("supplierCode",byId.getSupplierCode());
            item.put("materialName",byId.getMaterialName());
            item.put("batchNo",byId.getBatchNo());
            item.put("inDate",af.format(byId.getInDate()));
            item.put("quantityKg",byId.getQuantityKg());
            result.add(item);

        }
        return result;
    }

    //确认报检单打印完成，根据serialNumber修改其is_print为1
    @Override
    public boolean updatePrintBySerialNumber(String serialNumber) {
        QueryWrapper<CallInspect> wrapper = new QueryWrapper<>();
        wrapper.eq("serial_number",serialNumber);
        wrapper.eq("is_deleted",0);
        Integer numOfNeedToUpdate = callInspectMapper.selectCount(wrapper);

        int num = callInspectMapper.updatePrintBySerialNumber(serialNumber);


        return num == numOfNeedToUpdate ? true : false;
    }
}
