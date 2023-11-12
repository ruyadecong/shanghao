package com.xcc.system.service;

import com.xcc.model.entity.InspectionData;
import com.xcc.model.qc.InspectionDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 检验项目汇总表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-07-23
 */
public interface InspectionDetailService extends IService<InspectionDetail> {

    //根据检验项目的mark获取检验项目
    InspectionData getInspectionByMark(String mark);
}
