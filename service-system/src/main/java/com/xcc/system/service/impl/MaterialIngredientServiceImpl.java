package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xcc.model.entity.MaterialIngredient;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.system.mapper.MaterialIngredientMapper;
import com.xcc.system.service.MaterialIngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MaterialIngredientServiceImpl extends ServiceImpl<MaterialIngredientMapper,MaterialIngredient> implements MaterialIngredientService {

    @Autowired
    MaterialIngredientMapper materialIngredientMapper;


    //根据原料编码获取原料成分信息
    @Override
    public List<MaterialIngredient> getByCode(String code) {

        List<MaterialIngredient> list = materialIngredientMapper.getByCode(code);
        return list;
    }

    //逻辑删除ingredient表中的对应code的数据
    @Override
    public void deleteByCode(String materialCode) {

        materialIngredientMapper.updateByCodeForDelete(materialCode);

    }



}
