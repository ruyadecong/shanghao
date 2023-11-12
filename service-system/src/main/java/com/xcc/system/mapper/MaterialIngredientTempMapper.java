package com.xcc.system.mapper;

import com.xcc.model.qc.UsedIngredient;
import com.xcc.model.temp.MaterialIngredientTemp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 原料成分信息临时表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-08-18
 */
@Repository
public interface MaterialIngredientTempMapper extends BaseMapper<MaterialIngredientTemp> {

    //根据material_code获取其ingredient信息
    List<MaterialIngredientTemp> getByCode(@Param("code") String materialCode);


    //根据新成分的标准中文名获取相应的建议列表
    //此数据直接通过sql从used_ingredient表中获取
    List<String> getSuggestListOfCname(@Param("cname") String newCname);

    //根据成分标准中文名获取其在2021已使用原料目录中的相应信息
    List<String> getInfoByCname(@Param("cname") String newCname);
}
