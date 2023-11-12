package com.xcc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.entity.Material;
import com.xcc.model.qc.MaterialBasedata;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.vo.MaterialBasedataVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 原料基础信息 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-06-03
 */
public interface MaterialBasedataService extends IService<MaterialBasedata> {

    //导出原料基本信息到Excel中
    public void batchOutToExcel(List<Integer> ids);
    //条件分页查询
    IPage<MaterialBasedata> selectPage(Page<MaterialBasedata> pageParam, MaterialBasedataVo vo);

    //根据原料编码获取原料的成分信息
/*    List<MaterialIngredient>  getIngredientByCode(String code);*/


    MaterialBasedata getByCode(String maCode);


    //获取可以用做原料编码前三位的字符串列表，这个列表直接通过数据库进行管理，不在前端做管理页面，这里直接通过sql语句来进行
    List<String> getPrefixList();


    //根据prefix获取material_basedata中的material_code的未使用过的后三位列表（001～999）
    List<String> getSuffixByPrefix(String prefix);


    //获取申请修改原料标准的列表，就是material_basedata表中standard_number为0的记录
    List<MaterialBasedata> getListOfApplyForEdit();


    //测试用
    List<MaterialBasedata> getlist();

}
