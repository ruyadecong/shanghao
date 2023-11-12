package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.technology.FormulaData;
import com.xcc.model.technology.FormulaDataVo;
import com.xcc.system.mapper.FormulaDataMapper;
import com.xcc.system.service.FormulaDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品配方信息表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-06-23
 */
@Service
public class FormulaDataServiceImpl extends ServiceImpl<FormulaDataMapper, FormulaData> implements FormulaDataService {

    @Autowired
    FormulaDataMapper formulaDataMapper;

    //获取分页数据

    @Override
    public IPage<FormulaData> selectPage(Page<FormulaData> pageParam, FormulaDataVo vo) {
        //前端加入了根据原料编码进行查询的功能，由于配方中有的是半成品，而半成品又含有原料编码，因此根据原料编码进行查询要处理一下
        //如果直接在sql中进行处理，会比较难，所以这里采用代码生成sql语句的方法进行
        String sql;
        //1 先拿到vo的materialCode值，判断是否为空
        String materialCode = vo.getMaterialCode();
        //如果materialCode为空，则直接执行xml中的sql语句，无需在这里编辑sql语句（因为xml中判断materialCode为null的话，xml中的sql语句则不会加入这个判断条件）
        if(materialCode == null) {
            IPage<FormulaData> pageModel = baseMapper.selectPage(pageParam, vo);
            return pageModel;
        }else{
            //2 如果materialCode不为空，则先要获取含有materialCode的半成品的配方编码
            List<String> semiList = formulaDataMapper.selectSemiByMaterialCode(materialCode);
            System.out.println("*********************************");
            for (String s : semiList) {
                System.out.println(s);
            }
            //2.1 如果semiList长度为0，则直接构造只含有materialCode的sql
            if(semiList.size() == 0){
                sql = "'" + materialCode + "' IN (SELECT material_code FROM formula_component WHERE formula_code = formula_data.`formula_code`)";
            }else{  //2.2 如果semiList长度大于0，则需要构造含有所有semiFormulaCode的sql
                sql = "('" + materialCode + "' IN (SELECT material_code FROM formula_component WHERE formula_code = formula_data.`formula_code`)";
                for (String s : semiList) {
                    sql = sql + " OR '" + s + "' IN (SELECT material_code FROM formula_component WHERE formula_code = formula_data.`formula_code`)";
                }
                //2.3 循环结束后，加上)
                sql = sql + ")";
            }
            //3 将sql放到vo.materialCode中，此时xml中判断vo.materialCode不为null，会自动加入这一句
            vo.setMaterialCode(sql);
            System.out.println("****************************");
            System.out.println(sql);
            IPage<FormulaData> pageModel = baseMapper.selectPage(pageParam, vo);
            return pageModel;
        }
    }

    //根据code获得data表中的数据
    @Override
    public FormulaData getByCode(String formulaCode) {
        QueryWrapper<FormulaData> wrapper = new QueryWrapper<>();
        wrapper.eq("formula_code",formulaCode);
        FormulaData formulaData = formulaDataMapper.selectOne(wrapper);
        return formulaData;
    }

    //根据配方编码前几位获取建议列表
    @Override
    public List<String> getSuggestListOfFormulaCode(String formulaCode) {

        //提前给formulaCode加上%，用于select语句
        formulaCode = "%" + formulaCode + "%";
        List<String> stringList = formulaDataMapper.getSuggestOfFormulaCode(formulaCode);
        return stringList;
    }


    //为前端的配方工艺表获取配方的分页数据，并加上有关配方的一些内容
    @Override
    public IPage<FormulaData> selectPageForCraft(Page<FormulaData> pageParam, FormulaDataVo vo) {
        IPage<FormulaData> pageModel = baseMapper.selectPageForCraft(pageParam,vo);

        return pageModel;
    }

}
