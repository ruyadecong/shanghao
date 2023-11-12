package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.model.temp.FormulaComponentTemp;
import com.xcc.system.mapper.FormulaComponentTempMapper;
import com.xcc.system.service.FormulaComponentTempService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 配方成分表（临时） 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-08-26
 */
@Service
public class FormulaComponentTempServiceImpl extends ServiceImpl<FormulaComponentTempMapper, FormulaComponentTemp> implements FormulaComponentTempService {

    @Autowired
    FormulaComponentTempMapper formulaComponentTempMapper;

    //根据配方编码获取配方的原料组成数据
    @Override
    public List<FormulaComponentTemp> getByCode(String formulaCode) {
        QueryWrapper<FormulaComponentTemp> wrapper = new QueryWrapper<>();
        wrapper.eq("formula_code",formulaCode);
        wrapper.eq("is_deleted",0);
        wrapper.orderByAsc("rank_number");
        List<FormulaComponentTemp> formulaComponentTempList = formulaComponentTempMapper.selectList(wrapper);
        return formulaComponentTempList;
    }
}
