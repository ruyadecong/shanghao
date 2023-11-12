package com.xcc.system.mapper;

import com.xcc.model.product.ProductVo;
import com.xcc.model.qc.StandardList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 标准列表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-15
 */
@Repository
public interface StandardListMapper extends BaseMapper<StandardList> {

    //根据查询条件获取产品标准列表
    List<StandardList> getList(@Param("vo") ProductVo vo);

    //获取所有的标准类型用于前端搜索的下拉列表
    List<String> getClassList();
}
