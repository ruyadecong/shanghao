package com.xcc.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.model.qc.Equipment;
import com.xcc.model.vo.EquipmentQueryVo;
import com.xcc.system.service.EquipmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "设备管理接口")
@RestController
@RequestMapping("/admin/qc/Equipment")
public class EquipmentController {

    @Autowired
    EquipmentService equipmentService;

    @ApiOperation("查询所有记录")
    @GetMapping("findAll")
    public Result findAll(){

        List<Equipment> list = equipmentService.list(null);

        return Result.ok(list);
    }

    @ApiOperation("逻辑删除接口")
    @DeleteMapping("remove/{id}")
    public Result removeEquipment(@PathVariable Integer id){
        boolean b = equipmentService.removeById(id);
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }

    }


    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result findPageQueryEquipment(@PathVariable Long page,
                                         @PathVariable Long limit,
                                         EquipmentQueryVo vo) {
        //创建page对象
        Page<Equipment> pageParam = new Page<>(page, limit);
        //调用service方法
        IPage<Equipment> pageModel = equipmentService.selectPage(pageParam, vo);

        return Result.ok(pageModel);
    }

    @ApiOperation("添加设备")
    @PostMapping("save")
    public Result saveEquipment(@RequestBody Equipment equipment){
        boolean b = equipmentService.save(equipment);
        if(b) {
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("根据id查询")
    @PostMapping("findEquipmentById/{id}")
    public Result findEquipmentById(@PathVariable Integer id){

        Equipment equipment = equipmentService.getById(id);

        return Result.ok(equipment);

    }

    @ApiOperation("修改设备信息")
    @PostMapping("update")
    public Result update(@RequestBody Equipment equipment){

        boolean b = equipmentService.updateById(equipment);

        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }

    }

    @ApiOperation("批量逻辑删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Integer> ids){
        boolean b = equipmentService.removeByIds(ids);
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("获取设备所有放置地点的名字")      //给下拉菜单用
    @GetMapping("getPlace")
    @ResponseBody
    public Result getPlace(){

        List<String> places = new ArrayList<>();

        places = equipmentService.getAllPlace();

        return Result.ok(places);
    }

/*    @ApiOperation("上传图片")
    @PostMapping("uploadPhoto")
    public Result uploadPhoto(MultipartFile ){

        return Result.ok();
    }*/


}
