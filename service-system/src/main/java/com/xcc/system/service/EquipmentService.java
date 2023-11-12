package com.xcc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.qc.Equipment;
import com.xcc.model.vo.EquipmentQueryVo;

import java.util.List;

public interface EquipmentService extends IService<Equipment> {
    IPage<Equipment> selectPage(Page<Equipment> pageParam, EquipmentQueryVo vo);

    List<String> getAllPlace();
}
