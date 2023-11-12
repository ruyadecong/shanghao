package com.xcc.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.common.result.Result;
import com.xcc.model.purchase.MaterialPurchase;
import com.xcc.model.purchase.MaterialPurchaseTemporarily;
import com.xcc.system.mapper.MaterialPurchaseTemporarilyMapper;
import com.xcc.system.service.MaterialPurchaseTemporarilyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 原料采购信息暂存数据表 前端控制器
 * </p>
 *
 * @author xcc
 * @since 2023-08-01
 */

@Api(tags = "临时采购数据管理")
@RestController
@RequestMapping("/admin/purchase/materialPurchaseTemporarily")
public class MaterialPurchaseTemporarilyController {

    @Autowired
    MaterialPurchaseTemporarilyService materialPurchaseTemporarilyService;

    @ApiOperation("保存采购信息至临时数据表")
    @PostMapping("savePurchaseData")
    public Result savePurchaseData(@RequestBody MaterialPurchaseTemporarily materialPurchase){

        boolean save = materialPurchaseTemporarilyService.save(materialPurchase);

        Integer id = materialPurchase.getId();


        if(save){
            return Result.ok(id);
        }else{
            return Result.fail(id);
        }
    }

    @ApiOperation("更新采购信息至临时数据表")
    @PostMapping("updatePurchaseData")
    public Result updatePurchaseData(@RequestBody MaterialPurchaseTemporarily materialPurchase){

        boolean b = materialPurchaseTemporarilyService.updateById(materialPurchase);

        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }


    @ApiOperation("获取所有临时存储的原料采购信息")
    @GetMapping("getAllTemporaryPurchase")
    public Result getAllTemporaryPurchase(){
        QueryWrapper<MaterialPurchaseTemporarily> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        wrapper.eq("is_deleted",0);
        List<MaterialPurchaseTemporarily> list = materialPurchaseTemporarilyService.list(wrapper);
        return Result.ok(list);
    }

    @ApiOperation("根据id获取临时储存的原料采购信息")
    @GetMapping("getTemporaryById/{id}")
    public Result getTemporaryById(@PathVariable Integer id){
        MaterialPurchaseTemporarily byId = materialPurchaseTemporarilyService.getById(id);
        return Result.ok(byId);
    }


}

