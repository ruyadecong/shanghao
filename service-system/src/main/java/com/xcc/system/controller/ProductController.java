package com.xcc.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.common.utils.FileUtil;
import com.xcc.common.utils.GenerateCreateTime;
import com.xcc.common.utils.JwtHelper;
import com.xcc.common.utils.ReturnMessage;
import com.xcc.model.base.ExportStatusEnum;
import com.xcc.model.base.IsConfirmEnum;
import com.xcc.model.product.*;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.qc.ProductStandardList;
import com.xcc.model.qc.StandardList;
import com.xcc.model.system.SysRole;
import com.xcc.model.technology.FileData;
import com.xcc.model.technology.FormulaData;
import com.xcc.model.technology.FormulaDataVo;
import com.xcc.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.PeriodUnit;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "产品管理接口")
@RestController
@RequestMapping("/admin/technology/Product")
public class ProductController {

    @Autowired
    ProductDataService productDataService;

    @Autowired
    ProductFormulaService productFormulaService;

    @Autowired
    FileCategoryService fileCategoryService;

    @Autowired
    FileDataService fileDataService;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    ProductFormulaTempService productFormulaTempService;

    @Autowired
    ProductDataHistoryService productDataHistoryService;

    @Autowired
    StandardListService standardListService;

    @Autowired
    FormulaDataService formulaDataService;

    @Autowired
    ProductCompositionService productCompositionService;

    @Autowired
    ProductStandardListService productStandardListService;


    //分页获取所有的产品数据，同时将产品关联的文件信息打包进传回前端的data中，还要打包进页面关联的文件类别
    @ApiOperation("分页获取产品信息")
    @GetMapping("{page}/{limit}")
    public Result getPageList(@PathVariable Integer page,
                              @PathVariable Integer limit,
                              ProductVo vo
                              ){
        Map<String,Object> result = new HashMap<>();

        //将前端传回的searchModel转换为onlyExport的代码
        // 前端：["所有产品","仅出口","有内销","不确定"]
        // 数据库：（0：有内销；1：仅出口；2：不确定）
        if ("仅出口".equals(vo.getSearchModel())) {
            vo.setOnlyExport(1);
        } else if ("有内销".equals(vo.getSearchModel())) {
            vo.setOnlyExport(0);
        } else if ("不确定".equals(vo.getSearchModel())) {
            vo.setOnlyExport(2);
        } else {
            vo.setSearchModel("");
        }

        //处理前端传回的“所有标准”
        if ("所有标准".equals(vo.getExecutiveStandard())){
            vo.setExecutiveStandard(null);
        }

        Page<ProductData> pageParam = new Page<>(page,limit);

        IPage<ProductData> pageModel = productDataService.selectPage(pageParam,vo);



        //获取目前表格所关联的文件类型数据
        List<String> categoryList = fileCategoryService.getListOfCategoryNameByTableName(vo.getTableName());
        result.put("categoryList",categoryList);

        //获取页面数据中所有id对应的文件列表
        for (ProductData record : pageModel.getRecords()) {
            //在file_data表中根据material_basedata表中的id获取相应的文件列表（获取过程要加入表格名称判断，以获取指定表格中可以显示的文件类型）
            List<FileData> fileDataList = fileDataService.getFileListByMaterialId(record.getId(),vo.getTableName());
            record.setExportStatue(ExportStatusEnum.getExportStatus(record.getOnlyExport()));
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
        //roleForFileManager.add("purchase");
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

    //获取所有的产品标准（作为页面搜索栏的产品标准下拉列表项目）
    @ApiOperation("获取所有的产品标准")
    @GetMapping("getAllProductStandard")
    public Result getAllProductStandard(){
        List<FileData> fileDataList = fileDataService.getAllProductStandard();
        List<String> fileNameList = new ArrayList<>();
        fileNameList.add("所有标准");
        for (FileData fileData : fileDataList) {
            if(!fileNameList.contains(fileData.getFileName())) {
                fileNameList.add(fileData.getFileName());
            }
        }
        return Result.ok(fileNameList);
    }

    //根据产品id获取其配方数据
    @ApiOperation("根据产品id获取其配方数据")
    @GetMapping("getDataByProductId/{productId}")
    public Result getDataByProductId(@PathVariable Integer productId){
        //productFormula中的colorNumber在service中set
        List<ProductFormula> productFormulaList = productFormulaService.getFormulaListByProductId(productId);

        return Result.ok(productFormulaList);
    }


    //根据产品id申请修改数据
    //这个申请修改之后就开始将数据复制到temp中
    //如果被驳回了，则会修改已经复制了的temp数据，同时对其逻辑删除
    //由于产品数据不设temp表（产品的配方组成表有temp表），所以在这里产品数据申请修改之后要直接保存到history表中
    //保存到history表中的时候要对history添加备注以明确申请修改原因
    //驳回的时候history表中也要添加备注以表明这次修改被驳回，同时逻辑删除
    @ApiOperation("根据产品id申请修改数据")
    @PostMapping("applyModify")
    public Result applyModify(@RequestBody ProductVo vo){

        String updateReason = vo.getModifyReason();
        System.out.println("///////////////////////////////////////////");
        System.out.println(updateReason);
        boolean b = true;
        ProductData data = productDataService.getById(vo.getId());
        String productCode = data.getProductCode();
        //根据产品编码获取其所有的配方数据
        List<ProductFormula> productFormulaList = productFormulaService.getByProductCode(productCode);

        //如果temp表中已经有了前端传回来的产品编码（is_deleted = 0），那么要核实表中的产品编码是否处于修改/审核状态，若是则不允许申请
        //上一次申请的进入temp表的formula数据，会在上一次审核通过后被逻辑删除
        List<ProductFormulaTemp> tempList = productFormulaTempService.getByProductCode(productCode);

        if(tempList.size() == 0){
            //将data的数据拷贝到history中
            ProductDataHistory history = new ProductDataHistory();
            history.setProductName(data.getProductName());
            history.setProductCode(data.getProductCode());
            history.setProductNumber(data.getProductNumber());
            history.setOnlyExport(data.getOnlyExport());
            history.setShelfLife(data.getShelfLife());
            history.setNetWeight(data.getNetWeight());
            history.setWeightModel(data.getWeightModel());
            history.setUnit(data.getUnit());
            history.setExecutiveStandard(data.getExecutiveStandard());
            history.setFilingNumber(data.getFilingNumber());
            history.setFilingPerson(data.getFilingPerson());
            history.setProductIntroduction(data.getProductIntroduction());
            history.setUsageMethod(data.getUsageMethod());
            history.setProductNote(data.getProductNote());
            history.setMarkOne(data.getMarkOne());
            history.setMarkTwo(data.getMarkTwo());
            history.setMarkThree(data.getMarkThree());
            history.setIsConfirm(10);
            history.setVersionNo(data.getVersionNo());
            history.setUpdateReason(updateReason);
            history.setRemark(data.getRemark());
            history.setStatus(false);
            history.setCreateTime(GenerateCreateTime.generate());

            //保存history数据
            boolean saveHistory = productDataHistoryService.save(history);

            if(productFormulaList.size() != 0){
                for (ProductFormula formula : productFormulaList) {
                    ProductFormulaTemp temp = new ProductFormulaTemp();
                    temp.setProductCode(productCode);
                    temp.setFormulaCode(formula.getFormulaCode());
                    temp.setColorNumber(formula.getColorNumber());
                    temp.setFormulaContent(formula.getFormulaContent());
                    temp.setFormulaMark(formula.getFormulaMark());
                    temp.setMarkOne(formula.getMarkOne());
                    temp.setMarkTwo(formula.getMarkTwo());
                    temp.setMarkThree(formula.getMarkThree());
                    temp.setRemark(formula.getRemark());
                    temp.setStatus(formula.getStatus());
                    temp.setCreateTime(GenerateCreateTime.generate());
                    boolean save = productFormulaTempService.save(temp);
                    b = b && save;
                }
            }

            data.setIsConfirm(-2);
            data.setUpdateReason(updateReason);
            boolean updateData = productDataService.updateById(data);

            b = b && saveHistory && updateData;
            if(b) {
                return Result.ok(ReturnMessage.message("1", "申请修改成功"));
            }else{
                return Result.ok(ReturnMessage.message("0","申请修改失败，请联系管理员"));
            }
        }else{
            return Result.ok(ReturnMessage.message("0","申请修改失败，temp表中已经有数据"));
        }
    }


    //产品编辑中用到的api

    //获取最近的100条申请列表数据（即获取product_data中的is_confirm为-2,-1,0的数据（前100条：通过limit设定））
    //注意，和material、formula不同，product表中没有temp表，需要直接从data表中获取修改、审核、申请状态的数据
    @ApiOperation("定量获取formula_data_temp中的数据")
    @GetMapping("getListOfModify")
    public Result getListOfModify(){
        DateFormat af = new SimpleDateFormat("yyyy-MM-dd");
        Integer limit = 100;
        QueryWrapper<ProductData> wrapper = new QueryWrapper<>();
        List<Integer> forWrapper = new ArrayList<>();
        forWrapper.add(-10);
        forWrapper.add(-2);
        forWrapper.add(-1);
        forWrapper.add(0);
        wrapper.in("is_confirm",forWrapper);
        List<ProductData> list = productDataService.list(wrapper);
        if(list.size() > 0){
            for (ProductData productData : list) {
                productData.setApplyDate(af.format(productData.getUpdateTime()));
                productData.setApplyStatus(IsConfirmEnum.getApplyStatus(productData.getIsConfirm()));
            }
        }
        return Result.ok(list);
    }

    //对前端传回的产品编码进行查重
    @ApiOperation("对前端传回的产品编码进行查重")
    @GetMapping("createNewProduct")
    public Result createNewProduct(ProductVo vo){
        //查重
        String newCode = vo.getProductCode();
        List<ProductData> list = productDataService.getByProductCode(newCode);
        if(list.size() != 0) {
            return Result.ok(ReturnMessage.message("0", "产品编码 " + newCode + " 已经存在！！！"));
        }else{
            ProductData productData = new ProductData();
            productData.setProductCode(newCode);
            productData.setProductName(newCode);
            productData.setCreateTime(GenerateCreateTime.generate());
            productData.setIsConfirm(-10);
            boolean save = productDataService.save(productData);
            if(save) {
                return Result.ok(ReturnMessage.message("1", "产品 " + newCode + " 新增成功"));
            }else{
                return Result.ok(ReturnMessage.message("0","产品 " + newCode + " 新增失败，请联系管理员"));
            }
        }
    }

    //获取可选择的产品标准列表
    @ApiOperation("获取可选择的产品标准列表")
    @GetMapping("getStandardList")
    public Result getStandardList(){

        List<StandardList> list = standardListService.list();
        for (StandardList standard : list) {
            String name = "（" + standard.getStandardClass() + "）" + standard.getStandardName() + "：" + standard.getStandardNumber();
            standard.setName(name);
        }
        return Result.ok(list);
    }


    //根据id获取产品的数据（包括配方信息和配方百分比合计）
    @ApiOperation("根据id获取产品的数据")
    @GetMapping("getDetailById/{productId}")
    public Result getDetailById(@PathVariable Integer productId){
        Map<String,Object> result = new HashMap<>();
        ProductData data = productDataService.getById(productId);
        List<ProductFormulaTemp> formulaTempList = productFormulaTempService.getByProductCode(data.getProductCode());
        BigDecimal sumOfContent = new BigDecimal(0);
        if(formulaTempList.size() > 0){
            for (ProductFormulaTemp temp : formulaTempList) {
                sumOfContent = sumOfContent.add(temp.getFormulaContent());
            }
        }
        result.put("productData",data);
        result.put("formulaList",formulaTempList);
        result.put("sum",sumOfContent);

        return Result.ok(result);
    }

    //根据产品id，从file_data表中获取关联的文件信息
    @ApiOperation("根据id获取文件信息")
    @GetMapping("getFileListByProductId/{productId}")
    public Result getFileListByProductId(@PathVariable Integer productId){
        String tableName = "product_data";
        //这个listOfCategoryNameByTableName是用于指定获取文件类型的
        List<String> listOfCategoryNameByTableName = fileCategoryService.getListOfCategoryNameByTableName(tableName);
        QueryWrapper<FileData> wrapper = new QueryWrapper<>();
        wrapper.eq("correlation_id",productId);
        wrapper.in("file_category",listOfCategoryNameByTableName);
        List<FileData> list = fileDataService.list(wrapper);
        return Result.ok(list);
    }

    //根据配方编码前几位获取建议列表
    @ApiOperation("根据配方编码前几位获取建议列表")
    @GetMapping("getListOfFormulaCode")
    public Result getListOfFormulaCode(FormulaDataVo vo){
        String formulaCode = vo.getFormulaCode();
        List<String> listOfSuggest = formulaDataService.getSuggestListOfFormulaCode(formulaCode);
        return Result.ok(listOfSuggest);
    }

    //根据配方编码获取配方数据（formula_data中的formulaCode是unique）
    @ApiOperation("根据配方编码获取配方数据")
    @GetMapping("getInfoOfFormula")
    public Result getInfoOfFormula(FormulaDataVo vo){
        String formulaCode = vo.getFormulaCode();
        FormulaData byCode = formulaDataService.getByCode(formulaCode);
        return Result.ok(byCode);
    }

    //根据配方数据新增配方记录
    @ApiOperation("根据配方数据新增配方记录")
    @PostMapping("addNewFormula")
    public Result addNewFormula(@RequestBody ProductFormulaTemp newFormula){

        //先对传回的配方编码从formula_data表中查询，看是否是已经存在的配方
        //但是这里不阻止不存在于与formula_data中的配方的添加，而是在提交审核的时候对所有的配方进行遍历，如果有配方不在formula_data中，那就阻止提交审核
        //注：同一个产品中允许同一个配方使用多次，所以不进行产品内配方查重
        String formulaCode = newFormula.getFormulaCode();
        FormulaData byCode = formulaDataService.getByCode(formulaCode);
        ProductFormulaTemp temp = new ProductFormulaTemp();
        temp.setProductCode(newFormula.getProductCode());
        temp.setFormulaCode(newFormula.getFormulaCode());
        //temp.setFormulaMark(newFormula.getFormulaMark());       //这里将配方在产品中的标记记录为formulaMark，也可以改成color_number
        temp.setFormulaContent(newFormula.getFormulaContent());
        temp.setRemark(newFormula.getRemark());
        temp.setCreateTime(GenerateCreateTime.generate());
        boolean save = productFormulaTempService.save(temp);
        if(save) {
            String msg;
            if (byCode == null) {
                msg = "添加成功，但配方" + formulaCode + "不存在与配方数据库中！！！";
            }else{
                msg = "添加成功";
            }
            return Result.ok(ReturnMessage.message("1", msg));
        }else {
            return Result.ok(ReturnMessage.message("0","添加失败，请联系管理员！！！"));
        }

    }


    //原料基本信息输入发生改变时，自动保存所有的原料基本信息数据
    @ApiOperation("自动修改产品信息")
    @PutMapping("autoSaveProductData")
    public Result autoSaveProductData(@RequestBody ProductData productData){
        boolean b = productDataService.updateById(productData);
        if(b){
            return Result.ok(ReturnMessage.message("1","修改成功"));
        }else{
            return Result.ok(ReturnMessage.message("0","修改失败，请联系管理员"));
        }
    }

    //配方信息输入发生改变时，自动保存所有的配方信息数据（单行）
    @ApiOperation("更新产品某个配方的数据")
    @PutMapping("autoSaveFormulaData")
    public Result autoSaveFormulaData(@RequestBody ProductFormulaTemp temp){
        boolean b = productFormulaTempService.updateById(temp);
        if(b){
            return Result.ok(ReturnMessage.message("1","修改成功"));
        }else{
            return Result.ok(ReturnMessage.message("0","修改失败，请联系管理员"));
        }
    }


    //根据product_formula_temp表中的id逻辑删除其中的formula记录
    @ApiOperation("根据id逻辑删除产品的配方")
    @DeleteMapping("deleteFormulaForProductById/{id}")
    public Result deleteFormulaForProductById(@PathVariable Integer id){

        boolean b = productFormulaTempService.removeById(id);
        if(b){
            return Result.ok(ReturnMessage.message("1","删除成功"));
        }else{
            return Result.ok(ReturnMessage.message("0","删除失败"));
        }
    }


    //修改完成，提交修改的数据待审核，修改product_data表中is_confirm为0
    //这个id是“产品数据修改申请列表”的id，指向product_data的记录
    @ApiOperation("提交配方数据进入审核状态")
    @GetMapping("submitForReview/{id}")
    public Result submitForReview(@PathVariable Integer id){
        List<String> invalidCodeList = new ArrayList<>();
        //1 根据id获取product_data中的记录，并提取其productCode
        ProductData productData = productDataService.getById(id);
        String productCode = productData.getProductCode();
        //2 根据productCode获取其在product_formula_temp中的所有配方的列表
        List<ProductFormulaTemp> formulaTempList = productFormulaTempService.getByProductCode(productCode);
        //3 遍历获取的配方列表，进一步核实配比合计是不是100%
        if("百分比".equals(productData.getWeightModel())) {
            BigDecimal sum = new BigDecimal(0);
            for (ProductFormulaTemp temp : formulaTempList) {
                sum = sum.add(temp.getFormulaContent());
            }
            if (sum.compareTo(new BigDecimal(100)) != 0) {
                return Result.ok(ReturnMessage.message("0", "提交审核失败（配方比例合计不是100），请联系管理员"));
            }
        } else if ("净含量".equals(productData.getWeightModel())) {
/*            BigDecimal sum = new BigDecimal(0);
            for (ProductFormulaTemp temp : formulaTempList) {
                sum = sum.add(temp.getFormulaContent());
            }
            if (sum.compareTo(productData.getNetWeight()) != 0) {
                return Result.ok(ReturnMessage.message("0", "提交审核失败（配方比例合计不是100），请联系管理员"));
            }*/
        } else {
            return Result.ok(ReturnMessage.message("2","产品净含量模式存在问题，请联系管理员！！！"));
        }
        //4 遍历获取的配方列表，看每一个配方是否在配方库中
        for (ProductFormulaTemp temp : formulaTempList) {
            String formulaCode = temp.getFormulaCode();
            FormulaData byCode = formulaDataService.getByCode(formulaCode);
            if(byCode == null){
                invalidCodeList.add(formulaCode);
            }
        }
        //5 判断invalidCodeList的长度
        if(invalidCodeList.size() == 0){
            //长度为0，说明所有的配方编码均可找到，那么可以放行进入审核
            productData.setIsConfirm(0);
            boolean b = productDataService.updateById(productData);
            if(b){
                return Result.ok(ReturnMessage.message("1","提交审核成功"));
            }else{
                return Result.ok(ReturnMessage.message("0","提交审核失败，请联系管理员"));
            }
        }else{
            //长度不为0，返回不在配方库中的配方编码
            String msg = "配方： ";
            for (String s : invalidCodeList) {
                msg = msg + s + "，";
            }
            msg = msg.substring(0,msg.length()-1);
            msg = msg + " 不存在于数据库中，不予提交审核";
            return Result.ok(ReturnMessage.message("0",msg));
        }
    }


    //以下为主管权限页面api

    //同意修改配方数据
    @ApiOperation("同意修改配方数据")
    @GetMapping("agreeForEdit/{id}")
    public Result agreeForEdit(@PathVariable Integer id){
        //将product_data中的is_confirm从-2改为-1
        ProductData productData = productDataService.getById(id);
        productData.setIsConfirm(-1);
        boolean b = productDataService.updateById(productData);
        if(b){
            return Result.ok(ReturnMessage.message("1","批准成功"));
        }else{
            return Result.ok(ReturnMessage.message("0","批准失败"));
        }
    }

    //审核通过修改之后的产品配方数据
    @ApiOperation("审核通过修改之后的产品配方数据")
    @GetMapping("confirmForEdit/{id}")
    public Result confirmForEdit(@PathVariable Integer id){
        boolean b = true;
        boolean deleteFormulaList = true;
        boolean saveFormulaList = true;
        boolean deleteTempFormulaList = true;

        //1 根据id获取product_data记录
        ProductData productData = productDataService.getById(id);
        productData.setVersionNo(productData.getVersionNo() + 1);           //version_no加1
        //2 获得product_code
        String productCode = productData.getProductCode();
        //3 根据productCode获得product_formula_temp中的配方列表
        List<ProductFormulaTemp> formulaTempList = productFormulaTempService.getByProductCode(productCode);
        //4 判断formulaTempList是否为空，若空则不能进行审核
        if(formulaTempList.size() == 0){
            return Result.ok(ReturnMessage.message("0","审核失败，产品的temp配方列表为空！！！"));
        }else{
            //5 获取可能存在的配方列表
            List<ProductFormula> formulaList = productFormulaService.getByProductCode(productCode);

            //6 判断formulaList是否为空，若不为空，则要先对其逻辑删除
            if(formulaList.size() != 0){
                for (ProductFormula productFormula : formulaList) {
                    boolean b1 = productFormulaService.removeById(productFormula.getId());
                    deleteFormulaList = deleteFormulaList && b1;
                }
            }
            if(!deleteFormulaList){
                return Result.ok(ReturnMessage.message("0","审核失败，产品的原配方列表删除失败！！！"));
            }
            //7 遍历formulaTempList，将其数据拷贝至product_formula表中
            for (ProductFormulaTemp temp : formulaTempList) {
                ProductFormula productFormula = new ProductFormula();
                productFormula.setProductCode(productCode);
                productFormula.setFormulaCode(temp.getFormulaCode());
                productFormula.setColorNumber(temp.getColorNumber());
                productFormula.setFormulaContent(temp.getFormulaContent());
                productFormula.setFormulaMark(temp.getFormulaMark());
                productFormula.setMarkOne(temp.getMarkOne());
                productFormula.setMarkTwo(temp.getMarkTwo());
                productFormula.setMarkThree(temp.getMarkThree());
                productFormula.setIsConfirm(1);
                productFormula.setVersionNo(productData.getVersionNo());
                productFormula.setRemark(temp.getRemark());
                productFormula.setStatus(true);
                productFormula.setCreateTime(GenerateCreateTime.generate());
                boolean save = productFormulaService.save(productFormula);
                saveFormulaList = saveFormulaList && save;
            }
            if(!saveFormulaList){
                return Result.ok(ReturnMessage.message("0","审核失败，配方列表拷贝失败！！！"));
            }
            //8 从product_formula_temp中逻辑删除formulaTempList
            for (ProductFormulaTemp temp : formulaTempList) {
                boolean b1 = productFormulaTempService.removeById(temp.getId());
                deleteTempFormulaList = deleteTempFormulaList && b1;
            }
            if(!deleteTempFormulaList){
                return Result.ok(ReturnMessage.message("0","审核失败，配方temp列表删除失败！！！"));
            }
            //9 修改productData的is_confirm为1，同时要删除productData的updateReason，审核通过后此字段仅留在history中
            productData.setIsConfirm(1);
            productData.setUpdateReason("");

            //加一个resultMsg，这样在后面文件关联出现问题时能够在不影响审核的情况下提示前端文件关联过程中出现的问题
            String resultMsg = "审核成功";
            String code = "1";

            //10 调整产品的执行标准关联数据
            //10.1 先拿到产品执行标准的名称
            //这个名称首先是从standard_list表中由standard_class和standard_name合并得到的，合并时standard_class由中文小括号所包裹
            //然后在file_data数据表中上传了同样file_name（同样的命名规则）的文件
            //这样就可以通过产品的executive_standard和产品id来查找file_data里面的关联记录
            String executiveStandard = productData.getExecutiveStandard();
            //10.2 通过产品的executive_standard来查找file_data里面的关联记录，即与executiveStandard相同的file_name记录
            QueryWrapper<FileData> wrapper = new QueryWrapper<>();
            wrapper.eq("file_name",executiveStandard);
            List<FileData> fileDataList = fileDataService.list(wrapper);        //注意：这个fileDataList只包含与executiveStandard相同file_name的数据
            //10.3 遍历fileDataList，找出correlation_id与产品id相同的记录
            List<FileData> existFileDataList = new ArrayList<>();
            if(fileDataList.size() > 0) {
                for (FileData fileData : fileDataList) {
                    if ((fileData.getCorrelationId() == productData.getId()) || (fileData.getCorrelationId().equals(productData.getId()))) {
                        existFileDataList.add(fileData);
                    }
                }
                //10.4 判断existFileDataList是否为空，如果为空，则要为产品关联标准文件
                if(existFileDataList.size() == 0){
                    //10.5 首先要根据product的id来找出这个product已经关联的标准文件
                    //由于existFileDataList为空，说明这些已经关联的标准文件不是当前所需要的（有关联的标准文件）
                    //那么就要先删除这些与产品关联的可能存在的标准文件的记录
                    //注意，只能删除file_category为“产品标准”和“尚好企业标准”的数据
                    //10.5.1 先要找出产品id关联的“尚好企业标准”和“产品标准”类文件
                    QueryWrapper<FileData> wrapper1 = new QueryWrapper<>();
                    wrapper1.eq("correlation_id",productData.getId());
                    List<String> fileCategoryList = new ArrayList<>();
                    fileCategoryList.add("产品标准");
                    fileCategoryList.add("尚好企业标准");
                    wrapper1.in("file_category",fileCategoryList);
                    List<FileData> list = fileDataService.list(wrapper1);
                    //10.5.2 如果存在这类文件，那么逻辑删除
                    if(list.size() > 0){
                        for (FileData fileData : list) {
                            fileDataService.removeById(fileData.getId());
                        }
                    }
                    //10.6 在file_data表中为此产品新增一条记录来关联标准文件
                    //fileDataList不为空（这个判断结果的代码块里面），说明file_data中是有这个文件的记录的，那么只要将其中的一个的记录的file_path拷贝给新增的记录的file_path字段
                    //10.6.1 先找出一个这样的记录，这里再加上一个判断已确定就是所需要的executiveStandard文件
                    FileData existFileData = new FileData();
                    for (FileData fileData : fileDataList) {
                        if(executiveStandard.equals(fileData.getFileName())){
                            existFileData = fileData;
                            break;
                        }
                    }
                    //10.6.2 将找出的文件记录逐字段赋给新的文件记录
                    FileData newFileData = new FileData();
                    newFileData.setCorrelationId(productData.getId());      //关键
                    newFileData.setOriginalName(existFileData.getOriginalName());
                    newFileData.setFileName(existFileData.getFileName());
                    newFileData.setFilePath(existFileData.getFilePath());       //关键
                    newFileData.setCategoryId(existFileData.getCategoryId());
                    newFileData.setFileCategory(existFileData.getFileCategory());
                    newFileData.setFileType(existFileData.getFileType());
                    newFileData.setUploadUser(existFileData.getUploadUser());
                    newFileData.setMarkOne(existFileData.getMarkOne());
                    newFileData.setMarkTwo(existFileData.getMarkTwo());
                    newFileData.setMarkThree(existFileData.getMarkThree());
                    newFileData.setMarkFour(existFileData.getMarkFour());
                    newFileData.setMarkFive(existFileData.getMarkFive());
                    newFileData.setVersionNo(existFileData.getVersionNo());
                    newFileData.setRemark(existFileData.getRemark());
                    newFileData.setStatus(true);
                    newFileData.setCreateTime(GenerateCreateTime.generate());
                    //10.6.3 保存文件记录
                    boolean save = fileDataService.save(newFileData);
                    if(!save){
                        code = "2";
                        resultMsg = "审核成功，但是为产品标准" + executiveStandard + "关联文件失败";
                    }

                }

            }else{
                code = "2";
                resultMsg = "审核成功，但是file_data中没有" + executiveStandard + "标准的文件记录";
            }


            //11 更新productData的记录
            boolean b1 = productDataService.updateById(productData);
            if(b1){
                return Result.ok(ReturnMessage.message(code,resultMsg));
            }else{
                return Result.ok(ReturnMessage.message("0","审核失败，产品数据更新失败！！！"));
            }
        }
    }

    //驳回修改申请或修改后审核
    @ApiOperation("驳回修改申请或修改后审核")
    @GetMapping("reject/{id}/{s}")
    public Result reject(@PathVariable Integer id,@PathVariable Integer s){
        /*驳回策略
        一、驳回修改申请：
            1、创建字符串：驳回时间 + “被驳回修改申请”，记为驳回标记，驳回时间要精确到秒
            2、修改data中的is_confirm为1，给其updateReason加上驳回标记
            3、修改history中的is_confirm为-100，在修改原因的字符串前面接上驳回标记
            4、在formulaTemp的备注字符串前面接上驳回标记，逻辑删除formulaTemp数据（这些数据实在修改申请的时候被拷贝进temp表的）
        二、驳回修改后的审核：
            1、修改data中的is_confirm为-1
            2、修改dataTemp中的is_confirm为-1（即回到同意修改、待修改状态，考虑到可能需要重新修改后再提交审核，所以这里的驳回为返回修改状态）
        */
        //1 根据id获取data数据
        ProductData productData = productDataService.getById(id);
        String productCode = productData.getProductCode();

        //2 根据记录中的is_confirm和s进行比对，如果相同则继续，反之返回异常
        if(s != productData.getIsConfirm()){
            return Result.ok(ReturnMessage.message("0","操作异常，前端的is_confirm与product_data表中的不一致"));
        }
        //3 判断s的值，选择驳回修改申请还是驳回审核申请，前端已经对is_confirm不等于0或-2的情况进行了处理，这里进一步进行处理
        if(s == -2){        //需要驳回的是修改申请
            //4 处理productData数据
            //4.1 创建驳回标记
            DateFormat af = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String rejectMark = af.format(new Date()) + "被驳回修改申请";
            //4.2 修改productData中的is_confirm为1
            productData.setIsConfirm(1);
            productData.setUpdateReason(productData.getUpdateReason() + " ： " + rejectMark);
            //4.3 更新productData
            boolean b = productDataService.updateById(productData);
            if(!b){
                return Result.ok(ReturnMessage.message("0","驳回失败，productData数据修改失败"));
            }
            //5 处理productDataHistory数据
            //5.1 先根据productCode获取最新的history数据
            ProductDataHistory lastHistory = productDataHistoryService.getLastOneByProductCode(productCode);
            //5.2 修改最新的history数据的is_confirm为-100，并给updateReason加上rejectMark
            lastHistory.setIsConfirm(-100);
            lastHistory.setUpdateReason(lastHistory.getUpdateReason() + " ： " + rejectMark);
            //5.3 更新数据
            boolean b1 = productDataHistoryService.updateById(lastHistory);
            if(!b1){
                return Result.ok(ReturnMessage.message("0","驳回失败，productDataHistory数据修改失败"));
            }
            //6 处理formulaTemp数据
            //6.1 根据productCode获取formulaTemp表中的数据列表
            List<ProductFormulaTemp> formulaTempList = productFormulaTempService.getByProductCode(productCode);
            boolean deleteTempFormula = true;
            if(formulaTempList.size() != 0){
                for (ProductFormulaTemp temp : formulaTempList) {
                    boolean b2 = productFormulaTempService.removeById(temp.getId());
                    deleteTempFormula = deleteTempFormula && b2;
                }
            }
            if(!deleteTempFormula){
                return Result.ok(ReturnMessage.message("0","驳回失败，productFormulaTemp数据删除失败"));
            }
            return Result.ok(ReturnMessage.message("1","驳回数据修改申请成功"));
        } else if (s == 0) {        //需要驳回的是审核申请
            //直接修改productData的is_confirm为-1，然后更新数据即可
            productData.setIsConfirm(-1);
            boolean b = productDataService.updateById(productData);
            if(b){
                return Result.ok(ReturnMessage.message("1","驳回审核成功"));
            }else{
                return Result.ok(ReturnMessage.message("0","驳回审核失败"));
            }
        }else{
            return Result.ok(ReturnMessage.message("0", "数据异常，前端传回is_confirm不是0也不是-2"));
        }
    }



    //以下的代码是关于产品组件的
    @ApiOperation("分页获取产品组件数据")
    @GetMapping("getAllProductForComposition/{page}/{limit}")
    public Result getAllProductForComposition(@PathVariable Integer page,
                                              @PathVariable Integer limit,
                                              ProductVo vo){
        Page<ProductData> pageParam = new Page<>(page,limit);

        IPage<ProductData> pageModel = productDataService.selectPageForComposition(pageParam,vo);

        return Result.ok(pageModel);
    }

    //根据所选产品的id返回相关的组件数据
    @ApiOperation("根据产品id获得组件")
    @GetMapping("getCompositionById/{productId}")
    public Result getCompositionById(@PathVariable Integer productId){
        QueryWrapper<ProductComposition> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id",productId);
        wrapper.eq("is_deleted",0);
        wrapper.orderByAsc("rank_number");
        List<ProductComposition> list = productCompositionService.list(wrapper);
        return Result.ok(list);
    }



    //根据产品编码获取产品的配方列表
    @ApiOperation("根据产品编码获取产品的配方列表")
    @GetMapping("getFormulaById/{id}")
    public Result getFormulaById(@PathVariable Integer id){
        ProductData byId = productDataService.getById(id);
        String unit ="未知";
        if("百分比".equals(byId.getWeightModel())){
            unit = "%";
        }
        if("净含量".equals(byId.getWeightModel())){
            unit = byId.getUnit();
        }
        List<ProductFormula> byProductCode = productFormulaService.getByProductCode(byId.getProductCode());
        for (ProductFormula productFormula : byProductCode) {
            productFormula.setUnit(unit);
        }
        return Result.ok(byProductCode);
    }

    //给产品增加新组件
    @ApiOperation("给产品增加新组件")
    @PostMapping("addNewCompositionForProduct")
    public Result addNewCompositionForProduct(@RequestBody ProductComposition composition){
        composition.setCreateTime(GenerateCreateTime.generate());
        boolean save = productCompositionService.save(composition);
        if(save){
            return Result.ok(ReturnMessage.message("1","添加组件成功"));
        }else{
            return Result.ok(ReturnMessage.message("0","添加组件失败"));
        }
    }


    //自动保存修改的组件
    @ApiOperation("自动保存修改的组件")
    @PutMapping("autoSaveComposition")
    public Result autoSaveComposition(@RequestBody ProductComposition composition){
        boolean b = productCompositionService.updateById(composition);
        if(b){
            return Result.ok(ReturnMessage.message("1","添加组件成功"));
        }else{
            return Result.ok(ReturnMessage.message("0","修改组件失败"));
        }
    }


    //根据id逻辑删除产品组件
    @ApiOperation("根据id逻辑删除产品组件")
    @DeleteMapping("removeCompositionById/{id}")
    public Result removeCompositionById(@PathVariable Integer id){
        boolean b = productCompositionService.removeById(id);
        if(b){
            return Result.ok(ReturnMessage.message("1","删除组件成功"));
        }else{
            return Result.ok(ReturnMessage.message("0","删除组件失败"));
        }
    }





    //产品标准管理相关的接口

    //获取所有的标准类型用于前端搜索的下拉列表
    @ApiOperation("获取所有标准类型")
    @GetMapping("getOptionsForStandardClass")
    public Result getOptionsForStandardClass(){
        List<String> list = standardListService.getClassList();
        return Result.ok(list);
    }

    //获取所有产品标准列表
    @ApiOperation("获取所有产品的标准的列表")
    @GetMapping("getAllStandard")
    public Result getAllStandard(ProductVo vo){
        if("所有".equals(vo.getStandardClass())){
            vo.setStandardClass("");
        }
        List<StandardList> list = standardListService.getList(vo);
        return Result.ok(list);
    }

    //根据产品标准的id获取产品标准的详情列表
    @ApiOperation("根据id获取产品标准")
    @GetMapping("getStandardsById/{id}")
    public Result getStandardsById(@PathVariable Integer id){
        LambdaQueryWrapper<ProductStandardList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductStandardList::getStandardId,id);
        List<ProductStandardList> list = productStandardListService.list(wrapper);
        return Result.ok(list);
    }


    //获取使用所选标准的产品的列表
    //id是所选标准的id
    @ApiOperation("根据标准id获取标准的产品的列表")
    @GetMapping("getProductForStandardById/{id}")
    public Result getProductForStandardById(@PathVariable Integer id){
        StandardList byId = standardListService.getById(id);
        String standard = "（" + byId.getStandardClass() + "）" + byId.getStandardName() + "：" + byId.getStandardNumber();
        LambdaQueryWrapper<ProductData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductData::getExecutiveStandard,standard);
        List<ProductData> list = productDataService.list(wrapper);
        return Result.ok(list);
    }

    //根据id获取某个标准某个项目的详细信息
    //这里前端传回的id就是标准项目的具体的记录的id
    @ApiOperation("根据id获取某个标准某个项目的详细信息")
    @GetMapping("getStandardDetailById/{id}")
    public Result getStandardDetailById(@PathVariable Integer id){
        ProductStandardList byId = productStandardListService.getById(id);
        return Result.ok(byId);
    }








}
