package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xcc.model.qc.Equipment;
import com.xcc.model.vo.EquipmentQueryVo;
import com.xcc.system.mapper.EquipmentMapper;
import com.xcc.system.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Autowired
    EquipmentMapper equipmentMapper;

    @Override
    public IPage<Equipment> selectPage(Page<Equipment> pageParam, EquipmentQueryVo vo) {
        IPage<Equipment> pageModel = baseMapper.selectPage(pageParam,vo);
        return pageModel;
    }

    @Override
    public List<String> getAllPlace() {

        List<String> places = new ArrayList<>();

        places = equipmentMapper.getAllPlace();

        return places;
    }
}
