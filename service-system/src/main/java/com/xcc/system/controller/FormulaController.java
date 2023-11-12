package com.xcc.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.common.utils.GenerateCreateTime;
import com.xcc.common.utils.JwtHelper;
import com.xcc.common.utils.ReturnMessage;
import com.xcc.model.base.FormulaModelEnum;
import com.xcc.model.base.IsConfirmEnum;
import com.xcc.model.entity.MaterialIngredient;
import com.xcc.model.history.FormulaComponentHistory;
import com.xcc.model.history.FormulaDataHistory;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.system.SysRole;
import com.xcc.model.technology.FormulaComponent;
import com.xcc.model.technology.FormulaCraft;
import com.xcc.model.technology.FormulaData;
import com.xcc.model.technology.FormulaDataVo;
import com.xcc.model.temp.FormulaComponentTemp;
import com.xcc.model.temp.FormulaDataTemp;
import com.xcc.model.vo.ModifyDataVo;
import com.xcc.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "配方管理接口")
@RestController
@RequestMapping("/admin/technology/Formula")
public class FormulaController {

    @Autowired
    FormulaDataService formulaDataService;

    @Autowired
    FormulaComponentService formulaComponentService;

    @Autowired
    MaterialBasedataService materialBasedataService;

    @Autowired
    MaterialIngredientService materialIngredientService;

    @Autowired
    FormulaDataTempService formulaDataTempService;

    @Autowired
    FormulaComponentTempService formulaComponentTempService;

    @Autowired
    FormulaDataHistoryService formulaDataHistoryService;

    @Autowired
    FormulaComponentHistoryService formulaComponentHistoryService;

    @Autowired
    FormulaCraftService formulaCraftService;

    @Autowired
    SysUserService sysUserService;

    //分页获取所有的配方基本数据
    @ApiOperation("分页获取所有的配方基本数据")
    @GetMapping("{page}/{limit}")
    public Result getAllFormulaDataForPage(@PathVariable Long page,
                                           @PathVariable Long limit,
                                           FormulaDataVo vo){
        //前端加入了根据原料编码进行查询的功能，由于配方中有的是中间料，而中间料又含有原料编码，因此根据原料编码进行查询要处理一下
        //处理在service中进行

        Page<FormulaData> pageParam = new Page<>(page,limit);

        IPage<FormulaData> pageModel = formulaDataService.selectPage(pageParam,vo);

        List<FormulaData> records = pageModel.getRecords();

        for (FormulaData record : records) {
            BigDecimal sumOfContent = new BigDecimal(0);
            List<FormulaComponent> formulaComponentList = formulaComponentService.getListByCode(record.getFormulaCode());
            for (FormulaComponent component : formulaComponentList) {
                sumOfContent = sumOfContent.add(component.getMaterialContent());
            }
            record.setSumOfContent(sumOfContent);
            record.setClassModel(FormulaModelEnum.getClassModel(record.getClassMark()));
        }

        return Result.ok(pageModel);
    }

    //根据配方编码获取配方原料数据列表
    @ApiOperation("根据配方编码获取配方原料数据列表")
    @GetMapping("getFormulaByCode")
    public Result getFormulaByCode(FormulaDataVo vo){
        BigDecimal sumOfContent = new BigDecimal(0);
        List<FormulaComponent> formulaComponentList = formulaComponentService.getListByCode(vo.getFormulaCode());
        for (FormulaComponent component : formulaComponentList) {
            //判断component的状态，存在于原料表中设置其mark为2，存在于配方表中设置其mark为1，都没有则设置其mark为0
            MaterialBasedata byCode = materialBasedataService.getByCode(component.getMaterialCode());
            if(byCode == null){
                FormulaData byCode1 = formulaDataService.getByCode(component.getMaterialCode());
                if(byCode1 == null){
                    component.setMark(0);
                }else{
                    component.setMark(1);
                }
            }else{
                component.setMark(2);
            }
            sumOfContent = sumOfContent.add(component.getMaterialContent());
        }
        Map<String,Object> result = new HashMap<>();
        result.put("formulaComponentList",formulaComponentList);
        result.put("sumOfContent",sumOfContent);
        return Result.ok(result);
    }

    //根据原料编码获取其在material_basedata表中的数据
    @ApiOperation("根据原料编码获取其在material_basedata表中的数据")
    @GetMapping("getInfoByMaterialCode")
    public Result getInfoByMaterialCode(FormulaDataVo vo){
        Map<String,Object> result = new HashMap<>();
        Integer mark = vo.getClassMark();           //如果无此原料为0，中间料为1，原料为2

        if (mark == 1) {
            FormulaData formulaData = formulaDataService.getByCode(vo.getMaterialCode());
            List<FormulaComponent> componentList = formulaComponentService.getListByCode(vo.getMaterialCode());




            for (FormulaComponent component : componentList) {
                //判断component的状态，存在于原料表中设置其mark为2，存在于配方表中设置其mark为1，都没有则设置其mark为0
                MaterialBasedata byCode = materialBasedataService.getByCode(component.getMaterialCode());
                if(byCode == null){
                    FormulaData byCode1 = formulaDataService.getByCode(component.getMaterialCode());
                    if(byCode1 == null){
                        component.setMark(0);
                    }else{
                        component.setMark(1);
                    }
                }else{
                    component.setMark(2);
                }
            }




            BigDecimal sum = new BigDecimal(0);
            for (FormulaComponent formulaComponent : componentList) {
                sum = sum.add(formulaComponent.getMaterialContent());
            }
            result.put("code","1");
            result.put("formulaData",formulaData);
            result.put("componentList",componentList);
            result.put("sumOfSemiContent",sum);
        } else if (mark == 2) {
            MaterialBasedata materialData = materialBasedataService.getByCode(vo.getMaterialCode());
            List<MaterialIngredient> ingredientList = materialIngredientService.getByCode(vo.getMaterialCode());
            result.put("code", "2");
            result.put("materialData", materialData);
            result.put("ingredientList", ingredientList);
        } else if (mark == 0){
            result.put("code","0");
            result.put("msg","未查到此原料信息");
            result.put("materialData",null);
            result.put("ingredientList",null);
            result.put("formulaData",null);
            result.put("componentList",null);
        } else {
            result.put("code","0");
            result.put("msg","查询异常");
            result.put("materialData",null);
            result.put("ingredientList",null);
            result.put("formulaData",null);
            result.put("componentList",null);
        }

        return Result.ok(result);
    }

    //申请修改配方数据，修改data中的is_confirm为-2，并复制相关数据至temp表中
    @ApiOperation("申请修改配方数据")
    @PostMapping("applyModify")
    public Result applyModify(@RequestBody ModifyDataVo vo){
        Map<String,String> message = new HashMap<>();
        boolean b,b1,b2,b3,b4;
        b = true;
        b1 = true;
        b2 = true;
        b3 = true;
        b4 = true;

        //获得vo传回来的数据
        Integer formulaId = vo.getId();
        String reason = vo.getReason();
        //获得申请修改的原始数据
        FormulaData formulaData = formulaDataService.getById(formulaId);
        String formulaCode = formulaData.getFormulaCode();
        List<FormulaComponent> formulaComponentList = formulaComponentService.getListByCode(formulaCode);
        //构造要放入dataTemp表中的数据
        FormulaDataTemp formulaDataTemp = new FormulaDataTemp();
        formulaDataTemp.setFormulaCode(formulaCode);
        formulaDataTemp.setProofCode(formulaData.getProofCode());
        formulaDataTemp.setFormulaName(formulaData.getFormulaName());
        formulaDataTemp.setColorNumber(formulaData.getColorNumber());
        formulaDataTemp.setMarkOne(formulaDataTemp.getMarkOne());
        formulaDataTemp.setMarkTwo(formulaData.getMarkTwo());
        formulaDataTemp.setMarkThree(formulaData.getMarkThree());
        formulaDataTemp.setFormulaDev(formulaData.getFormulaDev());
        formulaDataTemp.setFormulaDate(formulaData.getFormulaDate());
        formulaDataTemp.setClassMark(formulaData.getClassMark());
        formulaDataTemp.setIsConfirm(-2);
        formulaDataTemp.setUpdateReason(reason);
        formulaDataTemp.setVersionNo(formulaData.getVersionNo());
        formulaDataTemp.setRemark(formulaData.getRemark());
        formulaDataTemp.setCreateTime(GenerateCreateTime.generate());
        //保存构造的dataTemp数据
        b2 = formulaDataTempService.save(formulaDataTemp);
        //如果保存正常，则修改data表中的数据，改其is_confirm为0
        //要注意，和原料一样，data表中的is_confirm只有0,1两种状态，temp表中则有多种状态
        //这样的话data表中的is_confirm只有在申请修改和修改后审核通过时才进行状态变换，在批准修改和修改后保存时则无需进行状态变换
        formulaData.setIsConfirm(0);
        if(b2){
            b3 = formulaDataService.updateById(formulaData);
            //如果修改正常,则将formulaComponentList中的数据逐条保存至temp中
            if(b3){
                for (FormulaComponent formulaComponent : formulaComponentList) {
                   FormulaComponentTemp formulaComponentTemp = new FormulaComponentTemp();
                   formulaComponentTemp.setFormulaCode(formulaCode);
                   formulaComponentTemp.setRankNumber(formulaComponent.getRankNumber());
                   formulaComponentTemp.setMaterialCode(formulaComponent.getMaterialCode());
                   formulaComponentTemp.setMaterialContent(formulaComponent.getMaterialContent());
                   formulaComponentTemp.setMaterialPurpose(formulaComponent.getMaterialPurpose());
                   formulaComponentTemp.setRemark(formulaComponent.getRemark());
                   formulaComponentTemp.setCreateTime(GenerateCreateTime.generate());
                   boolean save = formulaComponentTempService.save(formulaComponentTemp);
                   b4 = b4 && save;
                }
            }
        }

        b = b1 && b2 && b3 && b4;

        if(b){
            message.put("code","1");
            message.put("msg","申请修改成功");
            return Result.ok(message);
        }else{
            message.put("code","0");
            message.put("msg","申请修改失败，请联系管理员");
            return Result.ok(message);
        }
    }


    //获取formula_data_temp中的数据（前100条）
    @ApiOperation("获取formula_data_temp中的数据")
    @GetMapping("getListOfModify")
    public Result getListOfModify(){
        DateFormat af = new SimpleDateFormat("yyyy-MM-dd");
        Integer limit;
        limit = 100;
        List<FormulaDataTemp> formulaDataTempList = formulaDataTempService.getListOfModify(limit);
        for (FormulaDataTemp formulaDataTemp : formulaDataTempList) {
            formulaDataTemp.setApplyDate(af.format(formulaDataTemp.getCreateTime()));
            formulaDataTemp.setApplyStatus(IsConfirmEnum.getApplyStatus(formulaDataTemp.getIsConfirm()));
        }
        return Result.ok(formulaDataTempList);
    }


    //根据在formula_data_temp表中的id获取配方信息和配方原料表
    @ApiOperation("根据在formula_data_temp表中的id获取配方信息和配方原料表")
    @GetMapping("getDetailById/{id}")
    public Result getDetailById(@PathVariable Integer id,
                                String token){
        FormulaDataTemp formulaDataTemp = formulaDataTempService.getById(id);
        String formulaCode = formulaDataTemp.getFormulaCode();
        List<FormulaComponentTemp> formulaComponentTempList = formulaComponentTempService.getByCode(formulaCode);

        BigDecimal sum = new BigDecimal(0);
        for (FormulaComponentTemp formulaComponentTemp : formulaComponentTempList) {

            String materialCode = formulaComponentTemp.getMaterialCode();
            MaterialBasedata byCode = materialBasedataService.getByCode(materialCode);
            if(byCode != null) {
                formulaComponentTemp.setMaterialName(byCode.getMaterialName());
            }
            sum = sum.add(formulaComponentTemp.getMaterialContent());
        }
        Map<String,Object> result = new HashMap<>();
        result.put("formulaData",formulaDataTemp);
        result.put("materialList",formulaComponentTempList);
        result.put("sum",sum);

/*
        String username = JwtHelper.getUsername(token);
        //根据用户名获取其角色信息（用户名为unique）
        //一个用户可能有多个角色
        List<SysRole> roleList = sysUserService.getUserRoleByUserName(username);
        //authorityList用于保存用户在此表中所具有的权限
        List<Integer> authorityList = new ArrayList<>();
        //roleForFileManager具有文件管理权限的角色列表
        //如果还有其他权限指定角色列表，则要重新设置一个新的具有某种权限的角色列表
        List<String> pasteRole = new ArrayList<>();
        pasteRole.add("备案");
        pasteRole.add("SYSTEM");
        //对用户的每个角色进行遍历，每个角色可能有不同的权限
        for (SysRole sysRole : roleList) {
            String roleCode = sysRole.getRoleCode();
            if(pasteRole.contains(roleCode)){
                //如果用户的角色code在roleForFileManager列表中，则往其authorityList中加入1
                //若是此角色还需赋予其他权限，则需要在另外一个判断语句中往authorityList中加入其他标记数字
                authorityList.add(1);
            }
        }
        int authority = 0;
        for (Integer integer : authorityList) {
            if (integer.equals(1)){
                authority = 1;
            }
        }
        result.put("authority ",authority);
        System.out.println("***********************************");
        System.out.println(authority);*/
        return Result.ok(result);
    }

    //同意原料信息修改（主管权限里面）
    @ApiOperation("根据baseTemp中的id，修改baseTemp中的is_confirm为-1")
    @GetMapping("agreeForEdit/{id}")
    public Result agreeForEdit(@PathVariable Integer id){
        FormulaDataTemp formulaDataTemp = formulaDataTempService.getById(id);

        formulaDataTemp.setIsConfirm(-1);
        boolean b = formulaDataTempService.updateById(formulaDataTemp);
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
    @ApiOperation("配方驳回")
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
        //1 根据id获取dataTemp数据以及要处理的配方编码
        FormulaDataTemp dataTemp = formulaDataTempService.getById(id);
        String formulaCode = dataTemp.getFormulaCode();
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
                FormulaData data = formulaDataService.getByCode(formulaCode);
                //4.2 修改data中的is_confirm为1
                data.setIsConfirm(1);
                //4.3 修改data数据
                boolean updateData = formulaDataService.updateById(data);
                //5 处理dataTemp数据
                //5.1 创建驳回标记
                DateFormat af = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String rejectMark = af.format(new Date()) + "被驳回修改申请";
                //5.2 修改dataTemp中的is_confirm为-100
                dataTemp.setIsConfirm(-100);
                //5.3 在dataTemp中的remark前面加上驳回标记
                dataTemp.setRemark(rejectMark + dataTemp.getRemark());
                //5.4 逻辑删除dataTemp
                boolean deleteDataTemp = formulaDataTempService.removeById(dataTemp.getId());
                //5.5 在componentTemp的remark前面加上驳回标记，然后逻辑删除componentTemp
                boolean deleteComponentTempList = true;
                List<FormulaComponentTemp> componentTempList = formulaComponentTempService.getByCode(formulaCode);
                for (FormulaComponentTemp formulaComponentTemp : componentTempList) {
                    formulaComponentTemp.setRemark(rejectMark + formulaComponentTemp.getRemark());
                    boolean b = formulaComponentTempService.removeById(formulaComponentTemp.getId());
                    deleteComponentTempList = deleteComponentTempList && b;
                }

                if(updateData && deleteDataTemp && deleteComponentTempList){
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
                boolean b = formulaDataTempService.updateById(dataTemp);
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


    //保存修改的数据至temp表中，但未提交审核，不变动is_confirm的值
    //通过实时auto保存来进行


    //修改完成，提交修改的数据待审核，修改temp表中is_confirm为0，保存修改的数据至temp表中
    @ApiOperation("提交配方数据进入审核状态")
    @GetMapping("submitForReview/{id}")
    public Result submitForReview(@PathVariable Integer id){

        Map<String, String> message = new HashMap<>();
        List<String> invalidCodeList = new ArrayList<>();
        FormulaDataTemp tempForConfirm = formulaDataTempService.getById(id);

        //要对所提交审核的配方的所有原料进行核查，确认所有原料/中间料配方均在原料库或配方库中方可放行进入审核，即将is_confirm改为0
        //1 获取配方的配方编码
        String formulaCode = tempForConfirm.getFormulaCode();
        //2 根据配方编码，获取其保存在componentTemp表中的原料列表
        List<FormulaComponentTemp> componentTempList = formulaComponentTempService.getByCode(formulaCode);
        //2.1 遍历获取的原料列表，进一步核实配比合计是不是100%
        //由于前端为了照顾序号调整的方便，所以前端在修改单行数据后并不对配比合计值进行计算，这会导致配比修改后仍然可以提交的情况
        BigDecimal sum = new BigDecimal(0);
        for (FormulaComponentTemp formulaComponentTemp : componentTempList) {
            sum = sum.add(formulaComponentTemp.getMaterialContent());
        }



        if(sum.compareTo(new BigDecimal(100)) != 0){
            message.put("msg", "提交审核失败（原料配比合计不是100），请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }




        //3 遍历获得的原料列表，看每一个原料是否在原料库或配方库中
        for (FormulaComponentTemp formulaComponentTemp : componentTempList) {
            //4 获取每一个原料记录的原料编码
            String materialCode = formulaComponentTemp.getMaterialCode();
            //5 先核查原料编码是否在原料库中(因为原料占大多数，所以先核查原料，再核查配方）
            MaterialBasedata material = materialBasedataService.getByCode(materialCode);
            if(material == null){
                //6 如果原料库中查到的数据为空，则查找配方库
                FormulaData formula = formulaDataService.getByCode(materialCode);
                //7 如果配方库中查到的数据也为空，则往invalidCodeList添加此编码
                if(formula == null){
                    invalidCodeList.add(materialCode);
                }
            }
        }

        //8 判断invalidCodeList的长度
        if(invalidCodeList.size() == 0) {
            //9 如果长度为0，说明所有的原料/中间料配方均可找到，那么可以放行进入审核
            tempForConfirm.setIsConfirm(0);
            boolean b = formulaDataTempService.updateById(tempForConfirm);
            if (b) {
                message.put("msg", "提交审核成功");
                message.put("code", "1");
                return Result.ok(message);
            } else {
                message.put("msg", "提交审核失败，请联系管理员");
                message.put("code", "0");
                return Result.ok(message);
            }
        }else{
            //10 如果invalidCodeList的长度大于0，说明存在原料/中间料配方不可找到，禁止放行进入审核，并返回找不到的数据所组成的字符串给前端
            String msg = "原料： ";
            for (String s : invalidCodeList) {
                msg = msg + s + "，";
            }
            msg = msg.substring(0,msg.length()-1);
            msg = msg + " 不存在于数据库中，不予提交审核";
            message.put("msg", msg);
            message.put("code", "2");
            return Result.ok(message);
        }
    }


    //审核通过修改之后的配方数据（主管管理权限里面的）
    //修改temp表中的is_confirm为1，并将temp表中的相关数据复制至data表中
    //构建历史数据
    @ApiOperation("根据Temp中的id，修改baseTemp中的is_confirm为1")
    @GetMapping("confirmForEdit/{id}")                      //注意：这里的id是Temp表中的id
    public Result confirmForEdit(@PathVariable Integer id){

        Map<String,String> message = new HashMap<>();

        boolean b = true;

        boolean listIsNotEmpty = true;          //用于标记component表是否为空（新配方时component表为空），如果为否（即true），则能够往componentHistory表中拷贝数据

        //1 拿到dataTemp表中的数据
        FormulaDataTemp dataTemp = formulaDataTempService.getById(id);
        if(dataTemp == null){
            message.put("msg", "没有temp数据，审核失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
        //2 从dataTemp中拿到formulaCode，用于从componentTemp表中获取数据
        String formulaCode = dataTemp.getFormulaCode();
        List<FormulaComponentTemp> tempList = formulaComponentTempService.getByCode(formulaCode);
        if(tempList.size() == 0){
            message.put("msg", "没有tempList数据，审核失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
        //3 根据formulaCode获取data表中的数据和component表中的数据
        FormulaData data = formulaDataService.getByCode(formulaCode);
        if(data == null){
            data.setFormulaCode(formulaCode);
        }
        List<FormulaComponent> list = formulaComponentService.getListByCode(formulaCode);
        if(list.size() == 0){
            list.add(new FormulaComponent());
            listIsNotEmpty = false;
        }
        //4 将data和list拷贝到history表中
        FormulaDataHistory formulaDataHistory = new FormulaDataHistory();
        formulaDataHistory.setProofCode(data.getProofCode());
        formulaDataHistory.setFormulaCode(data.getFormulaCode());
        formulaDataHistory.setFormulaName(data.getFormulaName());
        formulaDataHistory.setColorNumber(data.getColorNumber());
        formulaDataHistory.setMarkOne(data.getMarkOne());
        formulaDataHistory.setMarkTwo(data.getMarkTwo());
        formulaDataHistory.setMarkThree(data.getMarkThree());
        formulaDataHistory.setFormulaDev(data.getFormulaDev());
        formulaDataHistory.setFormulaDate(data.getFormulaDate());
        formulaDataHistory.setClassMark(data.getClassMark());
        formulaDataHistory.setVersionNo(data.getVersionNo());
        formulaDataHistory.setRemark(data.getRemark());
        formulaDataHistory.setCreateTime(data.getCreateTime());             //这个保存的是历史版本数据的创建时间，不是表数据的创建时间

        boolean saveHistoryData = formulaDataHistoryService.save(formulaDataHistory);
        boolean saveHistoryList = true;
        if(listIsNotEmpty) {
            for (FormulaComponent component : list) {
                FormulaComponentHistory formulaComponentHistory = new FormulaComponentHistory();
                formulaComponentHistory.setFormulaCode(component.getFormulaCode());
                formulaComponentHistory.setRankNumber(component.getRankNumber());
                formulaComponentHistory.setMaterialCode(component.getMaterialCode());
                formulaComponentHistory.setMaterialContent(component.getMaterialContent());
                formulaComponentHistory.setMaterialPurpose(component.getMaterialPurpose());
                formulaComponentHistory.setVersionNo(data.getVersionNo());              //component表中不保存version_no
                formulaComponentHistory.setRemark(component.getRemark());
                formulaComponentHistory.setCreateTime(component.getCreateTime());
                boolean save = formulaComponentHistoryService.save(formulaComponentHistory);
                saveHistoryList = saveHistoryList && save;
            }
        }

        //5 将Temp表中的数据保存到data和component表中
        //5.1 data表中采取更新的方法
        data.setProofCode(dataTemp.getProofCode());
        data.setFormulaCode(dataTemp.getFormulaCode());         //在前端，formulaCode应该是不能更改的
        data.setFormulaName(dataTemp.getFormulaName());
        data.setColorNumber(dataTemp.getColorNumber());
        data.setMarkOne(dataTemp.getMarkOne());
        data.setMarkTwo(dataTemp.getMarkTwo());
        data.setMarkThree(dataTemp.getMarkThree());
        data.setFormulaDev(dataTemp.getFormulaDev());
        data.setFormulaDate(dataTemp.getFormulaDate());
        data.setClassMark(dataTemp.getClassMark());
        data.setRemark(dataTemp.getRemark());
        data.setVersionNo(data.getVersionNo() + 1);             //version_no直接从data表中拿
        data.setIsConfirm(1);                                   //审核成功，将is_confirm改为1，同时也将temp中的改为1
        dataTemp.setIsConfirm(1);

        boolean updateData = formulaDataService.updateById(data);
        formulaDataTempService.updateById(dataTemp);
        //为了避免往期完成修改的数据回显时导致原料数据的大量重复，将往期修改完成的原料Temp数据和基本Temp数据均进行逻辑删除
        boolean deleteTempData = formulaDataTempService.removeById(dataTemp.getId());
        boolean deleteList = true;
        //5.2 component则是先逻辑删除原有数据，然后再保存新的数据
        //5.2.1 先删除原有数据，这里要加入listIsNotEmpty的判断
        if(listIsNotEmpty) {
            for (FormulaComponent component : list) {
                boolean b1 = formulaComponentService.removeById(component.getId());
                deleteList = deleteList && b1;
            }
        }
        //5.2.2 将componentTemp中的新数据拷贝到component中
        boolean saveList = true;
        for (FormulaComponentTemp formulaComponentTemp : tempList) {
            FormulaComponent formulaComponent = new FormulaComponent();
            formulaComponent.setFormulaCode(formulaComponentTemp.getFormulaCode());
            formulaComponent.setRankNumber(formulaComponentTemp.getRankNumber());
            formulaComponent.setMaterialCode(formulaComponentTemp.getMaterialCode());
            formulaComponent.setMaterialContent(formulaComponentTemp.getMaterialContent());
            formulaComponent.setMaterialPurpose(formulaComponentTemp.getMaterialPurpose());
            formulaComponent.setRemark(formulaComponentTemp.getRemark());
            formulaComponent.setCreateTime(GenerateCreateTime.generate());
            boolean save = formulaComponentService.save(formulaComponent);
            saveList = saveList && save;
        }


        //5.2.3 逻辑删除componentTemp中的数据
        //由于在前端回显往期修改数据时，往期修改的数据的基本数据虽然不重复，但是原料数据却会出现大量的重复（基本数据是一条一条的，原料数据确实所有未删除的数据组成的列表）
        //所以为了避免这个问题，直接在前端不回显往期已经完成修改的数据，即将dataTemp和ingredientTemp中的记录均进行逻辑删除
        boolean deleteTempList = true;

        for (FormulaComponentTemp formulaComponentTemp : tempList) {
            boolean b1 = formulaComponentTempService.removeById(formulaComponentTemp.getId());
            deleteTempList = deleteTempList && b1;
        }


        b = saveHistoryData && saveHistoryList && updateData && deleteTempData && deleteList && saveList;

        if(b){
            message.put("msg","审核成功，已修改/新增配方");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("msg", "审核失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
        /*
        boolean b,b1,b2,b3,b4;
        b = true;
        b1 = true;
        b2 = true;
        b3 = true;
        b4 = true;
        //首先获取dataTemp表中id对应的数据
        FormulaDataTemp formulaDataTemp = formulaDataTempService.getById(id);

        //获得temp表中对应的formulaCode
        String formulaCode = formulaDataTemp.getFormulaCode();
        //获得配方的原料组成数据（Temp中修改后的）
        List<FormulaComponentTemp> formulaComponentTempList = formulaComponentTempService.getByCode(formulaCode);
        //根据code修改data中的对应数据，由于code具有唯一性，所以可以使用code来找到对应的记录并修改
        //首先要根据code获得data表中的数据
        FormulaData formulaData = formulaDataService.getByCode(formulaCode);
        //然后用修改后的temp表中的数据修改data表中的数据的每一个对应属性
        //注意：createTime不是在这里生成，而是在新增配方的接口中生成
        formulaData.setProofCode(formulaDataTemp.getProofCode());
        formulaData.setFormulaCode(formulaDataTemp.getFormulaCode());
        formulaData.setFormulaName(formulaDataTemp.getFormulaName());
        formulaData.setColorNumber(formulaDataTemp.getColorNumber());
        formulaData.setMarkOne(formulaDataTemp.getMarkOne());
        formulaData.setMarkTwo(formulaDataTemp.getMarkTwo());
        formulaData.setMarkThree(formulaDataTemp.getMarkThree());
        formulaData.setFormulaDev(formulaDataTemp.getFormulaDev());
        formulaData.setFormulaDate(formulaDataTemp.getFormulaDate());
        formulaData.setClassMark(formulaDataTemp.getClassMark());
        formulaData.setIsConfirm(1);
        formulaData.setRemark(formulaDataTemp.getRemark());
        //拷贝完成后，更新data表中的数据
        b1 = formulaDataService.updateById(formulaData);
        //逻辑删除component表中的对应code的数据
        if(b1){
             formulaComponentService.deleteByCode(formulaCode);
        }
        //将componentTemp表中的数据逐个拷贝到一个新的formulaComponent中，并逐个保存至component表中
        for (FormulaComponentTemp formulaComponentTemp : formulaComponentTempList) {
            FormulaComponent component = new FormulaComponent();
            component.setFormulaCode(formulaCode);
            component.setRankNumber(formulaComponentTemp.getRankNumber());
            component.setMaterialCode(formulaComponentTemp.getMaterialCode());
            component.setMaterialContent(formulaComponentTemp.getMaterialContent());
            component.setMaterialPurpose(formulaComponentTemp.getMaterialPurpose());
            component.setRemark(formulaComponentTemp.getRemark());
            component.setCreateTime(GenerateCreateTime.generate());
            boolean save = formulaComponentService.save(component);
            b2 = b2 && save;
        }
        //删除componentTemp表中对应code的数据
        for (FormulaComponentTemp formulaComponentTemp : formulaComponentTempList) {
            boolean removeById = formulaComponentTempService.removeById(formulaComponentTemp.getId());
            b3 = b3 && removeById;
        }
        //前面的动作都完成后，修改temp数据的is_confirm为1
        formulaDataTemp.setIsConfirm(1);
        //保存修改
        b4 = formulaDataTempService.updateById(formulaDataTemp);

        b = b1 && b2 && b3 && b4;

        Map<String,String> message = new HashMap<>();
        if(b){
            message.put("status","审核成功，已修改/新增配方");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "审核失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
        */
    }


    //对确认新增配方的配方编码进行查重
    @ApiOperation("对确认新增配方的配方编码进行查重")
    @GetMapping("duplicateCheckForNewCode/{newCode}")
    public Result duplicateCheckForNewCode(@PathVariable String newCode){
        FormulaData byCode = formulaDataService.getByCode(newCode);
        Map<String,String> message = new HashMap<>();
        if(byCode == null){
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "配方编码： " + newCode + " 已经存在，请重新编制配方编码");
            message.put("code", "0");
            return Result.ok(message);
        }
    }

    //增加新配方
    @ApiOperation("增加新配方")
    @GetMapping("createNewFormula")
    public Result createNewFormula(FormulaDataVo vo){
        String newCode = vo.getFormulaCode();
        Integer classMark = vo.getClassMark();
        Map<String,String> message = new HashMap<>();
        FormulaData formulaData = new FormulaData();
        formulaData.setFormulaCode(newCode);
        formulaData.setCreateTime(GenerateCreateTime.generate());
        if(classMark == -1){
            message.put("status", "前后端沟通类型数据有误，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
        //根据前端传回的数据设置配方类型
        /*Integer classMark;
        if("产品配方".equals(formulaModel)){
            classMark = 0;
        } else if ("中间料配方".equals(formulaModel)){
            classMark = 1;
        }else{
            message.put("status", "前后端沟通类型数据有误，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }*/
        formulaData.setClassMark(classMark);
        //设置新创建的新增配方的is_confirm状态为0，basedata表中的is_confirm只有0和1两种状态
        formulaData.setIsConfirm(0);
        formulaData.setVersionNo(0);
        boolean save = formulaDataService.save(formulaData);

        if(save){
            //新增配方创建成功后，将其数据拷贝一份至temp数据表中，并设置其is_confirm为-10
            FormulaDataTemp formulaDataTemp = new FormulaDataTemp();
            formulaDataTemp.setFormulaCode(newCode);
            formulaDataTemp.setIsConfirm(-10);
            formulaDataTemp.setVersionNo(0);
            formulaDataTemp.setClassMark(classMark);
            formulaDataTemp.setCreateTime(GenerateCreateTime.generate());
            formulaDataTempService.save(formulaDataTemp);
            message.put("status","新增配方 " + newCode + " 创建成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "新增配方：" + newCode + "创建失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }

    }


    //自动保存编辑的配方数据
    @ApiOperation("自动保存编辑的配方数据")
    @PostMapping("autoSaveFormulaData")
    public Result autoSaveFormulaData(@RequestBody FormulaDataTemp formulaDataTemp){

        boolean b = formulaDataTempService.updateById(formulaDataTemp);

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



    //自动保存原料配方数据
    @ApiOperation("")
    @PostMapping("autoSaveMaterialData")
    public Result autoSaveMaterialData(@RequestBody FormulaComponentTemp formulaComponentTemp){

        //这里不能直接保存从前端传来的数据，因为前端传来的数据并没有涵盖表格中的所有字段
        //所以需要通过上面的代码将前端传回来的部分数据拷贝入通过id获取的记录中
        Integer id = formulaComponentTemp.getId();
        FormulaComponentTemp byId = formulaComponentTempService.getById(id);
        byId.setRankNumber(formulaComponentTemp.getRankNumber());
        byId.setMaterialCode(formulaComponentTemp.getMaterialCode());
        byId.setMaterialContent(formulaComponentTemp.getMaterialContent());
        byId.setMaterialPurpose(formulaComponentTemp.getMaterialPurpose());
        byId.setRemark(formulaComponentTemp.getRemark());

        boolean b = formulaComponentTempService.updateById(byId);

        Map<String,String> message = new HashMap<>();

        if(b){
            String materialName = "";
            MaterialBasedata material = materialBasedataService.getByCode(byId.getMaterialCode());
            if(material != null){
                materialName = material.getMaterialName();
            }else{
                FormulaData semi = formulaDataService.getByCode(byId.getMaterialCode());
                if(semi != null){
                    materialName = "中间料";
                }else{
                    materialName = "无此数据";
                }
            }
            message.put("materialName",materialName);
            message.put("status","修改成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("status", "修改失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }


    //根据新增原料弹窗的原料/中间料编码获取相应的数据
    //其中vo中的classMark用来标志类型，1表示中间料，2表示原料
    @ApiOperation("根据原料/中间料编码获取相应数据")
    @GetMapping("getMaterialInfo")
    public Result getMaterialInfo(FormulaDataVo vo){
        Map<String,String> result = new HashMap<>();


        Integer classMark = vo.getClassMark();
        if(classMark == 1){
            FormulaData byCode = formulaDataService.getByCode(vo.getMaterialCode());
            //这里要加上byCode.getIsConfirm() == 1的判断以筛除修改/新增未经过审核数据
            if(byCode != null && byCode.getIsConfirm() == 1) {
                result.put("code", "1");
                result.put("newMaterialName", byCode.getFormulaName());
            }else{
                result.put("code","0");
                result.put("msg","未查到此中间料，核实是否类型选择错误");
            }
        } else if (classMark == 2) {
            MaterialBasedata byCode = materialBasedataService.getByCode(vo.getMaterialCode());
            //这里要加上byCode.getIsConfirm() == 1的判断以筛除修改/新增未经过审核数据
            if(byCode != null && byCode.getIsConfirm() == 1) {
                result.put("code", "1");
                result.put("newMaterialName", byCode.getMaterialName());
                result.put("newMaterialProducterName", byCode.getProducterName());
            }else{
                result.put("code","0");
                result.put("msg","未查到此原料，核实是否类型选择错误");
            }
        }else{
            result.put("code","0");
            result.put("msg","类型错误");
        }
        return Result.ok(result);
    }



    //给配方增加原料
    //参数由后端的FormulaDataVo接收
    @ApiOperation("给配方增加原料")
    @PostMapping("addNewMaterial")
    public Result addNewMaterial(@RequestBody FormulaDataVo vo){

        FormulaComponentTemp formulaComponentTemp = new FormulaComponentTemp();
        formulaComponentTemp.setFormulaCode(vo.getFormulaCode());
        formulaComponentTemp.setRankNumber(vo.getRankNumber());
        formulaComponentTemp.setMaterialCode(vo.getMaterialCode());
        formulaComponentTemp.setMaterialContent(vo.getMaterialContent());
        formulaComponentTemp.setMaterialPurpose(vo.getMaterialPurpose());
        formulaComponentTemp.setRemark(vo.getRemark());
        formulaComponentTemp.setCreateTime(GenerateCreateTime.generate());

        boolean b = formulaComponentTempService.save(formulaComponentTemp);

        Map<String,String> message = new HashMap<>();

        if(b){
            message.put("msg","添加成功");
            message.put("code","1");
            return Result.ok(message);
        }else {
            message.put("msg", "添加失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }


    //给新建的配方增加一个空的原料列表
    @ApiOperation("新增一个空的配方列表")
    @GetMapping("addEmptyList/{id}/{num}")
    public Result addEmptyList(@PathVariable Integer id,@PathVariable Integer num){
        Map<String, String> message = new HashMap<>();
        if(num != 0) {
            String formulaCode = formulaDataTempService.getById(id).getFormulaCode();
            boolean b;
            b = true;
            for (int i = 0; i < num; i++) {
                FormulaComponentTemp formulaComponentTemp = new FormulaComponentTemp();
                formulaComponentTemp.setFormulaCode(formulaCode);
                formulaComponentTemp.setCreateTime(GenerateCreateTime.generate());
                formulaComponentTemp.setRankNumber(i + 1);
                formulaComponentTemp.setMaterialCode("");
                formulaComponentTemp.setMaterialContent(new BigDecimal(0));
                boolean save = formulaComponentTempService.save(formulaComponentTemp);
                b = b && save;
            }
            if (b) {
                message.put("msg", "添加成功");
                message.put("code", "1");
                return Result.ok(message);
            } else {
                message.put("msg", "添加失败，请联系管理员");
                message.put("code", "0");
                return Result.ok(message);
            }
        }else{
            message.put("msg", "原料数量不能为0");
            message.put("code", "0");
            return Result.ok(message);
        }
    }

    //删除配方的原料成分
    @ApiOperation("删除配方的原料成分")
    @DeleteMapping("deleteMaterialFromFormulaById/{id}")
    public Result deleteMaterialFromFormulaById(@PathVariable Integer id){

        boolean b = formulaComponentTempService.removeById(id);

        Map<String, String> message = new HashMap<>();
        if (b) {
            message.put("msg", "删除成功");
            message.put("code", "1");
            return Result.ok(message);
        } else {
            message.put("msg", "删除失败，请联系管理员");
            message.put("code", "0");
            return Result.ok(message);
        }
    }

    @ApiOperation("")
    @GetMapping("getUserName")
    public Result getUserName(HttpServletRequest request){
        String userNameByServlet = JwtHelper.getUserNameByServlet(request);
        Map<String,String> result = new HashMap<>();
        result.put("username",userNameByServlet);
        return Result.ok(result);
    }



    //以下是配方投料工艺相关代码

    //分页获取配方的基础数据
    @ApiOperation("分页获取配方对于工艺的基础数据")
    @GetMapping("getAllFormulaForCraft/{page}/{limit}")
    public Result getAllFormulaForCraft(@PathVariable Integer page,
                                        @PathVariable Integer limit,
                                        FormulaDataVo vo){
        if("仅产品配方".equals(vo.getFormulaType())){
            vo.setClassMark(0);
        } else if ("仅中间料配方".equals(vo.getFormulaType())) {
            vo.setClassMark(1);
        }else{
            vo.setClassMark(-1);
        }

        Page<FormulaData> pageParam = new Page<>(page,limit);

        IPage<FormulaData> pageModel = formulaDataService.selectPageForCraft(pageParam,vo);

        List<FormulaData> records = pageModel.getRecords();

        for (FormulaData record : records) {
            record.setClassModel(FormulaModelEnum.getClassModel(record.getClassMark()));
        }

        return Result.ok(pageModel);
    }

    //根据配方id显示某个工艺的具体工艺数据
    @ApiOperation("根据配方id获取工艺数据")
    @GetMapping("getCraftById/{formulaId}")
    public Result getCraftById(@PathVariable Integer formulaId){
        QueryWrapper<FormulaCraft> wrapper = new QueryWrapper<>();
        wrapper.eq("formula_id",formulaId);
        wrapper.eq("is_deleted",0);
        wrapper.orderByAsc("rank_number");
        List<FormulaCraft> list = formulaCraftService.list(wrapper);
        return Result.ok(list);
    }

    //保存前端传回的配方新工艺步骤数据
    @ApiOperation("保存配方新工艺步骤数据")
    @PostMapping("addNewStageForFormula")
    public Result addNewStageForFormula(@RequestBody FormulaCraft formulaCraft){
        formulaCraft.setCreateTime(GenerateCreateTime.generate());
        if(formulaCraft.getFormulaId() == null || formulaCraft.getFormulaId().equals(0)){
            return Result.ok(ReturnMessage.message("0","添加失败"));
        }
        if(formulaCraft.getUnit().equals("无")){
            formulaCraft.setStageDurationFloor(0L);
            formulaCraft.setStageDurationCeiling(0L);
        }
        boolean save = formulaCraftService.save(formulaCraft);
        if(save){
            return Result.ok(ReturnMessage.message("1","添加成功"));
        }else {
            return Result.ok(ReturnMessage.message("0","添加失败"));
        }
    }


    //自动保存工艺数据
    @ApiOperation("自动保存工艺数据")
    @PutMapping("autoSaveCraft")
    public Result autoSaveCraft(@RequestBody FormulaCraft formulaCraft){
        boolean b = formulaCraftService.updateById(formulaCraft);
        if(b){
            return Result.ok(ReturnMessage.message("1","修改成功"));
        }else {
            return Result.ok(ReturnMessage.message("0","修改失败"));
        }

    }


    //根据id逻辑删除工艺步骤
    @ApiOperation("根据id逻辑删除工艺步骤")
    @DeleteMapping("removeStageById/{id}")
    public Result removeStageById(@PathVariable Integer id){
        boolean b = formulaCraftService.removeById(id);
        if(b){
            return Result.ok(ReturnMessage.message("1","删除成功"));
        }else {
            return Result.ok(ReturnMessage.message("0","删除失败"));
        }
    }

    //处理复制到前端的数据
    @ApiOperation("处理前端传回的粘贴数据")
    @GetMapping("getMaterialNameList")
    public Result getMaterialNameList(List<FormulaComponentTemp> list){
        System.out.println("*********************************");
        for (FormulaComponentTemp formulaComponentTemp : list) {
            System.out.println(formulaComponentTemp);
        }
        //String materialCode = vo;
/*        MaterialBasedata byCode = materialBasedataService.getByCode(materialCode);
        if(byCode != null){
            return Result.ok(byCode.getMaterialName());
        }else{
            FormulaData byCode1 = formulaDataService.getByCode(materialCode);
            if(byCode1 != null){
                if("".equals(byCode1.getFormulaName())){
                    return Result.ok("未命名中间料");
                }else{
                    return Result.ok(byCode1.getFormulaName());
                }
            }
        }*/
        return Result.ok("异常");
    }

    //导入粘贴的配方数据
    @ApiOperation("根据粘贴新增原料")
    @PostMapping("addFormulaDataList")
    public Result addFormulaDataList(@RequestBody List<FormulaComponentTemp> list){
        boolean saveAll = true;
        for (FormulaComponentTemp formulaComponentTemp : list) {
            if(formulaComponentTemp.getMaterialCode().equals("") || formulaComponentTemp.getMaterialCode() == null || formulaComponentTemp.getMaterialContent() == null || formulaComponentTemp.getMaterialContent().equals(BigDecimal.ZERO)){
                return Result.ok(ReturnMessage.message("2","第 " + formulaComponentTemp.getRankNumber() + " 个原料编码或含量为空，不可导入！！！"));
            }
        }
        for (FormulaComponentTemp formulaComponentTemp : list) {
            formulaComponentTemp.setCreateTime(GenerateCreateTime.generate());
            boolean save = formulaComponentTempService.save(formulaComponentTemp);
            saveAll = saveAll && save;
        }
        if(saveAll){
            return Result.ok(ReturnMessage.message("1","导入成功"));
        }else{
            return Result.ok(ReturnMessage.message("0","可能存在个别成分导入失败，请核实！！！"));
        }
    }

}
