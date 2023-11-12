package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.model.technology.FormulaComponent;
import com.xcc.system.mapper.FormulaComponentMapper;
import com.xcc.system.service.FormulaComponentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 配方成分表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-08-25
 */
@Service
public class FormulaComponentServiceImpl extends ServiceImpl<FormulaComponentMapper, FormulaComponent> implements FormulaComponentService {

    @Autowired
    FormulaComponentMapper formulaComponentMapper;

    //根据配方编码获取配方原料数据列表
    @Override
    public List<FormulaComponent> getListByCode(String formulaCode) {

        QueryWrapper<FormulaComponent> wrapper = new QueryWrapper<>();
        wrapper.eq("formula_code",formulaCode);
        wrapper.eq("is_deleted",0);
        wrapper.orderByAsc("rank_number");
        List<FormulaComponent> formulaComponentList = formulaComponentMapper.selectList(wrapper);
        return formulaComponentList;
    }

    //逻辑删除component表中的对应code的数据
    @Override
    public void deleteByCode(String formulaCode) {
        formulaComponentMapper.updateByCodeForDelete(formulaCode);
    }
}
