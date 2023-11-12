package com.xcc.system.mapper;

import com.xcc.model.technology.FileCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 文件类别汇总表及与数据库表的关系 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
@Repository
public interface FileCategoryMapper extends BaseMapper<FileCategory> {

    //获取目前表格所关联的文件类型数据
    List<String> getListOfCategoryNameByTableName(@Param("tableName") String tableName);
}
