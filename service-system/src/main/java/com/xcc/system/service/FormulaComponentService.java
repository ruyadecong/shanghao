package com.xcc.system.service;

import com.xcc.model.technology.FormulaComponent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 配方成分表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-08-25
 */
public interface FormulaComponentService extends IService<FormulaComponent> {


    //根据配方编码获取配方原料数据列表
    List<FormulaComponent> getListByCode(String formulaCode);


    //逻辑删除component表中的对应code的数据
    void deleteByCode(String formulaCode);
}
