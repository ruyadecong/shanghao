package com.xcc.system.service;

import com.xcc.model.entity.MaterialIngredient;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.temp.MaterialBasedataTemp;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 原料基础信息临时表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-08-18
 */
public interface MaterialBasedataTempService extends IService<MaterialBasedataTemp> {

    //获取material_basedata_temp中前limit条的数据
    List<MaterialBasedataTemp> getListOfModify(Integer limit);

    //查看temp表中是否已经有了在修改/审核状态的code
    List<MaterialBasedataTemp> getByCode(String materialCode);
}
