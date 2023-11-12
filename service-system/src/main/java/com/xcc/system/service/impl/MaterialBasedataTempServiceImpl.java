package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.model.temp.MaterialBasedataTemp;
import com.xcc.system.mapper.MaterialBasedataTempMapper;
import com.xcc.system.service.MaterialBasedataTempService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 原料基础信息临时表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-08-18
 */
@Service
public class MaterialBasedataTempServiceImpl extends ServiceImpl<MaterialBasedataTempMapper, MaterialBasedataTemp> implements MaterialBasedataTempService {

    @Autowired
    MaterialBasedataTempMapper materialBasedataTempMapper;

    //获取material_basedata_temp中前limit条的数据
    @Override
    public List<MaterialBasedataTemp> getListOfModify(Integer limit) {
        List<MaterialBasedataTemp> tempList = materialBasedataTempMapper.getListOfModify(limit);
        return tempList;
    }

    //查看temp表中是否已经有了在修改/审核状态的code
    @Override
    public List<MaterialBasedataTemp> getByCode(String materialCode) {

        QueryWrapper<MaterialBasedataTemp> wrapper = new QueryWrapper<>();

        wrapper.eq("material_code",materialCode);

        wrapper.notIn("is_confirm",1);

        wrapper.eq("is_deleted",0);

        List<MaterialBasedataTemp> tempList = baseMapper.selectList(wrapper);

        return tempList;

    }
}
