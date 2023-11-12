package com.xcc.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.entity.Material;
import com.xcc.model.qc.MaterialBasedata;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.vo.MaterialBasedataVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 原料基础信息 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-06-03
 */
@Repository
public interface MaterialBasedataMapper extends BaseMapper<MaterialBasedata> {
    //条件分页查询
    IPage<MaterialBasedata> selectPage(Page<MaterialBasedata> pageParam, @Param("vo") MaterialBasedataVo vo);


    //获取可以用做原料编码前三位的字符串列表，这个列表直接通过数据库进行管理，不在前端做管理页面，这里直接通过sql语句来进行
    List<String> getPrefixList();


    //获取prefix对应的所有原料编码
    List<String> getUsedCode(@Param("prefix") String prefix);


    //根据原料编码获取原料成分信息，由于前端修改，暂时不用此方法，详细内容见controller
    /*List<MaterialIngredient>  getIngredientByCode(@Param("code") String code);*/

    //测试用
    MaterialBasedata getByCode(@Param("code") String code);
}
