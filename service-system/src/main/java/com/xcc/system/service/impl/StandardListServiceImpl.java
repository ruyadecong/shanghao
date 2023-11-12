package com.xcc.system.service.impl;

import com.xcc.model.product.ProductVo;
import com.xcc.model.qc.StandardList;
import com.xcc.system.mapper.StandardListMapper;
import com.xcc.system.service.StandardListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 标准列表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-15
 */
@Service
public class StandardListServiceImpl extends ServiceImpl<StandardListMapper, StandardList> implements StandardListService {

    @Autowired
    StandardListMapper standardListMapper;

    //根据查询条件获取产品标准列表
    @Override
    public List<StandardList> getList(ProductVo vo) {
        List<StandardList> list = standardListMapper.getList(vo);
        return list;
    }


    //获取所有的标准类型用于前端搜索的下拉列表
    @Override
    public List<String> getClassList() {
        List<String> list = standardListMapper.getClassList();
        list.add("所有");
        return list;
    }
}
