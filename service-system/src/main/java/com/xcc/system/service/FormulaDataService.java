package com.xcc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.technology.FormulaData;
import com.xcc.model.technology.FormulaDataVo;

import java.util.List;

/**
 * <p>
 * 产品配方信息表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-06-23
 */
public interface FormulaDataService extends IService<FormulaData> {

    //获取分页数据
    IPage<FormulaData> selectPage(Page<FormulaData> pageParam, FormulaDataVo vo);

    //根据code获得data表中的数据
    FormulaData getByCode(String formulaCode);

    //根据配方编码前几位获取建议列表
    List<String> getSuggestListOfFormulaCode(String formulaCode);

    IPage<FormulaData> selectPageForCraft(Page<FormulaData> pageParam, FormulaDataVo vo);
}
