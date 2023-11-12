package com.xcc.system.mapper;

import com.xcc.model.entity.InspectionData;
import com.xcc.model.qc.InspectionDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 检验项目汇总表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-07-23
 */


@Repository
public interface InspectionDetailMapper extends BaseMapper<InspectionDetail> {

    //根据检验项目的mark获取检验项目
    InspectionData getInspectionByMark(@Param("mark") String mark);

}
