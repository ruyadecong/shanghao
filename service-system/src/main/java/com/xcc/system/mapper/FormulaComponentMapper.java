package com.xcc.system.mapper;

import com.xcc.model.technology.FormulaComponent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 配方成分表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-08-25
 */
@Repository
public interface FormulaComponentMapper extends BaseMapper<FormulaComponent> {

    //逻辑删除component表中的对应code的数据，通过update的方法
    void updateByCodeForDelete(@Param("code") String formulaCode);
}
