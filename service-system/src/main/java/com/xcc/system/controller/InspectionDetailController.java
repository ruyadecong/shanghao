package com.xcc.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.common.result.Result;
import com.xcc.model.qc.Equipment;
import com.xcc.model.qc.InspectionDetail;
import com.xcc.system.service.InspectionDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 检验项目汇总表 前端控制器
 * </p>
 *
 * @author xcc
 * @since 2023-07-23
 */
@Api(tags = "原料检验项目")
@RestController
@RequestMapping("admin/qc/inspectionDetail")
public class InspectionDetailController {

    @Autowired
    InspectionDetailService inspectionDetailService;

    @ApiOperation("获取所有检验项目")
    @GetMapping("getAll")
    public Result getAll(){

        QueryWrapper<InspectionDetail> wrapper =new QueryWrapper<>();

        wrapper.orderByAsc("inspection_order");
        List<InspectionDetail> list = inspectionDetailService.list(wrapper);

        return Result.ok(list);
    }


    @ApiOperation("根据id获取某类检验项目的数据")
    @GetMapping("getInspectionById/{id}")
    public Result getInspectionById(@PathVariable Integer id){

        InspectionDetail byId = inspectionDetailService.getById(id);

        return Result.ok(byId);

    }


    @ApiOperation("获取所有检验项目的mark")
    @GetMapping("getAllMark")
    public Result getAllMark(){

        List<InspectionDetail> list = inspectionDetailService.list();

        List<String> markList = new ArrayList<>();

        for (InspectionDetail inspectionDetail : list) {
            markList.add(inspectionDetail.getMark());
        }

        return Result.ok(markList);
    }


    @ApiOperation("逻辑删除接口")
    @DeleteMapping("remove/{id}")
    public Result removeInspection(@PathVariable Integer id){
        boolean b = inspectionDetailService.removeById(id);
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }

    }

    @ApiOperation("添加检验项目")
    @PostMapping("save")
    public Result saveInspection(@RequestBody InspectionDetail inspectionDetail){
        boolean b = inspectionDetailService.save(inspectionDetail);
        if(b) {
            return Result.ok();
        }else{
            return Result.fail();
        }
    }


    @ApiOperation("修改检验项目")
    @PostMapping("update")
    public Result update(@RequestBody InspectionDetail inspectionDetail){

        boolean b = inspectionDetailService.updateById(inspectionDetail);

        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("根据id查询")
    @PostMapping("getInspectionDetail/{id}")
    public Result findInspectionById(@PathVariable Integer id){

        InspectionDetail inspectionDetail = inspectionDetailService.getById(id);

        return Result.ok(inspectionDetail);

    }

}

