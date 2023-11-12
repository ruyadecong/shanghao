package com.xcc.system.service.impl;

import com.xcc.model.technology.TableList;
import com.xcc.system.mapper.TableListMapper;
import com.xcc.system.service.TableListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 数据库中表的汇总 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
@Service
public class TableListServiceImpl extends ServiceImpl<TableListMapper, TableList> implements TableListService {

    @Autowired
    TableListMapper tableListMapper;

    //根据所选的categoryId返回供选择的表格列表
    @Override
    public List<String> getUnassignTableName(Integer categoryId) {

        List<String> list = tableListMapper.getUnassignTableName(categoryId);
        return list;
    }
}
