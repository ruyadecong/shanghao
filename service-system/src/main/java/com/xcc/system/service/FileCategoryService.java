package com.xcc.system.service;

import com.xcc.model.technology.FileCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 文件类别汇总表及与数据库表的关系 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
public interface FileCategoryService extends IService<FileCategory> {

    //获取目前表格所关联的文件类型数据
    List<String> getListOfCategoryNameByTableName(String tableName);
}
