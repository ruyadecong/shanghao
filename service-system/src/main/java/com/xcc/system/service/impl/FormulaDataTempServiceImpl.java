package com.xcc.system.service.impl;

import com.xcc.model.temp.FormulaDataTemp;
import com.xcc.system.mapper.FormulaDataTempMapper;
import com.xcc.system.service.FormulaDataTempService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 配方信息表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-08-26
 */
@Service
public class FormulaDataTempServiceImpl extends ServiceImpl<FormulaDataTempMapper, FormulaDataTemp> implements FormulaDataTempService {

    @Autowired
    FormulaDataTempMapper formulaDataTempMapper;

    //获取formula_data_temp中的数据
    @Override
    public List<FormulaDataTemp> getListOfModify(Integer limit) {

        List<FormulaDataTemp> formulaDataTempList = formulaDataTempMapper.getListOfModify(limit);
        return formulaDataTempList;
    }
}
