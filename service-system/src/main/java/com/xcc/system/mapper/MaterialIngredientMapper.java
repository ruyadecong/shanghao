package com.xcc.system.mapper;

import com.xcc.model.entity.MaterialIngredient;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 备案用的原料信息 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-07-26
 */

@Repository
public interface MaterialIngredientMapper extends BaseMapper<MaterialIngredient> {

    //根据原料编码获取原料成分信息
    List<MaterialIngredient> getByCode(@Param("code") String code);

    //使用update方法逻辑删除ingredient表中的对应code的数据
    boolean updateByCodeForDelete(@Param("code") String materialCode);

    //测试用
    List<String> getCodeList();
}
