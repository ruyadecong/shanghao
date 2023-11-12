package com.xcc.system.service;

import com.xcc.model.product.ProductVo;
import com.xcc.model.qc.StandardList;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 标准列表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-15
 */
public interface StandardListService extends IService<StandardList> {

    //根据查询条件获取产品标准列表
    List<StandardList> getList(ProductVo vo);

    //获取所有的标准类型用于前端搜索的下拉列表
    List<String> getClassList();
}
