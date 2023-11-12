package com.xcc.system.service.impl;

import com.xcc.model.technology.FileCategory;
import com.xcc.system.mapper.FileCategoryMapper;
import com.xcc.system.service.FileCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 文件类别汇总表及与数据库表的关系 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
@Service
public class FileCategoryServiceImpl extends ServiceImpl<FileCategoryMapper, FileCategory> implements FileCategoryService {

    //获取目前表格所关联的文件类型数据
    @Override
    public List<String> getListOfCategoryNameByTableName(String tableName) {
        List<String> list = baseMapper.getListOfCategoryNameByTableName(tableName);
        return list;
    }
}
