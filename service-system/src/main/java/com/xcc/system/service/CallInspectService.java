package com.xcc.system.service;

import com.xcc.model.qc.CallInspect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.vo.MaterialBasedataVo;

import java.util.List;

/**
 * <p>
 * 原料报检信息表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-08-12
 */
public interface CallInspectService extends IService<CallInspect> {

    //获取前limit条serial_number的未删除的报检数据的serial_number
    List<String> getListOfSerialNumber(Integer limit, MaterialBasedataVo vo);


    //根据serialNumber查询报检id和原料id，并获取原料的某些数据
    List<Object> getListOfInspect(String serialNumber);

    //确认报检单打印完成，根据serialNumber修改其is_print为1
    boolean updatePrintBySerialNumber(String serialNumber);
}
