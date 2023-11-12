package com.xcc.system.service;

import com.xcc.model.technology.TableList;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 数据库中表的汇总 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
public interface TableListService extends IService<TableList> {

    //根据所选的categoryId返回供选择的表格列表
    List<String> getUnassignTableName(Integer categoryId);
}
