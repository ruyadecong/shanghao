package com.xcc.system.service;

import com.xcc.model.qc.UsedIngredient;
import com.xcc.model.temp.MaterialIngredientTemp;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 原料成分信息临时表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-08-18
 */
public interface MaterialIngredientTempService extends IService<MaterialIngredientTemp> {

    //根据material_code获取其ingredient信息
    List<MaterialIngredientTemp> getByCode(String materialCode);


    //根据新成分的标准中文名获取相应的建议列表
    List<String> getSuggestListOfCname(String newCname);


    //根据成分标准中文名获取其在2021已使用原料目录中的相应信息
    List<String> getInfoByCname(String newCname);
}
