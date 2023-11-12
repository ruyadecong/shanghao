package com.xcc.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.entity.Material;
import com.xcc.model.entity.StandardData;
import com.xcc.model.qc.MaterialStandard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.vo.MaterialStandardVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * <p>
 * 原料标准信息 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-06-07
 */
@Repository
public interface MaterialStandardMapper extends BaseMapper<MaterialStandard> {

    //条件分页查询
    //IPage<MaterialStandard> selectPage(Page<MaterialStandard> pageParam, @Param("vo") MaterialStandardVo vo);
    IPage<Material> selectPage(Page<Material> pageParam,@Param("vo") MaterialStandardVo vo);
    //IPage<MaterialStandard> selectPage(Page<MaterialStandard> pageParam,@Param("vo") MaterialStandardVo vo);


    //根据原料编码获取原料编码对应的所有检验项目内容
    List<StandardData> selectListByCode(@Param("code") String materialCode);

    //利用materialCode、mark以及is_deleted = 1 来真实删除已经逻辑删除的重复数据
    void realDelete(@Param("materialCode") String code,@Param("mark") String mark);

    //逻辑删除前先要进行判断，看是否已经存在被删除过一回的相同的materialCode、mark数据，如果有则先调用realDelete，再调用逻辑删除；如果没有则直接调用逻辑删除
    Integer countByCodeAndMark(@Param("materialCode") String code,@Param("mark") String mark);         //根据sql语句，这里统计的count（*）只是is_deleted = 1的记录

}
