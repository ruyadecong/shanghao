package com.xcc.system.service;

import com.xcc.model.technology.CategoryTableRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.technology.TableList;

import java.util.List;

/**
 * <p>
 * 文件类型-数据表关系 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
public interface CategoryTableRelationService extends IService<CategoryTableRelation> {

    //根据文件类型id获取与其关联的表的信息
    List<TableList> getTableLisByCategoryId(Integer id);

    //根据表id以及与之关联的categoryId删除文件类型与表的关联数据
    boolean removeByRelation(Integer id, Integer categoryId);
}
