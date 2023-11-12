package com.xcc.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.model.entity.*;
import com.xcc.model.qc.InspectionDetail;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.qc.MaterialStandard;
import com.xcc.model.vo.MaterialStandardVo;
import com.xcc.system.service.InspectionDetailService;
import com.xcc.system.service.MaterialBasedataService;
import com.xcc.system.service.MaterialStandardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.ResettableListIterator;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 原料标准信息 前端控制器
 * </p>
 *
 * @author xcc
 * @since 2023-06-07
 */
@Api(tags = "原料标准接口")
@RestController
@RequestMapping("/admin/qc/materialStandard")
public class MaterialStandardController {

    @Autowired
    MaterialStandardService materialStandardService;

    @Autowired
    InspectionDetailService inspectionDetailService;

    @Autowired
    MaterialBasedataService materialBasedataService;

    @ApiOperation("批量获取原料的标准")
    @PostMapping("findByIds")
    public Result getAll(@RequestBody List<Integer> ids) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        List<MaterialStandard> byIds = materialStandardService.getByIds(ids);


        return Result.ok(byIds);
    }



    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result findPageQueryMaterialStandard(@PathVariable Integer page,
                                                 @PathVariable Integer limit,
                                                 MaterialStandardVo vo)throws InvocationTargetException,NoSuchMethodException,IllegalAccessException{

        Page<Material> pageParam = new Page<>(page,limit);
        IPage<Material> materialIPage = materialStandardService.selectPage(pageParam, vo);

        List<Material> records = materialIPage.getRecords();
        Integer i = 1;
        for (Material record : records) {
            List<StandardData> standardData = materialStandardService.getByMaterialCode(record.getMaterialCode());

            //根据原料基本数据表中standard_number（原料检验项目数）字段来判断检验标准是否定稿，如果字段值为-1则为未定稿
            MaterialBasedata byCode = materialBasedataService.getByCode(record.getMaterialCode());
            Integer standardNumber;
            String updateReason;
            updateReason = "";
            if (byCode == null) {
                standardNumber = -2;           //-2代表material_basedata表中无此原料
            }else{
                standardNumber = byCode.getStandardNumber();
                updateReason = byCode.getUpdateReason();
            }
            record.setStandardConfirm(standardNumber);
            record.setItemIndex(i);
            record.setStandardData(standardData);
            record.setUpdateReason(updateReason);
            i = i+1;
        }
        return Result.ok(materialIPage);

/*        Page<MaterialStandard> pageParam = new Page<>(page,limit);
        IPage<MaterialStandard> materialStandardIPage = materialStandardService.selectPage(pageParam,vo);


        return Result.ok(materialStandardIPage);*/

    }

    //这部分代码似乎没用到
    @ApiOperation("添加原料标准")
    @PostMapping("save")
    public Result save(@RequestBody MaterialStandard vo) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        boolean b = materialStandardService.save(vo);
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("修改原料标准")
    @PostMapping("updateItem")
    public Result update(@RequestBody MaterialStandard materialStandard) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

/*        MaterialStandard standard = new MaterialStandard();
        standard.setId(materialStandard.getId());
        standard.setMaterialCode(materialStandard.getMaterialCode());
        standard.setMark(materialStandard.getMark());
        standard.setInspectionItem(materialStandard.getInspectionItem());
        standard.setInspectionStandard(materialStandard.getInspectionStandard());
        standard.setInspectionMethod(materialStandard.getInspectionMethod());
        standard.setRemark(materialStandard.getRemark());
        standard*/
        boolean b = materialStandardService.updateById(materialStandard);
        System.out.println("////////////////////////////////////////////////////////////");
        System.out.println(materialStandard);
        System.out.println(materialStandard.getId());
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }


    @ApiOperation("保存原料编码新增检验项目")
    @PostMapping("saveItem")
    public Result saveItemByCode(@RequestBody MaterialStandard materialStandard){

        boolean save = materialStandardService.save(materialStandard);

        if(save){
            return Result.ok();
        }else{
            return Result.fail();
        }

    }

    //获取id对应的原料的某个检验条目的数据
    @ApiOperation("根据id获取检验条目的数据")
    @GetMapping("getItemById/{id}")
    public Result getItemById(@PathVariable Integer id){

        MaterialStandard byId = materialStandardService.getById(id);
        return Result.ok(byId);
    }


    //根据原料标准的id获取原料标准对应的原料的原料编码，这个原料编码是用来在对此原料编码新增检验项目是用的
    @ApiOperation("根据id在原料标准表获取其code")
    @GetMapping("getCodeById/{id}")
    public Result getCodeById(@PathVariable Integer id){

        MaterialStandard byId = materialStandardService.getById(id);

        String materialCode = byId.getMaterialCode();

        return Result.ok(materialCode);
    }



    //根据原料编码来获取此原料没有的检验项目，InspectionDetailController里面有获取所有检验项目的接口，但是前端设计成下拉列表里的项目扣除已有的检验项目
    @ApiOperation("获取未在原料检验项目中的检验项目")
    @GetMapping("getNotExistMark/{id}")
    public Result getNotExistMark(@PathVariable Integer id){

        boolean b;

        MaterialStandard byId = materialStandardService.getById(id);

        String materialCode = byId.getMaterialCode();

        List<StandardData> standardDataList = materialStandardService.getByMaterialCode(materialCode);
        List<String> existMarkList = new ArrayList<>();
        for (StandardData standardData : standardDataList) {
            existMarkList.add(standardData.getMark());
        }



        List<InspectionDetail> list = inspectionDetailService.list();
        List<String> allMarkList = new ArrayList<>();
        for (InspectionDetail inspectionDetail : list) {
            allMarkList.add(inspectionDetail.getMark());
        }



        List<String> markList = new ArrayList<>();
        for (String s : allMarkList) {
            b = false;
            for (String s1 : existMarkList) {
                if (s.equals(s1)){
                    b = true;
                }
            }
            if(b == false){
                markList.add(s);
            }
        }

        return Result.ok(markList);
    }

    //根据检验项目的标记（mark）获取检验项目的数据
    @ApiOperation("根据mark获取inspection")
    @GetMapping("getInspectionByMark/{mark}")
    public Result getInspectionByMark(@PathVariable String mark){

        InspectionData detail = inspectionDetailService.getInspectionByMark(mark);

        return Result.ok(detail);

    }

    //根据前端传回的id删除指定检验项目
    @ApiOperation("根据id进行逻辑删除")
    @DeleteMapping("removeById/{id}")
    public Result removeById(@PathVariable Integer id){
        MaterialStandard byId = materialStandardService.getById(id);
        String code = byId.getMaterialCode();
        String mark = byId.getMark();
        //逻辑删除前先要进行判断，看是否已经存在被删除过一回的相同的materialCode、mark数据，如果有则先调用realDelete，再调用逻辑删除；如果没有则直接调用逻辑删除
        Integer num = materialStandardService.countByCodeAndMark(code,mark);

/*        System.out.println("*************num = "+num);
        System.out.println("*************");*/
        //sql语句中查询的是is_deleted = 1的情况，如果返回0，则说明没有重复数据
        if (num == 0){
            boolean b = materialStandardService.removeById(id);
            if(b){
                return Result.ok();
            }else{
                return Result.fail();
            }
        }else { //如果返回不是0，则说明有重复数据
            //出现无法删除的情况时，可能是由于unique key（code、mark、is_deleted）导致已经删除过一回的相同内容（code、mark）不能再删除（再删除会导致unique key重复）
            //如果确实出现这种情况，需要找到要删除的数据的materialCode，再由此code去找到已经被删除过一回的相同code和mark的记录的id
            //利用materialCode、mark以及is_deleted = 1 来真实删除已经逻辑删除的重复数据
            materialStandardService.realDelete(code, mark);
            boolean b = materialStandardService.removeById(id);
            if(b){
                return Result.ok();
            }else{
                return Result.fail();
            }
        }
    }

    //根据前端传回的数据增加原料标准
    @ApiOperation("保存新增的原料标准数据")
    @PostMapping("saveMaterial")
    public Result saveMaterial(@RequestBody StandardFromFront standard){

        MaterialStandard materialStandard = new MaterialStandard();

        materialStandard.setMaterialCode(standard.getMaterialCode());
        //保存外观数据
        materialStandard.setMark("外观");
        materialStandard.setInspectionItem("外观");
        materialStandard.setInspectionStandard(standard.getAppearance());
        materialStandard.setInspectionMethod("目测");
        boolean saveAppearance = materialStandardService.save(materialStandard);

        //保存颜色数据
        materialStandard.setMark("颜色");
        materialStandard.setInspectionItem("颜色");
        materialStandard.setInspectionStandard(standard.getColor());
        materialStandard.setInspectionMethod("目测");
        boolean saveColor = materialStandardService.save(materialStandard);

        //保存气味数据
        materialStandard.setMark("气味");
        materialStandard.setInspectionItem("气味");
        materialStandard.setInspectionStandard(standard.getSmell());
        materialStandard.setInspectionMethod("嗅测");
        boolean saveSmell = materialStandardService.save(materialStandard);

        if(saveAppearance && saveColor && saveSmell){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }


    //查询新添加的原料编码是否已经在数据库中，如果没有则会启动感官输入框
    @ApiOperation("查询是否已有原料编码")
    @GetMapping("checkDuplicationByCode/{code}")
    public Result checkDuplicationByCode(@PathVariable String code){

        List<StandardData> byMaterialCode = materialStandardService.getByMaterialCode(code);

        int size = byMaterialCode.size();

        return Result.ok(size > 0 ? true : false);
    }


    //根据id找到code，再将material_basedata表中对应code的standard_Number改为检验项目数
    @ApiOperation("检验项目定稿")
    @GetMapping("confirmStandard/{id}")
    public Result confirmStandard(@PathVariable Integer id) {

        Map<String, String> msg = new HashMap<>();
        //根据id在material_standard表中找到material_code
        MaterialStandard byId = materialStandardService.getById(id);
        String materialCode = byId.getMaterialCode();
        //根据materiaCode在material_basedata表中找到对应记录
        MaterialBasedata byCode = materialBasedataService.getByCode(materialCode);
        if (byCode == null) {
            msg.put("status", "无数据");
            return Result.ok(msg);
        } else {
            //根据materiaCode在material_standard表中找到对应原料的检验项目数
            QueryWrapper<MaterialStandard> wrapper = new QueryWrapper<>();
            wrapper.eq("material_code", materialCode);
            wrapper.eq("is_deleted", 0);
            int count = materialStandardService.count(wrapper);
            //修改material_basedata表中找到对应记录的standard_number字段为count
            byCode.setStandardNumber(count);
            //根据byCode修改material_basedata表中的对应记录
            boolean b = materialBasedataService.updateById(byCode);



            if (b) {
                msg.put("status", "成功");
                return Result.ok(msg);
            } else {
                msg.put("status", "失败");
                return Result.fail(msg);
            }
        }
    }


    //申请对已经定稿的原料标准进行修改
    @ApiOperation("申请对已经定稿的原料标准进行修改")
    @GetMapping("applyForEdit")
    public Result applyForEdit(Material editData){
        String code = editData.getMaterialCode();
        String updateReason = editData.getUpdateReason();
        //申请修改原料标准后，将原料数据表中的standard_number设置为0
        MaterialBasedata materialBasedata = materialBasedataService.getByCode(code);
        materialBasedata.setStandardNumber(0);
        materialBasedata.setUpdateReason(updateReason);
        boolean b = materialBasedataService.updateById(materialBasedata);

        Map<String,String> message = new HashMap<>();

        if(b){
            message.put("status","申请原料标准修改成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "申请原料标准修改失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }

    //获取申请修改原料标准的列表，就是material_basedata表中standard_number为0的记录
    @ApiOperation("获取申请修改原料标准的列表")
    @GetMapping("getListOfApplyForEdit")
    public Result getListOfApplyForEdit(){

        List<MaterialBasedata> materialBasedataList = materialBasedataService.getListOfApplyForEdit();

        return Result.ok(materialBasedataList);
    }


    //同意原料标准修改申请，就是将material_basedata表中standard_number改为-1
    @ApiOperation("同意原料标准的修改申请")
    @GetMapping("confirmForApply/{id}")
    public Result confirmForApply(@PathVariable Integer id){

        MaterialBasedata materialBasedata = materialBasedataService.getById(id);
        materialBasedata.setStandardNumber(-1);
        materialBasedata.setUpdateReason(materialBasedata.getUpdateReason() + "<申请通过→→→>");
        boolean b = materialBasedataService.updateById(materialBasedata);

        Map<String,String> message = new HashMap<>();

        if(b){
            message.put("status","同意原料标准修改成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "同意原料标准修改失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }

    //驳回原料标准修改申请，就是将material_basedata表中standard_number改为实际条目数
    @ApiOperation("驳回原料标准的修改申请")
    @GetMapping("rejectForApply/{id}")
    public Result rejectForApply(@PathVariable Integer id){
/*
        Map<String, String> message = new HashMap<>();
        //根据id在material_standard表中找到material_code
        MaterialStandard byId = materialStandardService.getById(id);
        String materialCode = byId.getMaterialCode();
        //根据materiaCode在material_basedata表中找到对应记录
        MaterialBasedata byCode = materialBasedataService.getByCode(materialCode);
        if (byCode == null) {
            message.put("status", "驳回失败（未找到原料编码为" + materialCode + "的原料数据），请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        } else {
            //根据materiaCode在material_standard表中找到对应原料的检验项目数
            QueryWrapper<MaterialStandard> wrapper = new QueryWrapper<>();
            wrapper.eq("material_code", materialCode);
            wrapper.eq("is_deleted", 0);
            int count = materialStandardService.count(wrapper);
            //修改material_basedata表中找到对应记录的standard_number字段为count
            byCode.setStandardNumber(count);
            //根据byCode修改material_basedata表中的对应记录
            boolean b = materialBasedataService.updateById(byCode);
            if (b) {
                message.put("status", "驳回成功");
                message.put("code", "1");
                return Result.ok(message);
            } else {
                message.put("status", "驳回失败（未统计原料编码为" + materialCode + "的原料数据），请联系管理员");
                message.put("code", "0");
                return Result.ok(message);
            }
        }
        */






        Map<String, String> message = new HashMap<>();
        //根据id在material_basedata中找到对应记录
        MaterialBasedata byId = materialBasedataService.getById(id);
        String materialCode = byId.getMaterialCode();
        if (byId == null) {
            message.put("status", "驳回失败（未找到原料编码为" + materialCode + "的原料数据），请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        } else  {
            //根据materiaCode在material_standard表中找到对应原料的检验项目数
            QueryWrapper<MaterialStandard> wrapper = new QueryWrapper<>();
            wrapper.eq("material_code", materialCode);
            wrapper.eq("is_deleted", 0);
            int count = materialStandardService.count(wrapper);
            //修改material_basedata表中找到对应记录的standard_number字段为count
            byId.setStandardNumber(count);
            byId.setUpdateReason(byId.getUpdateReason() + "<申请被驳回！！！>");
            //根据byCode修改material_basedata表中的对应记录
            boolean b = materialBasedataService.updateById(byId);
            if (b) {
                message.put("status", "驳回成功");
                message.put("code", "1");
                return Result.ok(message);
            } else  {
                message.put("status", "驳回失败（未统计原料编码为" + materialCode + "的原料检验项目数），请联系管理员");
                message.put("code", "0");
                return Result.ok(message);
            }
        }

    }


}

