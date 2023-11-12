package com.xcc.system.controller;


import com.alibaba.excel.util.IoUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.common.utils.FileUtil;
import com.xcc.common.utils.GenerateCreateTime;
import com.xcc.common.utils.JwtHelper;
import com.xcc.model.base.ConstantsValue;
import com.xcc.model.technology.CategoryTableRelation;
import com.xcc.model.technology.FileCategory;
import com.xcc.model.technology.FileData;
import com.xcc.model.technology.TableList;
import com.xcc.model.vo.FileVo;
import com.xcc.system.service.CategoryTableRelationService;
import com.xcc.system.service.FileCategoryService;
import com.xcc.system.service.FileDataService;
import com.xcc.system.service.TableListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javafx.scene.control.Tab;
import org.apache.ibatis.executor.ResultExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文件列表 前端控制器
 * </p>
 *
 * @author xcc
 * @since 2023-09-04
 */
@Api(tags = "文件管理接口")
@RestController
@RequestMapping("/admin/technology/fileData")
public class FileDataController {
    @Autowired
    FileDataService fileDataService;

    @Autowired
    FileCategoryService fileCategoryService;

    @Autowired
    TableListService tableListService;

    @Autowired
    CategoryTableRelationService categoryTableRelationService;

    @ApiOperation("获取数据库中所有的表")
    @GetMapping("getAllTable")
    public Result getAllTable(){
        QueryWrapper<TableList> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("table_name");
        List<TableList> tableLists = tableListService.list(wrapper);
        return Result.ok(tableLists);
    }

    @ApiOperation("获取所有文件类别")
    @GetMapping("getAllFileCategory")
    public Result getAllFileCategory(){
        QueryWrapper<FileCategory> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("category_name");
        List<FileCategory> fileCategoryList = fileCategoryService.list(wrapper);
        return Result.ok(fileCategoryList);
    }


    @ApiOperation("保存或更新文件类别信息")
    @PostMapping("saveOrUpdateCategory")
    public Result saveOrUpdateCategory(@RequestBody FileCategory fileCategory){
        //String categoryPath = "D:\\data\\file";
        String categoryPath = ConstantsValue.path;
        Map<String,String> message = new HashMap<>();
        if(fileCategory.getCategoryName().trim().equals("")){
            message.put("msg", "文件类型名不能为空（也不能仅仅是空格）");
            message.put("code", "-1");
            return Result.ok(message);
        }

        if(fileCategory.getId() == null || fileCategory.getId() == 0){
            String categoryName = fileCategory.getCategoryName();
            QueryWrapper<FileCategory> wrapper = new QueryWrapper<>();
            wrapper.eq("category_name",categoryName);
            List<FileCategory> list = fileCategoryService.list(wrapper);
            //判定前端传回来的新建的文件类型是否已经存在
            if(list.size() == 0) {
                FileCategory category = new FileCategory();
                category.setCategoryName(categoryName);
                category.setCreateTime(GenerateCreateTime.generate());
                category.setCategoryPath(categoryPath + "\\" + fileCategory.getCategoryName());
                boolean save = fileCategoryService.save(category);
                if (save) {
                    File file = new File(category.getCategoryPath());
                    if(file.mkdir()){
                        message.put("msg", "文件类型数据保存成功，且自动创建路径为 " + category.getCategoryPath() + " 的文件夹");
                        message.put("code", "1");
                        return Result.ok(message);
                    }else{
                        message.put("msg", "文件类型数据保存成功，但未能创建文件夹，需要手动创建");
                        message.put("code", "1");
                        return Result.ok(message);
                    }
                } else {
                    message.put("msg", "文件类型数据保存失败，请联系管理员");
                    message.put("code", "0");
                    return Result.ok(message);
                }
            }else{
                message.put("msg", "文件类型数据保存失败（文件类型名重复)");
                message.put("code", "-1");
                return Result.ok(message);
            }
        }else{
            Integer id = fileCategory.getId();
            //根据前端选择修改的文件类型的id获取其原始的数据
            FileCategory originalFileCategoryInfo = fileCategoryService.getById(id);
            //根据文件类型的原始数据中的文件类型名是否与前端传回来的修改后的文件类型名相同进行分别处理
            //如果相同则说明文件类型名没有修改，直接用前端传回的数据进行update即可
            //如果没有对数据表中的名字和修改后的名字进行核对后再查重，则会导致没有修改名字的情况下也会报重
            if(originalFileCategoryInfo.getCategoryName().equals(fileCategory.getCategoryName())) {
                boolean b = fileCategoryService.updateById(fileCategory);
                if (b) {
                    message.put("msg", "修改文件类型数据修改成功");
                    message.put("code", "1");
                    return Result.ok(message);
                } else {
                    message.put("msg", "文件类型数据修改失败，请联系管理员");
                    message.put("code", "0");
                    return Result.ok(message);
                }
            }else{      //如果不相同，则说明在前端修改了文件类型名，此时需要核对修改后的文件类型名是否已经存在于数据库中，没有则执行update，反之不执行
                QueryWrapper<FileCategory> wrapper2 = new QueryWrapper<>();
                wrapper2.eq("category_name",fileCategory.getCategoryName());
                List<FileCategory> list2 = fileCategoryService.list(wrapper2);
                if(list2.size() == 0){      //list2.size() == 0说明修改后的文件类型名不存在于记录中，可以update，反之不行
                    boolean b = fileCategoryService.updateById(fileCategory);
                    if (b) {
                        message.put("msg", "修改文件类型数据修改成功");
                        message.put("code", "1");
                        return Result.ok(message);
                    } else {
                        message.put("msg", "文件类型数据修改失败，请联系管理员");
                        message.put("code", "0");
                        return Result.ok(message);
                    }
                }else{
                    message.put("msg", "文件类型数据修改失败（文件类型名重复)");
                    message.put("code", "-1");
                    return Result.ok(message);
                }
            }
        }
    }

    @ApiOperation("修改表格信息过程中自动保存")
    @PostMapping("autoSaveTableInfo")
    public Result autoSaveTableInfo(@RequestBody TableList tableInfo){
        Map<String, String> message = new HashMap<>();
        boolean b = tableListService.updateById(tableInfo);

        if (b) {
            message.put("msg", "修改成功");
            message.put("code", "1");
            return Result.ok(message);
        } else {
            message.put("msg", "修改失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }

    }

    @ApiOperation("创建空白的表格信息")
    @GetMapping("addEmptyTableInfo")
    public Result addEmptyTableInfo(TableList vo){
        Map<String,String> message = new HashMap<>();
        TableList tableInfo = new TableList();
        tableInfo.setTableName(vo.getTableName());
        tableInfo.setCreateTime(GenerateCreateTime.generate());
        QueryWrapper<TableList> wrapper = new QueryWrapper<>();
        wrapper.eq("table_name",vo.getTableName());
        List<TableList> list = tableListService.list(wrapper);
        if(list.size() != 0){
            message.put("msg", "此表格信息已存在，不予增加");
            message.put("code", "-1");
            return Result.ok(message);
        }else {
            boolean b = tableListService.save(tableInfo);
            if (b) {
                message.put("msg", "新增空白表格数据成功");
                message.put("code", "1");
                return Result.ok(message);
            } else {
                message.put("msg", "新增空白表格数据失败，请联系管理员");
                message.put("code", "0");
                return Result.ok(message);
            }
        }
    }

    @ApiOperation("根据文件类型id获取与其关联的表的信息")
    @GetMapping("getTableByCategoryId/{id}")
    public Result getTableByCategoryId(@PathVariable Integer id){
        List<TableList> tableList = categoryTableRelationService.getTableLisByCategoryId(id);
        return Result.ok(tableList);
    }

    @ApiOperation("删除表格list数据或关联数据")
    @DeleteMapping("removeTableById/{id}/{categoryId}")
    public Result removeTableById(@PathVariable Integer id,@PathVariable Integer categoryId){
        Map<String,String> message = new HashMap<>();

        if(categoryId == 0) {//删除表的数据
            //1 先删除category_table_relation中此id（即table_id）的所有记录
            QueryWrapper<CategoryTableRelation> wrapper = new QueryWrapper<>();
            wrapper.eq("table_id",id);
            List<CategoryTableRelation> list = categoryTableRelationService.list(wrapper);
            boolean deleteRelation = true;
            for (CategoryTableRelation categoryTableRelation : list) {
                boolean b = categoryTableRelationService.removeById(categoryTableRelation.getId());
                deleteRelation = deleteRelation && b;
            }

            //2 删除table_list表中此id的记录
            boolean b = tableListService.removeById(id);
            b = b && deleteRelation;
            if(b){
                message.put("msg","删除表格数据成功,同时删除了文件类型与表格的关联数据");
                message.put("code","1");
                return Result.ok(message);
            }else {
                message.put("msg", "删除表格数据失败，请联系管理员");
                message.put("code", "0");
                return Result.ok(message);
            }

        } else if (categoryId != 0){//删除文件类型与表的关联数据
            //根据表id以及与之关联的categoryId删除文件类型与表的关联数据
            boolean b = categoryTableRelationService.removeByRelation(id,categoryId);
            if(b){
                message.put("msg","删除表格与文件类型的关联数据成功");
                message.put("code","1");
                return Result.ok(message);
            }else {
                message.put("msg", "删除表格与文件类型的关联数据失败，请联系管理员");
                message.put("code", "0");
                return Result.ok(message);
            }
        }else{
            message.put("msg", "删除模式异常，删除失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }


    //根据所选的categoryId返回供选择的表格列表
    @ApiOperation("根据categoryId获得未分配的表格")
    @GetMapping("getTableForAssign/{categoryId}")
    public Result getTableForAssign(@PathVariable Integer categoryId){
        List<String> tableNameList = tableListService.getUnassignTableName(categoryId);

        return Result.ok(tableNameList);
    }

    //根据所选择的tableName（unique）返回tableMark（中文名）
    @ApiOperation("根据tableName获得tableMark")
    @GetMapping("getTableMarkByTableName")
    public Result getTableMarkByTableName(TableList vo){
        QueryWrapper<TableList> wrapper = new QueryWrapper<>();
        wrapper.eq("table_name",vo.getTableName());
        List<TableList> list = tableListService.list(wrapper);

        Map<String,String> result = new HashMap<>();
        if(list.size() == 1){
            String selectTableMark = "";
            for (TableList tableList : list) {
                selectTableMark = tableList.getTableMark();
            }
            result.put("selectTableMark",selectTableMark);
        }else{
            result.put("selectTableMark","表格信息记录有误（可能存在重复数据或无数据），请联系管理员");
        }
        return Result.ok(result);
    }

    //确认为所选的category分配表格
    @ApiOperation("根据tableName将其分配给某一文件类别")
    @GetMapping("confirmForAssignTable")
    public Result confirmForAssignTable(TableList vo){
        QueryWrapper<TableList> wrapper = new QueryWrapper<>();
        wrapper.eq("table_name",vo.getTableName());
        List<TableList> list = tableListService.list(wrapper);
        Map<String,String> message = new HashMap<>();
        if(list.size() == 1){
            Integer id;
            id = 0;
            for (TableList tableList : list) {
                id= tableList.getId();
            }
            if(id != 0){
                CategoryTableRelation categoryTableRelation = new CategoryTableRelation();
                categoryTableRelation.setCategoryId(vo.getCategoryId());
                categoryTableRelation.setTableId(id);
                categoryTableRelation.setCreateTime(GenerateCreateTime.generate());
                boolean save = categoryTableRelationService.save(categoryTableRelation);
                if(save){
                    message.put("msg", "分配成功");
                    message.put("code", "1");
                    return Result.ok(message);
                }else{
                    message.put("msg", "分配失败，请联系管理员");
                    message.put("code", "0");
                    return Result.ok(message);
                }
            }else{
                message.put("msg", "数据异常（未获得tableName对应的id），请联系管理员");
                message.put("code", "-1");
                return Result.ok(message);
            }
        }else{
            message.put("msg", "数据异常（可能存在重复数据或无数据），请联系管理员");
            message.put("code", "-1");
            return Result.ok(message);
        }
    }

    //根据id删除文件类型
    @ApiOperation("根据id删除文件类型")
    @DeleteMapping("removeCategoryById/{id}")
    public Result removeCategoryById(@PathVariable Integer id){

        Map<String,String> message = new HashMap<>();
        boolean delete = true;
        boolean b = fileCategoryService.removeById(id);
        QueryWrapper<CategoryTableRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id",id);
        List<CategoryTableRelation> list = categoryTableRelationService.list(wrapper);
        for (CategoryTableRelation categoryTableRelation : list) {
            boolean b1 = categoryTableRelationService.removeById(categoryTableRelation.getId());
            delete = delete && b1;
        }

        delete = delete && b;
        if(delete){
            message.put("code","1");
            message.put("msg","删除文件类型成功,同时删除了与之关联的数据");
            return Result.ok(message);
        }else{
            message.put("code","0");
            message.put("msg","删除失败,请联系管理员");
            return Result.ok(message);
        }
    }

















    //分页获取所有的文件列表
    @ApiOperation("获取文件列表（分页）")
    @GetMapping("{page}/{limit}")
    public Result getFileListPage(@PathVariable Integer page,
                                  @PathVariable Integer limit,
                                  FileVo vo){

        Map<String,Object> result = new HashMap<>();
/*        System.out.println("///////////////////////////////////////////");
        System.out.println(vo.getTableName());*/
        //获取所有文件记录的分页数据
        //加入一个返回的数据排列所依据的字段，如果此字段为空，则加入一个默认字段（文件名）
        if(vo.getOrderField() == null || "".equals(vo.getOrderField())){
            vo.setOrderField("file_name");
        }
        Page<FileData> pageParam = new Page<>(page,limit);
        IPage<FileData> pageModel = fileDataService.selectPage(pageParam,vo);
        result.put("fileList",pageModel);
        //获取目前表格所关联的文件类型数据
        List<String> categoryList = fileCategoryService.getListOfCategoryNameByTableName(vo.getTableName());
        result.put("categoryList",categoryList);

        return Result.ok(result);
    }






    //获取上传的文件
    @ApiOperation("获取上传文件")
    @PostMapping("fileUpload")
    public Result fileUpload(@RequestParam(value = "file") MultipartFile[] fileList,
                             @RequestParam(name = "fileName") String[] fileNameList,
                             @RequestParam(name = "fileCategory") String[] fileCategoryList,
                             @RequestParam(name = "token") String token,
                             @RequestParam(name = "correlationId") Integer correlationId
                             ){
        /*System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////");
        System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////");*/
        Map<String,String> message = new HashMap<>();
        List<String> uploadFail = new ArrayList<>();
        boolean save = true;
        if(fileList.length > 0){
            for (int i = 0; i < fileList.length; i++) {
                FileData fileData = new FileData();
                if(!fileList[i].isEmpty()){

                    fileData.setOriginalName(fileList[i].getOriginalFilename());
                    fileData.setFileName(fileNameList[i]);
                    fileData.setFileCategory(fileCategoryList[i]);

                    //根据categoryName获得其id以及其保存的路径
                    QueryWrapper<FileCategory> wrapper = new QueryWrapper<>();
                    wrapper.eq("category_name",fileCategoryList[i]);
                    wrapper.eq("is_deleted",0);
                    List<FileCategory> list = fileCategoryService.list(wrapper);
                    Integer categoryId = 0;
                    String path = "";
                    if(list.size() > 0) {
                        for (FileCategory category : list) {
                            categoryId = category.getId();
                            path = category.getCategoryPath();
                        }
                    }

                    fileData.setCategoryId(categoryId);
                    fileData.setFileType(fileList[i].getContentType());
                    fileData.setCorrelationId(correlationId);               //对于关联数据的id，如果文件没有关联其他的数据，那么前端设置其为0
                    fileData.setUploadUser(JwtHelper.getUsername(token));

                    if(categoryId == 0){
                        path = ConstantsValue.otherPath;
                    }

                    //获取时间戳，作为文件名的开头，时间戳后面还要加上一个特殊字符串用以分隔时间戳与原文件名
                    String timeStamp = Long.toString(System.currentTimeMillis()) + ConstantsValue.intervalString;

                    path = path + "\\" + timeStamp  + fileList[i].getOriginalFilename();

                    try {
                        fileList[i].transferTo( new File(path));
                    } catch (IOException e) {
                        uploadFail.add(fileNameList[i]);
                        throw new RuntimeException(e);
                    }

                    fileData.setFilePath(path);
                    fileData.setCreateTime(GenerateCreateTime.generate());

                    save = fileDataService.save(fileData);
                }

            }
            if(uploadFail.size() == 0 && save){
                message.put("code","1");
                message.put("msg","所有文件上传成功");
                return Result.ok(message);
            } else if (uploadFail.size() > 0 && save){
                String msg = "";
                for (String s : uploadFail) {
                    msg = msg + "，" + s;
                }
                msg = "文件上传异常" + msg + "未能成功上传，请联系管理员";
                message.put("code","0");
                message.put("msg",msg);
                return Result.ok(message);
            } else if (uploadFail.size() == 0 && !save) {
                message.put("code","0");
                message.put("msg","文件保存成功，但文件数据未能存入数据库，请联系管理员");
                return Result.ok(message);
            }else{
                message.put("code","0");
                message.put("msg","文件上传出现异常（问题可能在后端），请联系管理员");
                return Result.ok(message);
            }
        }else{
            message.put("code","0");
            message.put("msg","文件上传异常（可能是前后端通信问题）,请联系管理员");
            return Result.ok(message);
        }

    }

    //根据文件数据的id逻辑删除file_data表中相应的记录
    @ApiOperation("根据id逻辑删除文件数据")
    @DeleteMapping("removeFileById/{id}")
    public Result removeFileById(@PathVariable Integer id){
        boolean b = fileDataService.removeById(id);
        Map<String,String> message = new HashMap<>();
        if(b){
            message.put("code","1");
            message.put("msg","删除文件成功(只是删除了文件的记录，文件本身并未删除)");
            return Result.ok(message);
        }else{
            message.put("code","0");
            message.put("msg","删除失败,请联系管理员");
            return Result.ok(message);
        }
    }


    //根据原料id获取其关联的文件的信息
    @ApiOperation("根据原料id获取其关联的文件的信息")
    @GetMapping("getFileDataByMaterialId/{materialId}")
    public Result getFileDataByMaterialId(@PathVariable Integer materialId){
        QueryWrapper<FileData> wrapper = new QueryWrapper<>();
        wrapper.eq("correlation_id",materialId);
        List<FileData> list = fileDataService.list(wrapper);
        System.out.println("********************");
        System.out.println(materialId);
        for (FileData fileData : list) {
            System.out.println(fileData);
        }
        return Result.ok(list);
    }


    //根据file_data中的id获取file数据
    @ApiOperation("根据文件id获取文件")
    @GetMapping("getFileByFileId/{fileId}")
    public Result getFileByFileId(@PathVariable Integer fileId){

        FileData fileData = fileDataService.getById(fileId);
        Map<String,Object> file = new HashMap<>();
        file.put("fileName",fileData.getFileName() + "." + FileUtil.getFileSuffix(fileData));
        System.out.println(file.get("fileName"));
        return Result.ok(file);
    }


    //根据file_data中的id向前端传送文件流
    @ApiOperation("传送文件")
    @GetMapping("fileDownload")
    public void fileDownload(HttpServletResponse response,HttpServletRequest request){
        String fileId = request.getHeader("fileId");
        System.out.println(fileId);
        FileData fileData = fileDataService.getById(Integer.parseInt(fileId));
        String filePath = fileData.getFilePath();
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", FileUtil.getFileNameCategory(fileData));//文件名
        response.setHeader("Access-Control-Expose-Headers","Content-Disposition");
        File file = new File(filePath);
        if(file.exists() && file.isFile()){
            try {
                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = response.getOutputStream();//获取response的输出流对象
                IoUtils.copy(inputStream,outputStream);
                //刷新输出流
                outputStream.flush();
                //关闭输出流
                outputStream.close();
                inputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    //自动保存对文件信息的修改，因为前端只能修改文件名称和文件类型，所以这里只提取前端传过来的数据中的相应属性来修改
    @ApiOperation("自动更新文件信息的修改")
    @PostMapping("autoSaveEdit")
    public Result autoSaveEdit(@RequestBody FileData fileData) {
        Map<String, Object> result = new HashMap<>();
        Integer id = fileData.getId();

        String categoryName = fileData.getFileCategory();
        QueryWrapper<FileCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("category_name", categoryName);
        wrapper.eq("is_deleted", 0);
        List<FileCategory> list = fileCategoryService.list(wrapper);

        if (list.size() != 1) {
            result.put("code", "0");
            result.put("msg", "修改失败，文件类型出现问题（file_category表中出现多条 " + categoryName + " 的记录）");
            return Result.ok(result);
        } else {
            Integer categoryId = 0;
            for (FileCategory category : list) {
                categoryId = category.getId();
            }

            FileData byId = fileDataService.getById(id);
            byId.setFileName(fileData.getFileName());
            byId.setFileCategory(fileData.getFileCategory());
            byId.setCategoryId(categoryId);

            boolean b = fileDataService.updateById(byId);

            if (b) {
                result.put("code", "1");
                result.put("msg", "修改成功");
            } else {
                result.put("code", "0");
                result.put("msg", "修改失败");
            }
            FileData newFileData = fileDataService.getById(id);
            result.put("fileData", newFileData);
            return Result.ok(result);
        }
    }





}

