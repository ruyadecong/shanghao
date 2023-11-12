package com.xcc.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.qc.Equipment;
import com.xcc.model.vo.EquipmentQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentMapper extends BaseMapper<Equipment> {

    //条件分页查询
    IPage<Equipment> selectPage(Page<Equipment> pageParam,@Param("vo") EquipmentQueryVo vo);

    List<String> getAllPlace();
}
