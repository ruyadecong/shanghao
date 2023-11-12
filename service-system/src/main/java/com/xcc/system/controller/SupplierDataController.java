package com.xcc.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.model.purchase.SupplierData;
import com.xcc.model.qc.Equipment;
import com.xcc.model.qc.InspectionDetail;
import com.xcc.model.vo.EquipmentQueryVo;
import com.xcc.model.vo.SupplierVo;
import com.xcc.system.service.InspectionDetailService;
import com.xcc.system.service.SupplierDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 供应商信息 前端控制器
 * </p>
 *
 * @author xcc
 * @since 2023-07-18
 */
@Api(tags = "供应商信息管理")
@RestController
@RequestMapping("admin/purchase/supplierData")
public class SupplierDataController {
    @Autowired
    SupplierDataService supplierDataService;

    @ApiOperation("获取所有供应商数据")
    @GetMapping("getAll")
    public Result getAll(){

        QueryWrapper<SupplierData> wrapper =new QueryWrapper<>();

        wrapper.orderByAsc("supplier_code");
        List<SupplierData> list = supplierDataService.list(wrapper);

        return Result.ok(list);
    }

    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result findPageQueryEquipment(@PathVariable Long page,
                                         @PathVariable Long limit,
                                         SupplierVo vo) {
        //创建page对象
        Page<SupplierData> pageParam = new Page<>(page, limit);
        //调用service方法
        IPage<SupplierData> pageModel = supplierDataService.selectPage(pageParam, vo);

        return Result.ok(pageModel);
    }

    @ApiOperation("逻辑删除接口")
    @DeleteMapping("remove/{id}")
    public Result removeSupplier(@PathVariable Integer id){
        boolean b = supplierDataService.removeById(id);
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }

    }

    @ApiOperation("添加供应商")
    @PostMapping("save")
    public Result saveSupplier(@RequestBody SupplierData supplierData){
        boolean b = supplierDataService.save(supplierData);
        if(b) {
            return Result.ok();
        }else{
            return Result.fail();
        }
    }


    @ApiOperation("修改供应商数据")
    @PostMapping("update")
    public Result update(@RequestBody SupplierData supplierData){

        boolean b = supplierDataService.updateById(supplierData);

        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("根据id查询")
    @PostMapping("getSupplier/{id}")
    public Result findSupplierById(@PathVariable Integer id){

        SupplierData supplierData = supplierDataService.getById(id);

        return Result.ok(supplierData);
    }

    @ApiOperation("获取供应商类别")      //给下拉菜单用
    @GetMapping("getSort")
    @ResponseBody
    public Result getSort(){

        List<String> sort = new ArrayList<>();

        sort = supplierDataService.getAllSort();

        return Result.ok(sort);
    }
}

