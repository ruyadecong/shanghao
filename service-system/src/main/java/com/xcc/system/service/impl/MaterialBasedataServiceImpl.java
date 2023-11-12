package com.xcc.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.entity.Material;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.vo.MaterialBasedataVo;
import com.xcc.system.mapper.MaterialBasedataMapper;
import com.xcc.system.service.MaterialBasedataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 原料基础信息 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-06-03
 */
@Service
public class MaterialBasedataServiceImpl extends ServiceImpl<MaterialBasedataMapper, MaterialBasedata> implements MaterialBasedataService {


    @Autowired
    MaterialBasedataMapper materialBasedataMapper;

    //导出原料基本信息到Excel中
    @Override
    public void batchOutToExcel(List<Integer> ids) {

        List<MaterialBasedata> list = new ArrayList<>();

        list = materialBasedataMapper.selectBatchIds(ids);

        EasyExcel.write("C:\\Users\\xucon\\Desktop\\原料基础信息.xlsm")
                .sheet("导出的数据")
                .doWrite(list);

    }


    //条件分页查询
    @Override
    public IPage<MaterialBasedata> selectPage(Page<MaterialBasedata> pageParam, MaterialBasedataVo vo) {

        IPage<MaterialBasedata> pageData = baseMapper.selectPage(pageParam,vo);

        return pageData;
    }


    //根据原料代码获取原料成分信息
/*    @Override
    public List<MaterialIngredient> getIngredientByCode(String code) {

        List<MaterialIngredient> ingredientList = materialBasedataMapper.getIngredientByCode(code);

        return ingredientList;
    }*/



    //根据原料代码获取原料信息
    @Override
    public MaterialBasedata getByCode(String maCode) {

        QueryWrapper<MaterialBasedata> wrapper = new QueryWrapper<>();

        wrapper.eq("material_code",maCode);

        MaterialBasedata material =materialBasedataMapper.selectOne(wrapper);

        return material;
    }

    //获取可以用做原料编码前三位的字符串列表，这个列表直接通过数据库进行管理，不在前端做管理页面，这里直接通过sql语句来进行
    @Override
    public List<String> getPrefixList() {

        List<String> prefixList = materialBasedataMapper.getPrefixList();

        return prefixList;
    }


    //根据prefix获取material_basedata中的material_code的未使用过的后三位列表（001～999）
    @Override
    public List<String> getSuffixByPrefix(String prefix) {
        //编制001～999字符串的列表
        List<String> suffixList = new ArrayList<>();
        for (int i = 1; i < 1000; i++) {
            if(i<10){
                suffixList.add("00" + i);
            } else if (i<100) {
                suffixList.add("0" + i);
            }else{
                suffixList.add(String.valueOf(i));
            }
        }
        //根据prefix获取已经使用过的suffix
        //先获取prefix对应的所有原料编码
        List<String> usedCodeList = new ArrayList<>();
        if(prefix.length() == 3) {
            prefix = prefix + "-%";     //sql中的like条件的字符串
            usedCodeList = materialBasedataMapper.getUsedCode(prefix);
        }
        //将suffixList中含有usedCodeList后三位的数据移除


        for (String s : usedCodeList) {
            suffixList.remove(s.substring(4, 7));
        }

        return suffixList;
    }



    //获取申请修改原料标准的列表，就是material_basedata表中standard_number为0的记录
    @Override
    public List<MaterialBasedata> getListOfApplyForEdit() {
        QueryWrapper<MaterialBasedata> wrapper = new QueryWrapper<>();
        wrapper.eq("standard_number",0);
        List<MaterialBasedata> materialBasedataList = materialBasedataMapper.selectList(wrapper);
        return materialBasedataList;
    }


    //测试用
    @Override
    public List<MaterialBasedata> getlist() {

        QueryWrapper<MaterialBasedata> wrapper = new QueryWrapper<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        list.add(10);
        list.add(11);
        list.add(12);
        list.add(13);
        list.add(14);
        list.add(15);

        wrapper.in("standard_number",list);
        List<MaterialBasedata> materialBasedataList = materialBasedataMapper.selectList(wrapper);
        return materialBasedataList;
    }



}
