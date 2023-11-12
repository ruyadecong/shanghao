package com.xcc.system;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.common.utils.JwtHelper;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.qc.MaterialStandard;
import com.xcc.model.technology.FileData;
import com.xcc.model.temp.MaterialIngredientTemp;
import com.xcc.system.mapper.*;
import com.xcc.system.service.*;
import io.jsonwebtoken.Jwt;
import org.apache.poi.ss.formula.functions.Now;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
public class Test1 {

    @Autowired
    MaterialPurchaseService materialPurchaseService;
    @Autowired
    CallInspectService callInspectService;

    @Autowired
    MaterialBasedataService materialBasedataService;

    @Autowired
    MaterialIngredientTempService materialIngredientTempService;

    @Autowired
    MaterialStandardService materialStandardService;

    @Autowired
    MaterialIngredientService materialIngredientService;

    @Autowired
    MaterialIngredientMapper materialIngredientMapper;

    @Autowired
    MaterialBasedataMapper materialBasedataMapper;

    @Autowired
    FormulaComponentTempService formulaComponentTempService;

    @Autowired
    FileDataService fileDataService;



    @Test
    public void test2(){
        MaterialBasedata byCode = materialBasedataService.getByCode("101-000");
        System.out.println("resutl***************" + byCode);
        MaterialBasedata byCode1 = materialBasedataService.getByCode("101-001");
        System.out.println("resutl***************" + byCode1);
    }

    @Test
    public void test3(){
        List<String> suffixList = new ArrayList<>();
        for (int i = 1; i < 1000; i++) {
            if(i<10){
                suffixList.add("00" + i);
            } else if (i<100) {
                suffixList.add("0" + i);
            }else{
                suffixList.add(String.valueOf(i));
            }
        }
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 1000; i++) {
            if(i%3 == 0) {
                if (i < 10) {
                    list.add("00" + i);
                } else if (i < 100) {
                    list.add("0" + i);
                } else {
                    list.add(String.valueOf(i));
                }
            }
        }

        for (String s : list) {
            suffixList.remove(s);
        }


        for (String s : suffixList) {
            System.out.println(s);
        }
    }

    //测试mybatis根据id获取数据会不会包含is_deleted == 1
    @Test
    public void test4(){
        MaterialIngredientTemp byId1 = materialIngredientTempService.getById(2030);
        MaterialIngredientTemp byId2 = materialIngredientTempService.getById(2031);
        System.out.println(byId1);
        System.out.println(byId2);
    //结果：byId2输出为null，说明getById不会获取is_deleted == 1的记录
    }


    //检查material_basedata中的standard_number和material_standard中的真实条数的一致性
    @Test
    public void test5(){


        List<MaterialBasedata> getlist = materialBasedataService.getlist();



        List<Map<String,Integer>> listOfNumber = new ArrayList<>();
        List<Map<String,Integer>> listOfCount = new ArrayList<>();

        for (MaterialBasedata materialBasedata : getlist) {
            Map<String,Integer> mapOfNumber = new HashMap<>();
            Map<String,Integer> mapOfCount = new HashMap<>();

            String code;
            code = materialBasedata.getMaterialCode();
            QueryWrapper<MaterialStandard> wrapper = new QueryWrapper<>();
            wrapper.eq("material_code",code);
            Integer count = materialStandardService.count(wrapper);

            //System.out.println("number:" + materialBasedata.getStandardNumber() + "*****count:" + count);

            mapOfNumber.put(code,materialBasedata.getStandardNumber());
            mapOfCount.put(code,count);
            listOfNumber.add(mapOfNumber);
            listOfCount.add(mapOfCount);
        }
        for (Map<String, Integer> stringIntegerMap : listOfCount) {
            System.out.println(stringIntegerMap);
        }

        System.out.println("//////////////////////////////////////");
        for (Map<String, Integer> stringIntegerMap : listOfNumber) {
            System.out.println(stringIntegerMap);
        }
    }


    //找出存在于material_ingredient表而不存在于material_basedata表中的原料编码，这是由于测试原料信息修改功能时删除material_ingredient_temp而没有相应删除material_ingredient表中的数据的缘故
    //原本这两个表的数据是来源于不同的两个excel表格，所以存在有大量的数据不一致
    //存在于material_basedata而不存在于material_ingredient的话是没有问题的，反之会导致新增原料（material_ingredient有而material_basedata中没有也是可以进行新增原料的）出现问题
    @Test
    public void test6(){

        List<String> codeList = materialIngredientMapper.getCodeList();

        List<String> codeNotExistInBasedata = new ArrayList<>();
        for (String code : codeList) {
            MaterialBasedata materialBasedata = materialBasedataMapper.getByCode(code);
            if(materialBasedata == null){
                codeNotExistInBasedata.add(code);
            }
        }

        for (String codeNotExistInBasedatum : codeNotExistInBasedata) {
            System.out.println(codeNotExistInBasedatum);
        }
    }

    //时间格式测试
    @Test
    public void test7(){
        DateFormat af = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String rejectMark = af.format(new Date()) + "被驳回修改申请";
        System.out.println("**********************");
        System.out.println(rejectMark);
    }

    //测试生成token
    @Test
    public void test8(){
        String token = JwtHelper.createToken(1,"xcc");
        System.out.println(token);
    }


    //测试获取token数据
    @Test
    public void test9(){
        Long userId = JwtHelper.getUserId("eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLM0tjA0srAw0VEqLU4t8kwBikGYeYm5qUAtFcnJSrUAP2v2jEAAAAA.HDAlli6VlUTTJU8s4qVOd3hisMrKz3Ic-1ZJRZozN_B_8fd303f-ptYROdD54NavpkLIeb7e-z2_J3rxvNm8Eg");
        String username = JwtHelper.getUsername("eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLM0tjA0srAw0VEqLU4t8kwBikGYeYm5qUAtFcnJSrUAP2v2jEAAAAA.HDAlli6VlUTTJU8s4qVOd3hisMrKz3Ic-1ZJRZozN_B_8fd303f-ptYROdD54NavpkLIeb7e-z2_J3rxvNm8Eg");
        System.out.println("Id:"+userId+",username:"+username);
        System.out.println("test for git");

    }

    @Test
    public void test10(){
        List<FileData> material_basedata = fileDataService.getFileListByMaterialId(19, "material_basedata");
        for (FileData material_basedatum : material_basedata) {
            System.out.println(material_basedatum);

        }
    }

}
