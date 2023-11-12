package com.xcc.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.technology.FormulaData;
import com.xcc.model.technology.FormulaDataVo;
import com.xcc.model.temp.FormulaDataTemp;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 产品配方信息表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-06-23
 */
@Repository
public interface FormulaDataMapper extends BaseMapper<FormulaData> {
    //获取分页数据
    IPage<FormulaData> selectPage(Page<FormulaData> pageParam,@Param("vo") FormulaDataVo vo);

    //获取含有特定materialCode的半成品编码
    List<String> selectSemiByMaterialCode(@Param("code") String code);

    //提前给newCname加上%，用于select语句
    List<String> getSuggestOfFormulaCode(@Param("formulaCode") String formulaCode);

    //为前端的配方工艺页面获取配方的分页数据
    IPage<FormulaData> selectPageForCraft(Page<FormulaData> pageParam,@Param("vo") FormulaDataVo vo);
}
