package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.entity.Material;
import com.xcc.model.entity.MaterialIngredient;
import com.xcc.model.entity.StandardData;
import com.xcc.model.qc.MaterialStandard;
import com.xcc.model.vo.MaterialStandardVo;
import com.xcc.system.mapper.MaterialStandardMapper;
import com.xcc.system.service.MaterialStandardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * <p>
 * 原料标准信息 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-06-07
 */
@Service
public class MaterialStandardServiceImpl extends ServiceImpl<MaterialStandardMapper, MaterialStandard> implements MaterialStandardService {


    @Autowired
    MaterialStandardMapper materialStandardMapper;

    //根据ids批量获取原料标准
    @Override
    public List<MaterialStandard> getByIds(List<Integer> ids) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        List<MaterialStandard> materialStandards = baseMapper.selectBatchIds(ids);

        return materialStandards;
    }


    //条件分页查询
/*    @Override
    public IPage<MaterialStandard> selectPage(Page<MaterialStandard> pageParam, MaterialStandardVo vo) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        IPage<MaterialStandard> pageData = baseMapper.selectPage(pageParam,vo);

        return pageData;
    }*/

    @Override
    public IPage<Material> selectPage(Page<Material> pageParam, MaterialStandardVo vo) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        IPage<Material> pageData = baseMapper.selectPage(pageParam,vo);

        return pageData;

    }

    //条件分页查询
/*    @Override
    public IPage<MaterialStandard> selectPage(Page<MaterialStandard> pageParam, MaterialStandardVo vo) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        IPage<MaterialStandard> pageData = baseMapper.selectPage(pageParam,vo);

        return pageData;

    }*/




    @Override
    public List<StandardData> getByMaterialCode(String materialCode) {

        List<StandardData> standardDataList = materialStandardMapper.selectListByCode(materialCode);

        return standardDataList;
    }


    //利用materialCode、mark以及is_deleted = 1 来真实删除已经逻辑删除的重复数据
    @Override
    public void realDelete(String code, String mark) {

        materialStandardMapper.realDelete(code,mark);
    }

    //逻辑删除前先要进行判断，看是否已经存在被删除过一回的相同的materialCode、mark数据，如果有则先调用realDelete，再调用逻辑删除；如果没有则直接调用逻辑删除
    @Override
    public Integer countByCodeAndMark(String code, String mark) {

        Integer num = materialStandardMapper.countByCodeAndMark(code,mark);         //根据sql语句，这里统计的count（*）只是is_deleted = 1的记录
        return num;

    }


}
