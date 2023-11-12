package com.xcc.system.mapper;

import com.xcc.model.qc.CallInspect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.vo.MaterialBasedataVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 原料报检信息表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-08-12
 */
@Repository
public interface CallInspectMapper extends BaseMapper<CallInspect> {

    //获取前limit条serial_number的未删除的报检数据的serial_number
    List<String> getListOfSerialNumber(@Param("limit") Integer limit, @Param("vo")MaterialBasedataVo vo);


    //确认报检单打印完成，根据serialNumber修改其is_print为1
    int updatePrintBySerialNumber(@Param("serialNumber") String serialNumber);
}
