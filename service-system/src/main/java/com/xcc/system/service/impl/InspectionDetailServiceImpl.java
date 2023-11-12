package com.xcc.system.service.impl;

import com.xcc.model.entity.InspectionData;
import com.xcc.model.qc.InspectionDetail;
import com.xcc.system.mapper.InspectionDetailMapper;
import com.xcc.system.service.InspectionDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 检验项目汇总表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-07-23
 */
@Service
public class InspectionDetailServiceImpl extends ServiceImpl<InspectionDetailMapper, InspectionDetail> implements InspectionDetailService {

    @Autowired
    InspectionDetailMapper inspectionDetailMapper;

    //根据检验项目的mark获取检验项目
    @Override
    public InspectionData getInspectionByMark(String mark) {

        InspectionData detail = inspectionDetailMapper.getInspectionByMark(mark);

        return detail;
    }
}
