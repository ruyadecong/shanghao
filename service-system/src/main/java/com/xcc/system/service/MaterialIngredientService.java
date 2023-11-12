package com.xcc.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.entity.MaterialIngredient;
import com.xcc.model.qc.MaterialBasedata;

import java.util.List;

public interface MaterialIngredientService extends IService<MaterialIngredient> {

    //根据原料编码获取原料成分信息
    List<MaterialIngredient> getByCode(String code);


    //逻辑删除ingredient表中的对应code的数据
    void deleteByCode(String materialCode);


}
