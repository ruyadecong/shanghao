package com.xcc.system.service;

import com.xcc.model.temp.FormulaDataTemp;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 配方信息表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-08-26
 */
public interface FormulaDataTempService extends IService<FormulaDataTemp> {


    //获取formula_data_temp中的数据（前100条）
    List<FormulaDataTemp> getListOfModify(Integer limit);
}
