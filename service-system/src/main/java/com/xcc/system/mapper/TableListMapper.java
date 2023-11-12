package com.xcc.system.mapper;

import com.xcc.model.technology.TableList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 数据库中表的汇总 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
@Repository
public interface TableListMapper extends BaseMapper<TableList> {

    //根据所选的categoryId返回供选择的表格列表
    List<String> getUnassignTableName(@Param("categoryId") Integer categoryId);
}
