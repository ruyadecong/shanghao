package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.model.technology.CategoryTableRelation;
import com.xcc.model.technology.TableList;
import com.xcc.system.mapper.CategoryTableRelationMapper;
import com.xcc.system.mapper.TableListMapper;
import com.xcc.system.service.CategoryTableRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 文件类型-数据表关系 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
@Service
public class CategoryTableRelationServiceImpl extends ServiceImpl<CategoryTableRelationMapper, CategoryTableRelation> implements CategoryTableRelationService {

    @Autowired
    CategoryTableRelationMapper categoryTableRelationMapper;

    @Autowired
    TableListMapper tableListMapper;
    //根据文件类型id获取与其关联的表的信息
    @Override
    public List<TableList> getTableLisByCategoryId(Integer id) {
        QueryWrapper<CategoryTableRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id",id);
        List<CategoryTableRelation> categoryTableRelations = categoryTableRelationMapper.selectList(wrapper);
        if(categoryTableRelations.size() == 0){
            return null;
        }else {
            List<TableList> tableList = new ArrayList<>();
            for (CategoryTableRelation categoryTableRelation : categoryTableRelations) {
                TableList table = tableListMapper.selectById(categoryTableRelation.getTableId());
                tableList.add(table);
            }
            return tableList;
        }
    }

    //根据表id以及与之关联的categoryId删除文件类型与表的关联数据
    @Override
    public boolean removeByRelation(Integer id, Integer categoryId) {
        QueryWrapper<CategoryTableRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("table_id",id);
        wrapper.eq("category_id",categoryId);
        List<CategoryTableRelation> list = categoryTableRelationMapper.selectList(wrapper);
        if(list.size() == 0){
            return true;
        }else{
            List<Integer> idList = new ArrayList<>();
            for (CategoryTableRelation categoryTableRelation : list) {
                idList.add(categoryTableRelation.getId());
            }
            int i = categoryTableRelationMapper.deleteBatchIds(idList);
            if(i>0){
                return true;
            }else{
                return false;
            }
        }

    }
}
