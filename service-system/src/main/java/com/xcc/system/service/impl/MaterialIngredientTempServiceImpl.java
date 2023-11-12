package com.xcc.system.service.impl;

import com.xcc.model.qc.UsedIngredient;
import com.xcc.model.temp.MaterialIngredientTemp;
import com.xcc.system.mapper.MaterialIngredientMapper;
import com.xcc.system.mapper.MaterialIngredientTempMapper;
import com.xcc.system.service.MaterialIngredientTempService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 原料成分信息临时表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-08-18
 */
@Service
public class MaterialIngredientTempServiceImpl extends ServiceImpl<MaterialIngredientTempMapper, MaterialIngredientTemp> implements MaterialIngredientTempService {

    @Autowired
    MaterialIngredientTempMapper materialIngredientTempMapper;

    //根据material_code获取其ingredient信息
    @Override
    public List<MaterialIngredientTemp> getByCode(String materialCode) {
        List<MaterialIngredientTemp> ingredientTempList = materialIngredientTempMapper.getByCode(materialCode);

        return ingredientTempList;
    }


    //根据新成分的标准中文名获取相应的建议列表
    //此数据直接通过sql从used_ingredient表中获取
    @Override
    public List<String> getSuggestListOfCname(String newCname) {

        //提前给newCname加上%，用于select语句
        newCname = newCname + "%";
        List<String> stringList = materialIngredientTempMapper.getSuggestListOfCname(newCname);

        return stringList;
    }


    //根据成分标准中文名获取其在2021已使用原料目录中的相应信息
    @Override
    public List<String > getInfoByCname(String newCname) {

        List<String > inciNames = materialIngredientTempMapper.getInfoByCname(newCname);

        return inciNames;
    }
}
