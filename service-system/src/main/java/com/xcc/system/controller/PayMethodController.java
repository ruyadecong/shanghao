package com.xcc.system.controller;


import com.xcc.common.result.Result;
import com.xcc.model.purchase.MaterialPurchase;
import com.xcc.model.purchase.PayMethod;
import com.xcc.system.service.MaterialPurchaseService;
import com.xcc.system.service.PayMethodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 付款方式信息 前端控制器
 * </p>
 *
 * @author xcc
 * @since 2023-07-29
 */
@Api(tags = "付款方式管理")
@RestController
@RequestMapping("/admin/purchase/payMethod")
public class PayMethodController {

    @Autowired
    PayMethodService payMethodService;

    @Autowired
    MaterialPurchaseService materialPurchaseService;

    @ApiOperation("获取所有付款方式ModelList")
    @GetMapping("getAllPayMethod")
    public Result getAllPayMethod(){

        List<PayMethod> list = payMethodService.list();
        return Result.ok(list);
    }

    @ApiOperation("获取所有付款方式List")
    @GetMapping("getAllPayMethodForOption/{purpose}")           //purpose这个参数用于决定从哪个表中获取付款方式，从pay_method中获得数据是目前使用的付款方式，不包含被删除的付款方式，从material_purchase表中获得的付款方式是历史使用的付款方式的合计，可能包含或不包含目前使用的付款方式
    public Result getAllPayMethodForOption(@PathVariable Integer purpose){
        List<String> list = new ArrayList<>();
        if(purpose == 1){   //1表示是从pay_method中获取数据
            List<PayMethod> payMethods = payMethodService.list();
            for (PayMethod payMethod : payMethods) {
                list.add(payMethod.getPaymentMethod());
            }
            return Result.ok(list);
        } else if(purpose == 2){ //2表示是从material_purchase中获取数据
            //要编写sql，从material_purchase表中获取不重复的付款方式
            List<String> payMethodList = materialPurchaseService.payMethodList();

            return Result.ok(payMethodList);
        } else{
            return Result.fail();
        }
    }

    @ApiOperation("根据id逻辑删除")
    @DeleteMapping("removePayMethod/{id}")
    public Result removePayMethod(@PathVariable Integer id){
        boolean b = payMethodService.removeById(id);
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("根据id获取PayMethod")
    @GetMapping("getPayMethodById/{id}")
    public Result getPayMethodById(@PathVariable Integer id){
        PayMethod payMethod = payMethodService.getById(id);
        return Result.ok(payMethod);
    }

    @ApiOperation("添加新的付款方式")
    @PostMapping("addPayMethod")
    public Result addPayMethod(@RequestBody PayMethod payMethod){
        boolean save = payMethodService.save(payMethod);
        if(save){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("根据id修改付款方式")
    @PostMapping("updatePayMethodById")
    public Result updatePayMethodById(@RequestBody PayMethod payMethod){
        boolean b = payMethodService.updateById(payMethod);
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }
}

