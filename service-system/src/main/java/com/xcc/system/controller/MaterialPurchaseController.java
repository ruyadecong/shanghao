package com.xcc.system.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.model.base.SetValue;
import com.xcc.model.entity.CallInspectData;
import com.xcc.model.purchase.MaterialPurchase;
import com.xcc.model.purchase.MaterialPurchaseTemporarily;
import com.xcc.model.purchase.SupplierData;
import com.xcc.model.qc.CallInspect;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.vo.CallInspectVo;
import com.xcc.model.vo.MaterialBasedataVo;
import com.xcc.model.vo.MaterialPurchaseVo;
import com.xcc.model.vo.RemarkVo;
import com.xcc.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 原料采购信息 前端控制器
 * </p>
 *
 * @author xcc
 * @since 2023-06-10
 */
@Api(tags = "原料采购管理接口")
@RestController
@RequestMapping("/admin/purchase/material")
public class MaterialPurchaseController {

    @Autowired
    MaterialPurchaseService materialPurchaseService;

    @Autowired
    SupplierDataService supplierDataService;

    @Autowired
    MaterialBasedataService materialBasedataService;

    @Autowired
    MaterialPurchaseTemporarilyService materialPurchaseTemporarilyService;

    @Autowired
    SetValueService setValueService;

    @Autowired
    CallInspectService callInspectService;

    @ApiOperation("新增原料采购记录")
    @PostMapping("save")
    public Result save(@RequestBody MaterialPurchase materialPurchase){
        boolean b = materialPurchaseService.save(materialPurchase);

        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }

    }

    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result findPageQueryIngredientPurchase(@PathVariable Integer page,
                                                  @PathVariable Integer limit,
                                                  MaterialPurchaseVo vo){
        //创建page对象
        Page<MaterialPurchase> pageParam = new Page<>(page,limit);
        //调用service方法
        IPage<MaterialPurchase> pageModel = materialPurchaseService.selectPage(pageParam,vo);

        return Result.ok(pageModel);
    }

    @ApiOperation("根据id查找采购记录")
    @PostMapping("getById/{id}")
    public Result findById(@PathVariable Integer id){

        MaterialPurchase byId = materialPurchaseService.getById(id);

        return Result.ok(byId);

    }

    @ApiOperation("根据原料编码和供应商编码获取原料名称和供应商名称")
    @GetMapping("getCodeDetail/{maCode}/{suCode}")
    public Result getCodeDetail(@PathVariable String maCode,
                                @PathVariable String suCode){



        Map<String,String> map = new HashMap<>();

        if(maCode == "" || suCode == ""){
            map.put("status","请输入原料编码和供应商编码");
            return Result.ok(map);
        }

        MaterialBasedata material = materialBasedataService.getByCode(maCode);

        SupplierData supplier = supplierDataService.getByCode(suCode);

        if(material != null && supplier !=null) {

            map.put("status","成功");
            map.put("materialName", material.getMaterialName());
            map.put("supplierName", supplier.getTotalName());
            map.put("materialProducer", material.getProducterName());

            return Result.ok(map);
        } else if (material != null && supplier == null) {
            map.put("status","未查到供应商信息");
            map.put("materialName", material.getMaterialName());
            map.put("materialProducer", material.getProducterName());
            return Result.ok(map);
        } else if (material == null && supplier != null) {
            map.put("status","未查到原料数据");
            map.put("supplierName", supplier.getTotalName());
            return Result.ok(map);
        } else {
            map.put("status","未查到原料和供应商数据");

            return Result.ok(map);
        }
    }

/*    //根据id将采购完成的数据从material_purchase_temporarily表复制到material_purchase表中，同时将material_purchase_temporarily表中的数据进行逻辑删除
    @ApiOperation("根据id进行采购数据转存")
    @GetMapping("translatePurchaseData/{id}")
    public Result translatePurchaseData(@PathVariable Integer id){
        MaterialPurchaseTemporarily temporarily = materialPurchaseTemporarilyService.getById(id);
        MaterialPurchase materialPurchase = new MaterialPurchase();

        materialPurchase.setSupplierCode(temporarily.getSupplierCode());
        materialPurchase.setSupplierName(temporarily.getSupplierName());
        materialPurchase.setMaterialCode(temporarily.getMaterialCode());
        materialPurchase.setMaterialName(temporarily.getMaterialName());
        materialPurchase.setBatchNo(temporarily.getBatchNo());
        materialPurchase.setProductDate(temporarily.getProductDate());
        materialPurchase.setLifeSpan(temporarily.getLifeSpan());
        materialPurchase.setExpirationDate(temporarily.getExpirationDate());
        materialPurchase.setQuantityKg(temporarily.getQuantityKg());
        materialPurchase.setOrderCode(temporarily.getOrderCode());
        materialPurchase.setDeliveryDate(temporarily.getDeliveryDate());
        materialPurchase.setDeliveryCode(temporarily.getDeliveryCode());
        materialPurchase.setReceivedQuantity(temporarily.getReceivedQuantity());
        materialPurchase.setInDate(temporarily.getInDate());
        materialPurchase.setEntryNumber(temporarily.getEntryNumber());
        materialPurchase.setWholePackingPrice(temporarily.getWholePackingPrice());
        materialPurchase.setUnpackingUnitPrice(temporarily.getUnpackingUnitPrice());
        materialPurchase.setUnpackingSituation(temporarily.getUnpackingSituation());
        materialPurchase.setStorageWarehouse(temporarily.getStorageWarehouse());
        materialPurchase.setApplicationDate(temporarily.getApplicationDate());
        materialPurchase.setPaymentDate(temporarily.getPaymentDate());
        materialPurchase.setUnitPrice(temporarily.getUnitPrice());
        materialPurchase.setMaterialPrice(temporarily.getMaterialPrice());
        materialPurchase.setOtherPrice(temporarily.getOtherPrice());
        materialPurchase.setTotalPrice(temporarily.getTotalPrice());
        materialPurchase.setPaymentMethod(temporarily.getPaymentMethod());
        materialPurchase.setMaterialProducer(temporarily.getMaterialProducer());
        materialPurchase.setIsCallInspect(temporarily.getIsCallInspect());
        materialPurchase.setIsEmergencyRelease(temporarily.getIsEmergencyRelease());
        materialPurchase.setIsRelease(temporarily.getIsRelease());
        materialPurchase.setReportDate(temporarily.getReportDate());
        materialPurchase.setRemark(temporarily.getRemark());

        boolean save = materialPurchaseService.save(materialPurchase);
        Map<String,String> map = new HashMap<>();
        boolean b;
        if(save) {
            b = materialPurchaseTemporarilyService.removeById(id);
            map.put("status","成功");
            return Result.ok(map);
        }else{
            map.put("status","失败");
            return Result.ok(map);
        }
    }*/

    //表格录入版的新增原料采购
    @ApiOperation("新增原料采购")
    @GetMapping("addNewPurchase")
    public Result addNewPurchase(){
        MaterialPurchase materialPurchase = new MaterialPurchase();
        materialPurchase.setRemark("");
        materialPurchase.setUnitPrice(new BigDecimal(0));
        materialPurchase.setQuantityKg(new BigDecimal(0));
        materialPurchase.setMaterialPrice(new BigDecimal(0));
        materialPurchase.setOtherPrice(new BigDecimal(0));
        materialPurchase.setTotalPrice(new BigDecimal(0));
        Date date = new Date(System.currentTimeMillis());
        materialPurchase.setCreateTime(date);
        boolean save = materialPurchaseService.save(materialPurchase);


        Integer id = materialPurchase.getId();
        materialPurchase.setPurchaseSerialNumber(id);
        materialPurchaseService.updateById(materialPurchase);
/*        System.out.println("********************");
        System.out.println(id);
        System.out.println("********************");*/
        Map<String,String> message = new HashMap<>();
        if(save){
            message.put("status","成功");
            return Result.ok(message);
        }else{
            message.put("status","失败");
            return Result.fail(message);
        }
    }


    //表格录入版的删除采购数据
    @ApiOperation("根据id删除采购数据")
    @DeleteMapping("removeMaterialPurchase/{id}")
    public Result removeMaterialPurchase(@PathVariable Integer id){
        boolean b = materialPurchaseService.removeById(id);
        Map<String,String> message = new HashMap<>();
        if(b){
            message.put("status","成功");
            return Result.ok(message);
        }else{
            message.put("status","失败");
            return Result.fail(message);
        }
    }


    //表格录入版的修改采购数据
    @ApiOperation("修改采购数据")
    @PostMapping("updateMaterialPurchase")
    public Result updateMaterialPurchase(@RequestBody MaterialPurchase materialPurchase){
        MaterialBasedata material = materialBasedataService.getByCode(materialPurchase.getMaterialCode());
        String supplierCode = materialPurchase.getSupplierCode();
        //SupplierData supplier = supplierDataService.getByCode(materialPurchase.getSupplierCode());
        SupplierData supplier = supplierDataService.getByCode(supplierCode);
        if(material != null){
            materialPurchase.setMaterialName(material.getMaterialName());
        }else{
            materialPurchase.setMaterialName("未查到原料名称");
        }
        if(supplier != null) {
            materialPurchase.setSupplierName(supplier.getTotalName());
        }else{
            materialPurchase.setSupplierName("未查到供应商名称");
        }


        //根据入库日期生成入库单号
/*        Date inDate = materialPurchase.getInDate();
        String entryNumber = materialPurchaseService.getEntryNumber(inDate, supplierCode);
        materialPurchase.setEntryNumber(entryNumber);*/




/*        if("".equals(materialPurchase.getApplicationDate())){
            materialPurchase.setApplicationDate(null);
        }
        if("".equals(materialPurchase.getDeliveryDate())){
            materialPurchase.setDeliveryDate(null);
        }
        if("".equals(materialPurchase.getInDate()) || materialPurchase.getInDate() == null){
            System.out.println("**************************************");
            System.out.println(materialPurchase.getInDate());
            materialPurchase.setInDate(null);
            System.out.println(materialPurchase.getInDate());
        }
        if("".equals(materialPurchase.getPaymentDate())){
            materialPurchase.setPaymentDate(null);
        }*/
        if(materialPurchase.getQuantityKg() == null){
            materialPurchase.setQuantityKg(new BigDecimal(0));
        }
        if(materialPurchase.getUnitPrice() == null){
            materialPurchase.setUnitPrice(new BigDecimal(0));
        }
        if(materialPurchase.getOtherPrice() == null){
            materialPurchase.setOtherPrice(new BigDecimal(0));
        }
        if(materialPurchase.getReceivedQuantity() == null){
            materialPurchase.setReceivedQuantity(new BigDecimal(0));
        }

        materialPurchase.setMaterialPrice(materialPurchase.getUnitPrice().multiply(materialPurchase.getQuantityKg()));
        materialPurchase.setTotalPrice(materialPurchase.getMaterialPrice().add(materialPurchase.getOtherPrice()));

        boolean b = materialPurchaseService.updateById(materialPurchase);
        Map<String,String> message = new HashMap<>();
        if(b){
            message.put("status","成功");
            return Result.ok(message);
        }else{
            message.put("status","失败");
            return Result.fail(message);
        }
    }


    //根据id保存备注
    @ApiOperation("根据id保存备注")
    @GetMapping("updateRemark/{id}")
    public Result updateRemark(@PathVariable Integer id,
                               RemarkVo vo){

        //boolean b = materialPurchaseService.updateRemarkById(id,remark.getRemark());
        MaterialPurchase materialPurchase = materialPurchaseService.getById(id);
        materialPurchase.setRemark(vo.getRemark());
        materialPurchase.setPurchaseSerialNumber(vo.getPurchaseSerialNumber());
        boolean b = materialPurchaseService.updateById(materialPurchase);
        String remarkValue = materialPurchase.getRemark();
        Map<String,String> message = new HashMap<>();
        message.put("remark",remarkValue);
        if(b){
            message.put("status","成功");
            return Result.ok(message);
        }else{
            message.put("status","失败");
            return Result.fail(message);
        }
    }

    //获取配置的每页记录数
    @ApiOperation("郑柔丽获取每页记录数")
    @GetMapping("getLimit")
    public Result getLimit(){

        SetValue value = setValueService.getById(2018);
        int i = Integer.parseInt(value.getValue());
        return Result.ok(i);

    }



    //根据原料编码获取近期录入数据，用于生成新的采购记录
    @ApiOperation("根据原料编码获取近期录入数据")
    @GetMapping("getListForCopyAdd/{mc}")
    public Result getListForCopyAdd(@PathVariable String mc){

        int limit;
        limit = 10;
        //根据原料编码mc查询指定条目数limit的数据
        List<MaterialPurchase> list = materialPurchaseService.getByCode(mc,limit);

        return  Result.ok(list);
    }


    //根据选中的往期记录的某些字段新增采购记录
    @ApiOperation("根据选中的往期记录的某些字段新增采购记录")
    @GetMapping("selectThisOneForAdd/{id}/{purchaseSerialNumber}")
    public Result selectThisOneForAdd(@PathVariable Integer id,@PathVariable double purchaseSerialNumber){
        System.out.println(id);
        System.out.println(purchaseSerialNumber);

        MaterialPurchase byId = materialPurchaseService.getById(id);
        MaterialPurchase materialPurchase = new MaterialPurchase();
        materialPurchase.setRemark("");
        materialPurchase.setUnitPrice(new BigDecimal(0));
        materialPurchase.setQuantityKg(new BigDecimal(0));
        materialPurchase.setMaterialPrice(new BigDecimal(0));
        materialPurchase.setOtherPrice(new BigDecimal(0));
        materialPurchase.setTotalPrice(new BigDecimal(0));
        //加入往期采购数据的一些字段
        materialPurchase.setSupplierCode(byId.getSupplierCode());
        materialPurchase.setSupplierName(byId.getSupplierName());
        materialPurchase.setMaterialCode(byId.getMaterialCode());
        materialPurchase.setUnitPrice(byId.getUnitPrice());
        materialPurchase.setQuantityKg(byId.getQuantityKg());
        materialPurchase.setOtherMaterialName(byId.getOtherMaterialName());
        materialPurchase.setUnit(byId.getUnit());
        materialPurchase.setMaterialProducer(byId.getMaterialProducer());
        materialPurchase.setPaymentMethod(byId.getPaymentMethod());


        Date date = new Date(System.currentTimeMillis());
        materialPurchase.setCreateTime(date);


        boolean save = materialPurchaseService.save(materialPurchase);

        //这一部分是使用id作为采购序列号

        Integer saveId = materialPurchase.getId();

        if(purchaseSerialNumber == 0 ){
            materialPurchase.setPurchaseSerialNumber(saveId);
        }else{
            materialPurchase.setPurchaseSerialNumber(purchaseSerialNumber);
        }

        boolean b = materialPurchaseService.updateById(materialPurchase);

        Map<String,String> message = new HashMap<>();
        if(save){
            message.put("status","成功");
            return Result.ok(message);
        }else{
            message.put("status","失败");
            return Result.fail(message);
        }
    }

    @ApiOperation("获取采购数据用于报检")
    @GetMapping("getListForCallInspect")
    public Result getListForCallInspect(){
        List<MaterialPurchase> list = materialPurchaseService.getListForCallInspect();
        return Result.ok(list);
    }

    //处理前端传回的报检id列表
    @ApiOperation("处理前端传回的报检id列表")
    @PostMapping("callInspect")
    public Result callInspect(@RequestBody CallInspectVo vo){

        boolean b = materialPurchaseService.callInspect(vo);
        System.out.println(vo);

        Map<String,String> message = new HashMap<>();
        if(b){
            //获取vo中的idList
            List<Integer> idList = vo.getIdList();
            //把idList对应的记录的报检状态改为1
            for (Integer id : idList) {
                MaterialPurchase byId = materialPurchaseService.getById(id);
                byId.setIsCallInspect(1);
                materialPurchaseService.updateById(byId);
            }

            message.put("status","成功");
            return Result.ok(message);
        }else{
            message.put("status","失败");
            return Result.fail(message);
        }
    }

    //获取已报检数据列表
    @ApiOperation("获取已报检数据列表")
    @GetMapping("getListOfInspect")
    public Result getListOfInspect(MaterialBasedataVo vo){
        Integer limit;
        limit = 50;
        List<String> listOfSerialNumber = callInspectService.getListOfSerialNumber(limit,vo);
        List<CallInspectData> inspectData = new ArrayList<>();
        for (String serialNumber : listOfSerialNumber) {

            QueryWrapper<CallInspect> wrapper = new QueryWrapper<>();
            wrapper.eq("serial_number",serialNumber);
            wrapper.eq("is_deleted",0);
            List<CallInspect> list = callInspectService.list(wrapper);

            CallInspectData data = new CallInspectData();
            data.setSerialNumber(serialNumber);
            data.setInspectNum(list.size());
            Integer sum;    //用于判断当前serialNumber下的各个记录的is_print的状态，若都是1，则num = list.size（），若都是0，则num = 0，若都不是，则异常
            sum = 0;
            for (CallInspect inspect : list) {
                sum = sum + inspect.getIsPrint();
            }
            if(sum == list.size()){
                data.setIsPrint("是");
            } else if (sum == 0) {
                data.setIsPrint("否");
            }else{
                data.setIsPrint("异常");
            }

            inspectData.add(data);
        }

        return Result.ok(inspectData);
    }

    //根据报检数据表的serialNumber查询所有相关原料的信息和报检id
    @ApiOperation("根据serialNumber查询")
    @GetMapping("getListOfInspect/{serialNumber}")
    public Result getListOfInspect(@PathVariable String serialNumber){

        //根据serialNumber查询报检id和原料id，并获取原料的某些数据
        List<Object> result = callInspectService.getListOfInspect(serialNumber);

        return Result.ok(result);
    }


    //删除单个原料的报检数据
    @ApiOperation("删除单个原料的报检数据")
    @DeleteMapping("removeSingle/{inspectId}/{purchaseId}")
    public Result removeSingle(@PathVariable Integer inspectId,@PathVariable Integer purchaseId){

        //逻辑删除call_inspect中的inspectId对应的记录
        boolean b = callInspectService.removeById(inspectId);
        //将material_purchase中的purchaseId对应的记录的报检状态改为0
        MaterialPurchase materialPurchase = materialPurchaseService.getById(purchaseId);
        materialPurchase.setIsCallInspect(0);
        boolean b1 = materialPurchaseService.updateById(materialPurchase);

        return  Result.ok();
    }

    //根据serialNumber删除call_inspect表中的有关数据
    @ApiOperation("根据serialNumber删除call_inspect表中的有关数据")
    @DeleteMapping("removeBySerialNumber/{serialNumber}")
    public Result removeBySerialNumber(@PathVariable String serialNumber){

        QueryWrapper<CallInspect> wrapper = new QueryWrapper<>();
        wrapper.eq("serial_number",serialNumber);
        wrapper.eq("is_deleted",0);
        List<CallInspect> inspectList = callInspectService.list(wrapper);
        for (CallInspect inspect : inspectList) {
            //将material_purchase中的purchaseId对应的记录的报检状态改为0
            MaterialPurchase materialPurchase = materialPurchaseService.getById(inspect.getPurchaseId());
            materialPurchase.setIsCallInspect(0);
            materialPurchaseService.updateById(materialPurchase);
            callInspectService.removeById(inspect.getId());
        }

        return  Result.ok();
    }

    //确认报检单打印完成，根据serialNumber修改其is_print为1
    @ApiOperation("根据serialNumber修改其is_print为1")
    @GetMapping("confirmForPrint/{serialNumber}")
    public Result confirmForPrint(@PathVariable String serialNumber){
        //确认报检单打印完成，根据serialNumber修改其is_print为1
        boolean b = callInspectService.updatePrintBySerialNumber(serialNumber);

        Map<String,String> msg = new HashMap<>();

        if(b){
            msg.put("status","成功");
            return Result.ok(msg);
        }else{
            msg.put("status","失败");
            return Result.fail(msg);
        }
    }



















}

