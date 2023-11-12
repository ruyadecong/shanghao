package com.xcc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.entity.Material;
import com.xcc.model.entity.MaterialIngredient;
import com.xcc.model.entity.StandardData;
import com.xcc.model.qc.MaterialStandard;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.vo.MaterialStandardVo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * <p>
 * 原料标准信息 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-06-07
 */
public interface MaterialStandardService extends IService<MaterialStandard> {

    //根据ids批量获取原料标准
    List<MaterialStandard> getByIds(List<Integer> ids) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    //条件分页查询
    //IPage<MaterialStandard> selectPage(Page<MaterialStandard> pageParam, MaterialStandardVo vo) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    IPage<Material> selectPage(Page<Material> pageParam, MaterialStandardVo vo) throws InvocationTargetException,NoSuchMethodException,IllegalAccessException;

    //IPage<MaterialStandard> selectPage(Page<MaterialStandard> pageParam, MaterialStandardVo vo) throws InvocationTargetException,NoSuchMethodException,IllegalAccessException;

    List<StandardData> getByMaterialCode(String materialCode);


    //利用materialCode、mark以及is_deleted = 1 来真实删除已经逻辑删除的重复数据
    void realDelete(String code,String mark);

    //逻辑删除前先要进行判断，看是否已经存在被删除过一回的相同的materialCode、mark数据，如果有则先调用realDelete，再调用逻辑删除；如果没有则直接调用逻辑删除
    Integer countByCodeAndMark(String code,String mark);

}
