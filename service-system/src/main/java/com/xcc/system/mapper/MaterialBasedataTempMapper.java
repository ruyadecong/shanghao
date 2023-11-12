package com.xcc.system.mapper;

import com.xcc.model.temp.MaterialBasedataTemp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 原料基础信息临时表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-08-18
 */
@Repository
public interface MaterialBasedataTempMapper extends BaseMapper<MaterialBasedataTemp> {

    //获取material_database_temp中前limit条的数据
    List<MaterialBasedataTemp> getListOfModify(@Param("limit") Integer limit);


}
