package com.xcc.system.mapper;

import com.xcc.model.temp.FormulaDataTemp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 配方信息表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-08-26
 */

@Repository
public interface FormulaDataTempMapper extends BaseMapper<FormulaDataTemp> {

    //获取formula_data_temp中的数据
    List<FormulaDataTemp> getListOfModify(@Param("limit") Integer limit);
}
