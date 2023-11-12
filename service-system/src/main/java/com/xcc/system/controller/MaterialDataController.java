package com.xcc.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.common.utils.FileUtil;
import com.xcc.common.utils.GenerateCreateTime;
import com.xcc.common.utils.JwtHelper;
import com.xcc.model.base.IsConfirmEnum;
import com.xcc.model.entity.Material;
import com.xcc.model.entity.MaterialIngredient;
import com.xcc.model.history.MaterialBasedataHistory;
import com.xcc.model.history.MaterialIngredientHistory;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.system.SysRole;
import com.xcc.model.technology.FileData;
import com.xcc.model.temp.MaterialBasedataTemp;
import com.xcc.model.temp.MaterialIngredientTemp;
import com.xcc.model.vo.*;
import com.xcc.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <p>
 * 原料基础信息 前端控制器
 * </p>
 *
 * @author xcc
 * @since 2023-06-03
 */
@Api(tags = "原料信息接口")
@RestController
@RequestMapping("/admin/qc/MaterialData")
public class MaterialDataController {

    @Autowired
    MaterialBasedataService materialBasedataService;

    @Autowired
    MaterialIngredientService materialIngredientService;

    @Autowired
    MaterialBasedataTempService materialBasedataTempService;

    @Autowired
    MaterialIngredientTempService materialIngredientTempService;

    @Autowired
    MaterialBasedataHistoryService materialBasedataHistoryService;

    @Autowired
    MaterialIngredientHistoryService materialIngredientHistoryService;

    @Autowired
    FileCategoryService fileCategoryService;

    @Autowired
    FileDataService fileDataService;

    @Autowired
    SysUserService sysUserService;

    @ApiOperation("批量获取原料的信息")
    @PostMapping("findByIds")
    public Result findByIds(@RequestBody List<Integer> ids){

        List<MaterialBasedata> list = materialBasedataService.listByIds(ids);

        return Result.ok(list);

    }


    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result findPageQueryMaterialBaseData(@PathVariable Integer page,
                                                  @PathVariable Integer limit,
                                                  MaterialBasedataVo vo){
        Map<String,Object> result = new HashMap<>();

        //创建page对象
        Page<MaterialBasedata> pageParam = new Page<>(page,limit);
        //调用service方法
        IPage<MaterialBasedata> pageModel = materialBasedataService.selectPage(pageParam,vo);


        //由于前端采用原料基础信息和成分信息分开获取的方式，所以以下代码不用，包括其关联的service、mapper、sql都注释掉
/*        List<Material> records = pageModel.getRecords();

        for (Material record : records) {
            List<MaterialIngredient> ingredientList = materialBasedataService.getIngredientByCode(record.getMaterialCode());

            record.setMaterialIngredients(ingredientList);
        }*/


        //获取目前表格所关联的文件类型数据
        List<String> categoryList = fileCategoryService.getListOfCategoryNameByTableName(vo.getTableName());
        result.put("categoryList",categoryList);
        //获取页面数据中所有id对应的文件列表
        for (MaterialBasedata record : pageModel.getRecords()) {
            //在file_data表中根据material_basedata表中的id获取相应的文件列表（获取过程要加入表格名称判断，以获取指定表格中可以显示的文件类型）
            List<FileData> fileDataList = fileDataService.getFileListByMaterialId(record.getId(),vo.getTableName());
/*            for (FileData fileData : fileDataList) {
                System.out.println("///////////////////////");
                System.out.println(fileData);
            }*/
            if(fileDataList.size() > 0){
                for (FileData fileData : fileDataList) {
                    fileData.setFileName(FileUtil.getFileNameCategory(fileData));
                    //获得文件属性，这句代码暂时不用
                    //fileData.setFileType(FileUtil.getFileSuffix(fileData));
                }
            }
            record.setFileDataList(fileDataList);
        }

        result.put("fileList",pageModel);


/*
        for (MaterialBasedata record : pageModel.getRecords()) {
            System.out.println("*****************************************");
            System.out.println(record.getFileDataList());
        }*/

        String token = vo.getToken();

        String username = JwtHelper.getUsername(token);
        //根据用户名获取其角色信息（用户名为unique）
        //一个用户可能有多个角色
        List<SysRole> roleList = sysUserService.getUserRoleByUserName(username);
        //authorityList用于保存用户在此表中所具有的权限
        List<Integer> authorityList = new ArrayList<>();
        //roleForFileManager具有文件管理权限的角色列表
        //如果还有其他权限指定角色列表，则要重新设置一个新的具有某种权限的角色列表
        List<String> roleForFileManager = new ArrayList<>();
        roleForFileManager.add("purchase");
        roleForFileManager.add("SYSTEM");
        //对用户的每个角色进行遍历，每个角色可能有不同的权限
        for (SysRole sysRole : roleList) {
            String roleCode = sysRole.getRoleCode();
            if(roleForFileManager.contains(roleCode)){
                //如果用户的角色code在roleForFileManager列表中，则往其authorityList中加入1
                //若是此角色还需赋予其他权限，则需要在另外一个判断语句中往authorityList中加入其他标记数字
                authorityList.add(1);
            }
        }
        result.put("authorityList",authorityList);


        return Result.ok(result);

    }


    @ApiOperation("根据原料id获取其信息")
    @GetMapping("getDataByMaterialId/{id}")
    public Result getDataByMaterialId(@PathVariable Integer id){


        MaterialBasedata byId = materialBasedataService.getById(id);

        String code = byId.getMaterialCode();

        List<MaterialIngredient> list = materialIngredientService.getByCode(code);

        return Result.ok(list);

    }


    @ApiOperation("新增原料")
    @PostMapping("save")
    public Result save(@RequestBody MaterialBasedata materialBasedata){

        boolean b = materialBasedataService.save(materialBasedata);
        if(b) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //根据id处理申请修改原料数据
    @ApiOperation("申请修改原料数据")
    @PostMapping("applyModify")
    public Result applyModify(@RequestBody ModifyDataVo vo){

        MaterialBasedata materialBasedata = materialBasedataService.getById(vo.getId());
        String materialCode = materialBasedata.getMaterialCode();
        List<MaterialIngredient> materialIngredients = materialIngredientService.getByCode(materialCode);

        //如果temp表中已经有了前端传回来的原料编码，那么要核实表中的原料编码是否处于修改/审核状态，若是则不允许申请
        List<MaterialBasedataTemp> tempList = materialBasedataTempService.getByCode(materialCode);


        Map<String,String> message = new HashMap<>();
        if(tempList.size() == 0) {
            MaterialBasedataTemp materialBasedataTemp = new MaterialBasedataTemp();
            materialBasedataTemp.setMaterialCode(materialCode);
            materialBasedataTemp.setMaterialName(materialBasedata.getMaterialName());
            materialBasedataTemp.setProductName(materialBasedata.getProductName());
            materialBasedataTemp.setProducterName(materialBasedata.getProducterName());
            materialBasedataTemp.setSubmitCode(materialBasedata.getSubmitCode());
            materialBasedataTemp.setInventoryClass(materialBasedata.getInventoryClass());
            materialBasedataTemp.setMaterialModel(materialBasedata.getMaterialModel());
            materialBasedataTemp.setMaterialSpecifications(materialBasedata.getMaterialSpecifications());
            materialBasedataTemp.setVersionNo(materialBasedata.getVersionNo());
            materialBasedataTemp.setRemark(materialBasedata.getRemark());
            materialBasedataTemp.setUpdateReason(vo.getReason());
            materialBasedataTemp.setCreateTime(GenerateCreateTime.generate());
            materialBasedataTemp.setIsConfirm(-2);
            materialBasedataTempService.save(materialBasedataTemp);

            if (materialIngredients.size() != 0) {
                for (MaterialIngredient materialIngredient : materialIngredients) {
                    MaterialIngredientTemp ingredientTemp = new MaterialIngredientTemp();
                    ingredientTemp.setMaterialCode(materialIngredient.getMaterialCode());
                    ingredientTemp.setMaterialName(materialIngredient.getMaterialName());
                    ingredientTemp.setCName(materialIngredient.getCName());
                    ingredientTemp.setInciName(materialIngredient.getInciName());
                    ingredientTemp.setIngredientContent(materialIngredient.getIngredientContent());
                    ingredientTemp.setRemark(materialIngredient.getRemark());
                    ingredientTemp.setCreateTime(GenerateCreateTime.generate());
                    materialIngredientTempService.save(ingredientTemp);
                }
            }
            materialBasedata.setIsConfirm(0);
            materialBasedataService.updateById(materialBasedata);
            message.put("status","成功");
            return Result.ok(message);
        }else{

            message.put("status","失败");
            return Result.ok(message);
        }
    }



















    //根据原料编码获取原料的基本信息和成分信息，用于编辑原料数据
    @ApiOperation("根据原料编码获取原料信息")
    @GetMapping("getMaterialDataByCode/{code}")
    public Result getMaterialDataByCode(@PathVariable String code){
        System.out.println(code);
        MaterialBasedata materialBasedata = materialBasedataService.getByCode(code);
        List<MaterialIngredient> ingredients = materialIngredientService.getByCode(code);
        Material material = new Material();
        if(materialBasedata == null){
            material.setStatus(-100);    //未查到编码为 code 的原料信息
            return Result.ok(material);
        }else if (ingredients.size() == 0){
            material.setStatus(-200);    //未查到编码 code 的原料成分信息
            return Result.ok(material);
        }else{
/*            BigDecimal sum = new BigDecimal(0.000000);
            for (MaterialIngredient ingredient : ingredients) {
                sum = ingredient.getIngredientContent().add(sum);
            }
            material.setSumContent(sum);*/
            material.setId(materialBasedata.getId());
            material.setMaterialCode(code);
            material.setStatus(materialBasedata.getStatus());
            material.setMaterialName(materialBasedata.getMaterialName());
            material.setProductName(materialBasedata.getProductName());
            material.setProducterName(materialBasedata.getProducterName());
            material.setSubmitCode(materialBasedata.getSubmitCode());
            material.setInventoryClass(materialBasedata.getInventoryClass());
            material.setMaterialModel(materialBasedata.getMaterialModel());
            material.setMaterialSpecifications(materialBasedata.getMaterialSpecifications());
            material.setRemark(materialBasedata.getRemark());
            material.setIsConfirm(materialBasedata.getIsConfirm());
            material.setMaterialIngredients(ingredients);

            return Result.ok(material);
        }

    }

    //原料信息修改页面点击原料数据修改申请列表查看细节按钮后根据id获取原料数据





    //获取material_basedata_temp中前limit条的数据
    @ApiOperation("获取modify数据")
    @GetMapping("getListOfModify")
    public Result getListOfModify(){
        DateFormat af = new SimpleDateFormat("yyyy-MM-dd");
        Integer limit = 100;
        List<MaterialBasedataTemp> tempList = materialBasedataTempService.getListOfModify(limit);

        List<Map<String,Object>> resultList = new ArrayList<>();
        for (MaterialBasedataTemp temp : tempList) {
            Map<String,Object> result = new HashMap<>();
            result.put("id",temp.getId());
            result.put("materialCode",temp.getMaterialCode());
            result.put("isConfirm",temp.getIsConfirm());
            result.put("applyStatus", IsConfirmEnum.getApplyStatus(temp.getIsConfirm()));
            result.put("applyDate",af.format(temp.getCreateTime()));
            result.put("versionNo",temp.getVersionNo());
            resultList.add(result);
        }
        return Result.ok(resultList);
    }



    //原料信息管理页面,点击原料数据修改申请列表查看细节按钮后根据id获取原料数据
    //原料信息变更管理页面,点击原料数据修改申请列表查看细节按钮，获得所在行的id，根据id获得数据信息，并根据数据信息中的material_code获取其ingredient信息
    @ApiOperation("根据id获取原料的temp数据")
    @GetMapping("getDetailById/{id}")
    public Result getDetailById(@PathVariable Integer id){
        Map<String,Object> materialData = new HashMap<>();
        BigDecimal sum = new BigDecimal(0);
        MaterialBasedataTemp basedataTemp = materialBasedataTempService.getById(id);
        if(basedataTemp.getIsConfirm() == 1){
            List<MaterialIngredient> ingredientList = materialIngredientService.getByCode(basedataTemp.getMaterialCode());
            for (MaterialIngredient ingredient : ingredientList) {
                sum = sum.add(ingredient.getIngredientContent());
            }
            materialData.put("ingredientList",ingredientList);
            materialData.put("sum",sum);
        }else {
            List<MaterialIngredientTemp> ingredientTempList = materialIngredientTempService.getByCode(basedataTemp.getMaterialCode());
            for (MaterialIngredientTemp ingredientTemp : ingredientTempList) {
                sum = sum.add(ingredientTemp.getIngredientContent());
            }
            materialData.put("ingredientList",ingredientTempList);
            materialData.put("sum",sum);
        }

        materialData.put("materialData",basedataTemp);
        //materialData.put("ingredientList",ingredientTempList);
/*        for (MaterialIngredientTemp ingredientTemp : ingredientTempList) {
            System.out.println(ingredientTemp.getCName());
        }*/
        return Result.ok(materialData);
    }



    //同意原料信息修改（主管权限里面）
    @ApiOperation("根据basetemp中的id，修改is_confirm为-1")
    @GetMapping("agreeForEdit/{id}")
    public Result agreeForEdit(@PathVariable Integer id){

        //要注意，这里操作的是temp表，要注意此id是material_basedata_temp中的id
        MaterialBasedataTemp materialBasedataTemp = materialBasedataTempService.getById(id);

        materialBasedataTemp.setIsConfirm(-1);
        boolean b = materialBasedataTempService.updateById(materialBasedataTemp);

        Map<String,String> message = new HashMap<>();
        if(b){
            message.put("status","批准成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "批准失败");
            message.put("code", "0");
            return Result.ok(message);
        }
    }

    //驳回修改请求或修改后的审核，具体在代码中根据isConfirm的值进行区分（主管权限）
    @ApiOperation("原料驳回")
    @GetMapping("reject/{id}/{s}")
    public Result reject(@PathVariable Integer id,
                         @PathVariable Integer s){
        /*驳回策略
        一、驳回修改申请：
            1、修改data中的is_confirm为1
            2、创建字符串：驳回时间 + “被驳回修改申请”，记为驳回标记，驳回时间要精确到秒
            3、修改dataTemp中的is_confirm为-100，在备注的字符串前面接上驳回标记，逻辑删除dataTemp数据
            4、在componentTemp的备注字符串前面接上驳回标记，逻辑删除componentTemp数据
        二、驳回修改后的审核：
            1、data中的数据不动，is_confirm保持为0
            2、修改dataTemp中的is_confirm为-1（即回到同意修改、待修改状态，考虑到可能需要重新修改后再提交审核，所以这里的驳回为返回修改状态）
        */
        Map<String,String> message = new HashMap<>();
        //1 根据id获取dataTemp数据以及要处理的原料编码
        MaterialBasedataTemp dataTemp = materialBasedataTempService.getById(id);
        String materialCode = dataTemp.getMaterialCode();
        //2 根据记录中的is_confirm和s进行比对，如果相同则继续，反之返回异常
        if(s != dataTemp.getIsConfirm()){
            message.put("msg", "操作异常，前端的is_confirm与Temp表中的不一致");
            message.put("code", "0");
            return Result.ok(message);
        }else{
            //3 判断s的值，选择驳回修改申请还是驳回审核申请，前端已经对is_confirm不等于0或-2的情况进行了处理，这里进一步进行处理
            if(s == -2) {       //驳回修改申请
                //4 处理data数据
                //4.1 获取data数据
                MaterialBasedata data = materialBasedataService.getByCode(materialCode);
                //4.2 修改data中的is_confirm为1
                data.setIsConfirm(1);
                //4.3 修改data数据
                boolean updateData = materialBasedataService.updateById(data);
                //5 处理dataTemp数据
                //5.1 创建驳回标记
                DateFormat af = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String rejectMark = af.format(new Date()) + "被驳回修改申请";
                //5.2 修改dataTemp中的is_confirm为-100
                dataTemp.setIsConfirm(-100);
                //5.3 在dataTemp中的remark前面加上驳回标记
                dataTemp.setRemark(rejectMark + dataTemp.getRemark());
                //5.4 逻辑删除dataTemp
                boolean deleteDataTemp = materialBasedataTempService.removeById(dataTemp.getId());
                //5.5 在ingredientTemp的remark前面加上驳回标记，然后逻辑删除ingredientTemp
                boolean deleteIngredientTempList = true;
                List<MaterialIngredientTemp> ingredientTempList = materialIngredientTempService.getByCode(materialCode);
                for (MaterialIngredientTemp ingredientTemp : ingredientTempList) {
                    ingredientTemp.setRemark(rejectMark + ingredientTemp.getRemark());
                    boolean b = materialIngredientTempService.removeById(ingredientTemp.getId());
                    deleteIngredientTempList = deleteIngredientTempList && b;
                }

                if(updateData && deleteDataTemp && deleteIngredientTempList){
                    message.put("msg", "驳回申请成功");
                    message.put("code", "1");
                    return Result.ok(message);
                }else{
                    message.put("msg", "驳回申请失败");
                    message.put("code", "0");
                    return Result.ok(message);
                }


            } else if (s == 0) {//驳回审核申请
                //直接修改dataTemp的is_confirm为-1，更新表数据即可
                dataTemp.setIsConfirm(-1);
                boolean b = materialBasedataTempService.updateById(dataTemp);
                if(b){
                    message.put("msg", "驳回审核成功");
                    message.put("code", "1");
                    return Result.ok(message);
                }else{
                    message.put("msg", "驳回审核失败");
                    message.put("code", "0");
                    return Result.ok(message);
                }

            }else{              //is_confirm异常的状态
                message.put("msg", "数据异常，前端传回is_confirm不是0也不是-2");
                message.put("code", "0");
                return Result.ok(message);
            }
        }
    }


    //原料修改完成的数据提交审核（原料信息编辑里面的）



    //原料修改数据审核通过（主管管理权限里面的）
    //修改temp表中的is_confirm为1，并将temp表中的相关数据复制至data表中
    //构建历史数据
    @ApiOperation("根据Temp中的id，执行审核通过")
    @GetMapping("confirmForEdit/{id}")                      //注意：这里的id是Temp表中的id
    public Result confirmForEdit(@PathVariable Integer id){

        Map<String,String> message = new HashMap<>();

        boolean b =true;

        boolean listIsNotEmpty = true;          //用于标记ingredient表是否为空（新原料时ingredient表为空），如果为否（即true），则能够往ingredientHistory表中拷贝数据

        //1 拿到dataTemp表中的数据
        MaterialBasedataTemp dataTemp = materialBasedataTempService.getById(id);
        if(dataTemp == null){
            message.put("msg", "没有temp数据，审核失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
        //2 从dataTemp中拿到materialCode，用于从ingredientTemp表中获取数据
        String materialCode = dataTemp.getMaterialCode();
        List<MaterialIngredientTemp> tempList = materialIngredientTempService.getByCode(materialCode);
        if(tempList.size() == 0){
            message.put("msg", "没有tempList数据，审核失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
        //3 根据materialCode获取data表中的数据和ingredient表中的数据
        MaterialBasedata data = materialBasedataService.getByCode(materialCode);
        if(data == null){
            data.setMaterialCode(materialCode);
        }
        List<MaterialIngredient> list = materialIngredientService.getByCode(materialCode);
        if(list.size() == 0){
            list.add(new MaterialIngredient());
            listIsNotEmpty = false;
        }
        //4 将data和list拷贝到history表中
        MaterialBasedataHistory materialBasedataHistory = new MaterialBasedataHistory();
        materialBasedataHistory.setMaterialCode(data.getMaterialCode());
        materialBasedataHistory.setMaterialName(data.getMaterialName());
        materialBasedataHistory.setProductName(data.getProductName());
        materialBasedataHistory.setProducterName(data.getProducterName());
        materialBasedataHistory.setSubmitCode(data.getSubmitCode());
        materialBasedataHistory.setInventoryClass(data.getInventoryClass());
        materialBasedataHistory.setMaterialModel(data.getMaterialModel());
        materialBasedataHistory.setMaterialSpecifications(data.getMaterialSpecifications());
        materialBasedataHistory.setVersionNo(data.getVersionNo());
        materialBasedataHistory.setRemark(data.getRemark());
        materialBasedataHistory.setCreateTime(data.getCreateTime());            //这个保存的是历史版本数据的创建时间

        boolean saveHistoryData = materialBasedataHistoryService.save(materialBasedataHistory);
        boolean saveHistoryList = true;
        if(listIsNotEmpty) {
            for (MaterialIngredient ingredient : list) {
                MaterialIngredientHistory materialIngredientHistory = new MaterialIngredientHistory();
                materialIngredientHistory.setMaterialCode(ingredient.getMaterialCode());
                materialIngredientHistory.setMaterialName(ingredient.getMaterialName());
                materialIngredientHistory.setCName(ingredient.getCName());
                materialIngredientHistory.setInciName(ingredient.getInciName());
                materialIngredientHistory.setIngredientContent(ingredient.getIngredientContent());
                materialIngredientHistory.setVersionNo(data.getVersionNo());            //ingredient表中不保存version_no
                materialIngredientHistory.setRemark(ingredient.getRemark());
                materialIngredientHistory.setCreateTime(ingredient.getCreateTime());
                boolean save = materialIngredientHistoryService.save(materialIngredientHistory);
                saveHistoryList = saveHistoryList && save;
            }
        }

        //5 将Temp表中的数据保存到data和ingredientTemp表中
        //5.1 data表中采取更新的方法
        data.setMaterialCode(dataTemp.getMaterialCode());           //在前端，materialCode应该是不能更改的
        data.setMaterialName(dataTemp.getMaterialName());
        data.setProductName(dataTemp.getProductName());
        data.setProducterName(dataTemp.getProducterName());
        data.setSubmitCode(dataTemp.getSubmitCode());
        data.setInventoryClass(dataTemp.getInventoryClass());
        data.setMaterialModel(dataTemp.getMaterialModel());
        data.setMaterialSpecifications(dataTemp.getMaterialSpecifications());
        data.setVersionNo(data.getVersionNo() + 1);                 //version_no直接从data表中拿
        data.setRemark(dataTemp.getRemark());
        data.setIsConfirm(1);                                       //审核成功，将is_confirm改为1，同时也将temp中的改为1
        dataTemp.setIsConfirm(1);

        boolean updateData = materialBasedataService.updateById(data);
        materialBasedataTempService.updateById(dataTemp);
        //为了避免往期完成修改的数据回显时导致原料数据的大量重复，将往期修改完成的原料Temp数据和基本Temp数据均进行逻辑删除
        boolean deleteTempData = materialBasedataTempService.removeById(dataTemp.getId());
        boolean deleteList = true;
        //5.2 ingredient则先是逻辑删除原有数据，然后再保存新的数据
        //5.2.1 先删除原有数据，这里要加入listIsNotEmpty的判断
        if(listIsNotEmpty) {
            for (MaterialIngredient ingredient : list) {
                boolean b1 = materialIngredientService.removeById(ingredient.getId());
                deleteList = deleteList && b1;
            }
        }
        //5.2.2 将ingredientTemp中的新数据拷贝到ingredient中
        boolean saveList = true;
        for (MaterialIngredientTemp ingredientTemp : tempList) {
            MaterialIngredient materialIngredient = new MaterialIngredient();
            materialIngredient.setMaterialCode(ingredientTemp.getMaterialCode());
            materialIngredient.setMaterialName(ingredientTemp.getMaterialName());
            materialIngredient.setCName(ingredientTemp.getCName());
            materialIngredient.setInciName(ingredientTemp.getInciName());
            materialIngredient.setIngredientContent(ingredientTemp.getIngredientContent());
            materialIngredient.setRemark(ingredientTemp.getRemark());
            materialIngredient.setCreateTime(GenerateCreateTime.generate());
            boolean save = materialIngredientService.save(materialIngredient);
            saveList = saveList && save;
        }

        //5.2.3 逻辑删除ingredientTemp中的数据
        //由于在前端回显往期修改数据时，往期修改的数据的基本数据虽然不重复，但是成分数据却会出现大量的重复（基本数据是一条一条的，成分数据确实所有未删除的数据组成的列表）
        //所以为了避免这个问题，直接在前端不回显往期已经完成修改的数据，即将dataTemp和ingredientTemp中的记录均进行逻辑删除
        boolean deleteTempList = true;
        for (MaterialIngredientTemp ingredientTemp : tempList) {
            boolean b1 = materialIngredientTempService.removeById(ingredientTemp.getId());
            deleteTempList = deleteTempList && b1;
        }

        b = saveHistoryData && saveHistoryList && updateData && deleteTempData && deleteTempList && saveList;

        if(b){
            message.put("msg","审核成功，已修改/新增原料");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("msg", "审核失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }

        /*
        //先获得basetemp中的对应id的数据，要注意此id是material_basedata_temp中的id
        MaterialBasedataTemp basedataTemp = materialBasedataTempService.getById(id);
        //获得其code
        String materialCode = basedataTemp.getMaterialCode();
        //获得其ingredienttemp中的数据
        List<MaterialIngredientTemp> ingredientTemps = materialIngredientTempService.getByCode(materialCode);
        //根据code修改basedata中的对应数据，由于code具有唯一性，所以可以使用code来找到对应的记录并修改
        //先要根据code获得basedata中的数据
        MaterialBasedata materialBasedata = materialBasedataService.getByCode(materialCode);
        //然后将basedataTemp中的每一个字段拷贝到materialBasedata的对应字段中
        //注意：createTime不是在这里生成，而是在新增原料的接口中生成
        materialBasedata.setMaterialName(basedataTemp.getMaterialName());
        materialBasedata.setProductName(basedataTemp.getProductName());
        materialBasedata.setProducterName(basedataTemp.getProducterName());
        materialBasedata.setSubmitCode(basedataTemp.getSubmitCode());
        materialBasedata.setInventoryClass(basedataTemp.getInventoryClass());
        materialBasedata.setMaterialModel(basedataTemp.getMaterialModel());
        materialBasedata.setMaterialSpecifications(basedataTemp.getMaterialSpecifications());
        materialBasedata.setRemark(basedataTemp.getRemark());
        materialBasedata.setIsConfirm(1);
        //拷贝完成后，更新basedata数据
        boolean b1 = materialBasedataService.updateById(materialBasedata);
        //修改basedataTemp中的isConfirm为1
        basedataTemp.setIsConfirm(1);
        //保存修改
        boolean b2 = materialBasedataTempService.updateById(basedataTemp);

        //逻辑删除ingredient表中的对应code的数据
        materialIngredientService.deleteByCode(materialCode);

        //将ingredientTemp中的数据逐个拷贝到一个新的ingredient中，并逐个保存至ingredient表中

        boolean b3;
        b3 = true;
        for (MaterialIngredientTemp ingredientTemp : ingredientTemps) {
            MaterialIngredient ingredient = new MaterialIngredient();
            ingredient.setMaterialCode(materialCode);
            ingredient.setMaterialName(ingredientTemp.getMaterialName());
            ingredient.setCName(ingredientTemp.getCName());
            ingredient.setInciName(ingredientTemp.getInciName());
            ingredient.setIngredientContent(ingredientTemp.getIngredientContent());
            ingredient.setRemark(ingredientTemp.getRemark());
            ingredient.setCreateTime(GenerateCreateTime.generate());
            boolean ingredientSave = materialIngredientService.save(ingredient);
            b3 = b3 && ingredientSave;
        }

        //删除ingredientTemp表中对应code的数据
        boolean b4;
        b4 = true;
        for (MaterialIngredientTemp ingredientTemp : ingredientTemps) {

            boolean ingredientTempDelete = materialIngredientTempService.removeById(ingredientTemp.getId());
            b4 = b4 && ingredientTempDelete;
        }

        boolean b;
        b = true;
        b = b1 && b2 && b3 && b4;


        Map<String,String> message = new HashMap<>();

        if(b){
            message.put("status","审核成功，已修改/新增原料");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "审核失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
        */
    }


    //获取可以用做原料编码前三位的字符串列表，这个列表直接通过数据库进行管理，不在前端做管理页面，这里直接通过sql语句来进行
    @ApiOperation("获取原料编码前三位")
    @GetMapping("getListOfCodePrefix")
    public Result getListOfCodePrefix(){

        List<String> prefixList = materialBasedataService.getPrefixList();
        Map<String,Object> result = new HashMap<>();
        result.put("prefixList",prefixList);

        return Result.ok(result);
    }

    //根据新原料编码的前缀获取原料编码后缀的建议列表
    @ApiOperation("获取原料编码后缀的建议列表")
    @GetMapping("getListOfCodeSuffix/{prefix}")
    public Result getListOfCodeSuffix(@PathVariable String prefix){
        //根据prefix获取material_basedata中的material_code的未使用过的后三位列表（001～999）
        List<String> list = materialBasedataService.getSuffixByPrefix(prefix);
        for (String s : list) {
            System.out.println(s);
        }
        return Result.ok(list);
    }


    //对前端传回的新增原料的原料编码进行查重
    @ApiOperation("原料编码查重")
    @GetMapping("duplicateCheckForNewCode/{newCode}")
    public Result duplicateCheckForNewCode(@PathVariable String newCode){
        Map<String,String> message = new HashMap<>();
        MaterialBasedata byCode = materialBasedataService.getByCode(newCode);
        if(byCode == null){
            message.put("status","成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "原料编码：" + newCode + "已被使用，无法创建新增原料");
            message.put("code", "0");
            return Result.ok(message);
        }

    }

    //通过查重的原料编码可以作为新增原料的原料编码，并据此创建新增原料
    //这部分代码和上一部分代码可以合并，但是为了前端确认新增原料，这里分成两部分
    @ApiOperation("新增原料")
    @GetMapping("createNewMaterial/{newCode}")
    public Result createNewMaterial(@PathVariable String newCode){

        MaterialBasedata materialBasedata = new MaterialBasedata();
        materialBasedata.setMaterialCode(newCode);
        materialBasedata.setCreateTime(GenerateCreateTime.generate());
        //设置新创建的新增原料的is_confirm状态为0
        materialBasedata.setIsConfirm(0);
        materialBasedata.setVersionNo(0);
        boolean save = materialBasedataService.save(materialBasedata);
        Map<String,String> message = new HashMap<>();
        if(save){
            //新增原料创建成功后，将其数据拷贝一份至material_basedata_temp数据表中，并设置其is_confirm为-10
            MaterialBasedataTemp materialBasedataTemp = new MaterialBasedataTemp();
            materialBasedataTemp.setMaterialCode(newCode);
            materialBasedataTemp.setIsConfirm(-10);
            materialBasedataTemp.setVersionNo(0);
            materialBasedataTemp.setCreateTime(GenerateCreateTime.generate());
            materialBasedataTempService.save(materialBasedataTemp);
            message.put("status","新增原料 " + newCode + " 创建成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "新增原料：" + newCode + "创建失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }



    //通过原料编码给其添加成分，这个接口没有考虑到原料成分信息的非空属性，要用addNewIngredient方法
    /*
    @ApiOperation("根据原料编码添加成分")
    @GetMapping("addIngredient/{code}/{name}")
    public Result addIngredient(@PathVariable String code,@PathVariable String name){
        MaterialIngredientTemp ingredientTemp = new MaterialIngredientTemp();
        ingredientTemp.setMaterialCode(code);
        ingredientTemp.setMaterialName(name);
        ingredientTemp.setCreateTime(GenerateCreateTime.generate());
        boolean b = materialIngredientTempService.save(ingredientTemp);

        Map<String,String> message = new HashMap<>();

        if(b){
            message.put("status","添加成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "添加成分失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }
    */

    //根据新成分的标准中文名获取相应的建议列表
    @ApiOperation("获取标准中文名列表")
    @GetMapping("getListOfCname")
    //由于有的cname过长，这里改为用params传参，这里要注意url传参的长度限制
    public Result getListOfCname(CnameVo vo){
        String newCname = vo.getCname();
        List<String> listOfCname = materialIngredientTempService.getSuggestListOfCname(newCname);

        return Result.ok(listOfCname);
    }


    //根据成分标准中文名获取其在2021已使用原料目录中的相应信息
    @ApiOperation("根据成分标准中文名获取其相应信息")
    @GetMapping("getInfoOfIngredient")
    //由于有的cname过长，这里改为用params传参
    public Result getInfoOfIngredient(CnameVo vo){
        String newCname = vo.getCname();
        List<String> inciNames = materialIngredientTempService.getInfoByCname(newCname);

        return Result.ok(inciNames);
    }


    //保存原料的新增成分
    @ApiOperation("保存新增成分")
    @PostMapping("addNewIngredient")
    public Result addNewIngredient(@RequestBody IngredientVo vo){

        MaterialIngredientTemp ingredientTemp = new MaterialIngredientTemp();

        ingredientTemp.setMaterialCode(vo.getMaterialCode());
        ingredientTemp.setMaterialName(vo.getMaterialName());
        ingredientTemp.setCName(vo.getCname());
        ingredientTemp.setInciName(vo.getInciName());
        ingredientTemp.setIngredientContent(vo.getIngredientContent());
        ingredientTemp.setRemark(vo.getRemark());
        ingredientTemp.setCreateTime(GenerateCreateTime.generate());

        boolean b = materialIngredientTempService.save(ingredientTemp);
        Map<String,String> message = new HashMap<>();

        if(b){
            message.put("status","添加成分成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "添加成分失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }

    }

    //根据ingredientTemp表中的id逻辑删除原料的成分
    @ApiOperation("逻辑删除原料的成分")
    @DeleteMapping("deleteIngredientForMaterialById/{id}")
    public Result deleteIngredientForMaterialById(@PathVariable Integer id){

        boolean b = materialIngredientTempService.removeById(id);

        Map<String,String> message = new HashMap<>();

        if(b){
            message.put("status","删除成分成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "删除成分失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }


    //原料信息编辑完成后点击保存修改并提交审核按钮
    @ApiOperation("保存原料修改后的数据")
    @GetMapping("submitForReview/{id}")
    public Result submitForReview(@PathVariable Integer id){
        MaterialBasedataTemp byId = materialBasedataTempService.getById(id);
        byId.setIsConfirm(0);
        boolean b = materialBasedataTempService.updateById(byId);
        Map<String,String> message = new HashMap<>();
        if(b){
            message.put("msg","提交审核成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("msg", "提交审核失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
/*
        MaterialBasedataTemp editMaterialData = vo.getMaterialData();
        List<MaterialIngredientTemp> editIngredientList = vo.getIngredientList();

        boolean b1;
        boolean b2;
        b1 = true;
        b2 = true;
        for (MaterialIngredientTemp ingredientTemp : editIngredientList) {
            //根据id获取ingredientTemp表中的数据，如果结果为null，说明表中无此记录或此记录已被逻辑删除，那么执行save操作，反之执行update操作
            MaterialIngredientTemp byId = materialIngredientTempService.getById(ingredientTemp.getId());
            boolean b;
            if(byId != null){
                b = materialIngredientTempService.updateById(ingredientTemp);
            }else {
                b = materialIngredientTempService.save(ingredientTemp);
            }
            //b1用于汇总是否每一条ingredientTemp的数据均成功保存
            b1 = b1 && b;
        }

        //如果每一条ingredientTemp的数据均成功保存，在保存materialDataTemp，并将其is_confirm置为0（待审核），反之置为-200，表示此记录的原料成分保存异常
        if(b1){
            editMaterialData.setIsConfirm(0);
            b2 = materialBasedataTempService.updateById(editMaterialData);
        }else{
            editMaterialData.setIsConfirm(-200);
            b2 = materialBasedataTempService.updateById(editMaterialData);
        }

        Map<String,String> message = new HashMap<>();
        boolean b = true;
        b = b1 && b2;
        if(b){
            message.put("status","保存成功，待审核");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "保存失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }*/
    }

    //原料基本信息输入发生改变时，自动保存所有的原料基本信息数据
    @ApiOperation("保存原料基本信息数据")
    @PostMapping("autoSaveMaterialData")
    public Result autoSaveMaterialData(@RequestBody MaterialBasedataTemp materialBasedataTemp){

        boolean b = materialBasedataTempService.updateById(materialBasedataTemp);
        Map<String,String> message = new HashMap<>();

        if(b){
            message.put("status","修改成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "修改失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }

    //原料成分信息输入发生改变时，自动保存所有的原料成分信息数据（单行）
    @ApiOperation("保存单行原料成分修改后的信息")
    @PostMapping("autoSaveIngredientData")
    public Result autoSaveIngredientData(@RequestBody MaterialIngredientTemp ingredientTemp){

        Integer ingredientTempId = ingredientTemp.getId();
        MaterialIngredientTemp byId = materialIngredientTempService.getById(ingredientTempId);
        byId.setIngredientContent(ingredientTemp.getIngredientContent());
        byId.setRemark(ingredientTemp.getRemark());

        Map<String,String> message = new HashMap<>();
        //这里不能直接保存从前端传来的数据，因为前端传来的数据并没有涵盖表格中的所有字段
        //所以需要通过上面的代码将前端传回来的部分数据拷贝入通过id获取的记录中
        boolean b = materialIngredientTempService.updateById(byId);
        if(b){
            message.put("status","修改成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "修改失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }

    //根据selectId(temp)获取原料编码对应的文件列表
    //getFileListByMaterialTempId
    @ApiOperation("根据selectId(temp)获取原料编码对应的文件列表")
    @GetMapping("getFileListByMaterialTempId/{materialTempId}")
    public Result getFileListByMaterialTempId(@PathVariable Integer materialTempId){

        MaterialBasedataTemp byId = materialBasedataTempService.getById(materialTempId);
        String materialCode = byId.getMaterialCode();
        MaterialBasedata materialBasedata = materialBasedataService.getByCode(materialCode);
        Integer materialBasedataId = materialBasedata.getId();
        List<FileData> fileListByMaterialId = fileDataService.getFileListByMaterialId(materialBasedataId,"material_basedata");
        if(fileListByMaterialId.size() > 0){
            for (FileData fileData : fileListByMaterialId) {
                fileData.setFileName(FileUtil.getFileNameCategory(fileData));
            }
        }
        return Result.ok(fileListByMaterialId);
    }

















}

