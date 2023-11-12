package com.xcc.system.service;

import com.xcc.model.temp.FormulaComponentTemp;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 配方成分表（临时） 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-08-26
 */
public interface FormulaComponentTempService extends IService<FormulaComponentTemp> {


    //根据配方编码获取配方的原料组成数据
    List<FormulaComponentTemp> getByCode(String formulaCode);
}
