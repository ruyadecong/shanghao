package com.xcc.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.common.utils.GenerateCreateTime;
import com.xcc.common.utils.ReturnMessage;
import com.xcc.model.order.*;
import com.xcc.model.product.ProductData;
import com.xcc.model.product.ProductFormula;
import com.xcc.model.technology.FormulaComponent;
import com.xcc.model.technology.FormulaData;
import com.xcc.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.Get;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "订单管理接口")
@RestController
@RequestMapping("/admin/inventory/order")
public class OrderController {

    @Autowired
    OrderDataService orderDataService;

    @Autowired
    OrderProductService orderProductService;

    @Autowired
    ProductDataService productDataService;

    @Autowired
    ProductFormulaService productFormulaService;

    @Autowired
    FormulaDataService formulaDataService;

    @Autowired
    OrderProductFormulaService orderProductFormulaService;

    @Autowired
    OrderSemiFormulaService orderSemiFormulaService;

    @Autowired
    FormulaComponentService formulaComponentService;

    @Autowired
    OrderFormulaComponentService orderFormulaComponentService;

    @Autowired
    OrderSemiComponentService orderSemiComponentService;

    //根据查询条件分页获取订单数据
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result getOrderPageList(@PathVariable Long page,
                                   @PathVariable Long limit,
                                   OrderSearchVo vo){
        Page<OrderData> pageParam = new Page<>(page,limit);
        IPage<OrderData> pageModel = orderDataService.selectPage(pageParam,vo);
        return Result.ok(pageModel);
    }

    //根据订单id获取订单的产品
    @ApiOperation("根据订单id获取订单的产品")
    @GetMapping("getProductByOrderId/{orderId}")
    public Result getProductByOrderId(@PathVariable Integer orderId){
        List<OrderProduct> orderProductList = orderProductService.getProductByOrderId(orderId);
        return Result.ok(orderProductList);
    }

    //添加新订单（编码）
    @ApiOperation("添加新订单（编码）")
    @GetMapping("confirmForAddOrder")
    public Result confirmForAddOrder(OrderSearchVo vo){
        String orderCode = vo.getOrderCode();
        //先判断orderCode是否已经存在，order_code为unique
        QueryWrapper<OrderData> wrapper = new QueryWrapper<>();
        wrapper.eq("order_code",orderCode);
        List<OrderData> list = orderDataService.list(wrapper);
        if(list.size() > 0) {
            return Result.ok(ReturnMessage.message("0", "订单添加失败，订单编码：" + orderCode + " 已经存在！！！"));
        }
        //由于order_code为unique，所以存在逻辑删除了的情况，此时list.size() == 0，但仍然不能往数据库中添加数据
        Integer num = orderDataService.countByOrderCode(orderCode);
        if(num > 0){
            return Result.ok(ReturnMessage.message("2", "订单添加失败，订单编码：" + orderCode + " 已经存在且被逻辑删除，如确实需要添加，请联系管理员！！！"));
        }
        OrderData orderData = new OrderData();
        orderData.setOrderCode(orderCode);
        orderData.setCreateTime(GenerateCreateTime.generate());
        boolean save = orderDataService.save(orderData);
        if(save){
           return Result.ok(ReturnMessage.message("1","订单：" + orderCode + " 添加成功"));
        }
        return Result.ok(ReturnMessage.message("2","订单添加失败，请联系管理员！！！"));
    }


    //根据产品编码前几位获取建议列表
    @ApiOperation("根据产品编码前几位获取建议列表")
    @GetMapping("getListOfProductCode")
    public Result getListOfProductCode(OrderSearchVo vo){
        String productCode = vo.getProductCode();
        String orderCode = vo.getOrderCode();
        //这个参数是用于决定从产品库还是以往订单中获取产品信息
        String selectModel = vo.getSelectModel();
        if("从产品库中选择".equals(selectModel)){
            List<String> listOfSuggest = orderProductService.getSuggestListOfProductCode(productCode);
            return Result.ok(listOfSuggest);
        } else if ("从以往订单产品中选择".equals(selectModel)) {
            List<String> listOfSuggest = orderProductService.getSuggestListOfOrderProductCode(productCode,orderCode);
            return Result.ok(listOfSuggest);
        } else {
            return Result.ok(ReturnMessage.message("0","前端传回的类型有误"));
        }
    }

    //根据所选择的产品信息来源和产品编码，为订单增加产品
    @ApiOperation("为订单增加产品")
    @GetMapping("addProductForOrder")
    public Result addProductForOrder(OrderSearchVo vo){
        String selectModel = vo.getSelectModel();
        String productCode = vo.getProductCode();
        String orderCode = vo.getOrderCode();
        //用来保存不在formula_data中，却在product_formula中的配方编码
        List<String> emptyFormula = new ArrayList<>();
        //用来保存保存失败的formula_data的配方编码
        List<String> failSaveFormula = new ArrayList<>();
        //用来保存保存失败的配方成分的配方编码
        List<String> failSaveComponent = new ArrayList<>();
        if("从产品库中选择".equals(selectModel)){
            //进一步核实产品数据库中是否有前端选择的产品编码
            List<ProductData> productDataList = productDataService.getByProductCode(productCode);
            if(productDataList.size() > 0){
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrderCode(orderCode);
                orderProduct.setProductCode(productCode);
                for (ProductData productData : productDataList) {
                    orderProduct.setNetWeight(productData.getNetWeight());
                    orderProduct.setWeightModel(productData.getWeightModel());
                    orderProduct.setUnit(productData.getUnit());
                }
                orderProduct.setCreateTime(GenerateCreateTime.generate());
                boolean save = orderProductService.save(orderProduct);
                //如果保存失败，直接返回消息，反之跳过return继续后面的代码
                if(!save){
                    return Result.ok(ReturnMessage.message("0","为订单新增产品失败"));
                }
                //产品data保存成功后，获取产品的formula
                List<ProductFormula> productFormulaList = productFormulaService.getByProductCode(productCode);
                if(productFormulaList.size() > 0){
                    for (ProductFormula productFormula : productFormulaList) {
                        //获取配方在formula_data中的数据
                        FormulaData formulaData = formulaDataService.getByCode(productFormula.getFormulaCode());
                        if(formulaData != null){
                            //这里拿到的formulaData应该是产品的配方，不是中间料的，但是这里还是核实一下
                            //类型标记，默认为0（产品配方），1为中间料配方
                            if(formulaData.getClassMark().intValue() == 0) {   //如果是产品配方则存入order_product_formula
                                OrderProductFormula orderProductFormula = new OrderProductFormula();
                                orderProductFormula.setOrderProductId(orderProduct.getId());
                                orderProductFormula.setFormulaCode(productFormula.getFormulaCode());
                                orderProductFormula.setColorNumber(productFormula.getColorNumber());
                                orderProductFormula.setFormulaContent(productFormula.getFormulaContent());
                                orderProductFormula.setFormulaMark(productFormula.getFormulaMark());
                                orderProductFormula.setClassMark(formulaData.getClassMark());
                                orderProductFormula.setMarkOne(formulaData.getMarkOne());
                                orderProductFormula.setMarkTwo(formulaData.getMarkTwo());
                                orderProductFormula.setMarkThree(formulaData.getMarkThree());
                                orderProductFormula.setRemark(formulaData.getRemark());
                                orderProductFormula.setCreateTime(GenerateCreateTime.generate());
                                boolean save1 = orderProductFormulaService.save(orderProductFormula);
                                if(!save1){
                                    failSaveFormula.add(formulaData.getFormulaCode());
                                }else{
                                    //如果配方数据保存成功，则开始保存配方成分
                                    List<FormulaComponent> formulaComponentList = formulaComponentService.getListByCode(formulaData.getFormulaCode());
                                    boolean saveComponent = true;
                                    if(formulaComponentList.size() > 0){
                                        for (FormulaComponent formulaComponent : formulaComponentList) {
                                            OrderFormulaComponent orderFormulaComponent = new OrderFormulaComponent();
                                            orderFormulaComponent.setOrderFormulaId(orderProductFormula.getId());
                                            //orderFormulaComponent.setClassMark(orderProductFormula.getClassMark());
                                            orderFormulaComponent.setRankNumber(formulaComponent.getRankNumber());
                                            orderFormulaComponent.setMaterialCode(formulaComponent.getMaterialCode());
                                            //如果MaterialCode在产品配方表中，且属于中间料类，则同时要将此MaterialCode存入order_semi_formula表中
                                            FormulaData byCode = formulaDataService.getByCode(formulaComponent.getMaterialCode());

                                            //用于保存orderSemiFormulaService进行save时的生成的id，最后用于保存进order_formula_component的order_semi_id中
                                            Integer orderSemiId = 0;

                                            if(byCode != null){
                                                //进一步判断其classMark是否为1
                                                if(byCode.getClassMark().intValue() == 1){
                                                    orderFormulaComponent.setClassMark(1);
                                                    //确定是中间料配方，那么先将配方信息存入order_semi_formula中
                                                    OrderSemiFormula orderSemiFormula = new OrderSemiFormula();
                                                    orderSemiFormula.setOrderProductId(orderProduct.getId());
                                                    orderSemiFormula.setSemiCode(byCode.getFormulaCode());
                                                    orderSemiFormula.setClassMark(1);
                                                    orderSemiFormula.setRemark(byCode.getRemark());
                                                    orderSemiFormula.setCreateTime(GenerateCreateTime.generate());
                                                    boolean saveSemi = orderSemiFormulaService.save(orderSemiFormula);

                                                    orderSemiId = orderSemiFormula.getId();

                                                    //然后获取中间料的配方的成分数据，并将其存入order_formula_component表中
                                                    List<FormulaComponent> listByCode = formulaComponentService.getListByCode(byCode.getFormulaCode());
                                                    if(listByCode.size() > 0){
                                                        for (FormulaComponent component : listByCode) {
                                                            OrderSemiComponent semiComponent = new OrderSemiComponent();
                                                            semiComponent.setOrderSemiId(orderSemiFormula.getId());

                                                            //接下来要处理二级中间料的问题
                                                            //如果中间料的组分是原料，则将classMark设置为0
                                                            //如果中间料的组分是二级中间料，则将classMark设置为1，并将其存入order_semi_formula中，并从formula_component中获得其数据存入order_semi_component中
                                                            String semiComponentCode = component.getMaterialCode();
                                                            FormulaData semiByCode = formulaDataService.getByCode(semiComponentCode);

                                                            Integer semiSemiId = 0;

                                                            if(semiByCode != null){         //成立则说明这个成分是二级中间料
                                                                //进一步判断其classMark是否为1
                                                                if(semiByCode.getClassMark().intValue() == 1){
                                                                    //确定是二级中间料配方，那么要先将配方信息存入order_semi_formula中
                                                                    semiComponent.setClassMark(1);      //如果是二级中间料，那么其作为组分存入order_semi_component之前先要将其classMark设置为1
                                                                    OrderSemiFormula semiSemiFormula = new OrderSemiFormula();
                                                                    semiSemiFormula.setOrderProductId(orderProduct.getId());
                                                                    semiSemiFormula.setSemiCode(semiByCode.getFormulaCode());
                                                                    semiSemiFormula.setClassMark(1);
                                                                    semiSemiFormula.setRemark(semiByCode.getRemark());
                                                                    semiSemiFormula.setCreateTime(GenerateCreateTime.generate());
                                                                    boolean saveSemiSemi = orderSemiFormulaService.save(semiSemiFormula);
                                                                    semiSemiId = semiSemiFormula.getId();

                                                                    //遍历二级中间料的组分，并存入order_semi_component之中
                                                                    List<FormulaComponent> semiSemiComponents = formulaComponentService.getListByCode(semiByCode.getFormulaCode());

                                                                    if(semiSemiComponents.size() > 0){
                                                                        for (FormulaComponent semiSemiComponent : semiSemiComponents) {
                                                                            OrderSemiComponent semiSemi = new OrderSemiComponent();
                                                                            semiSemi.setOrderSemiId(semiSemiId);
                                                                            semiSemi.setClassMark(0);
                                                                            semiSemi.setRankNumber(semiSemiComponent.getRankNumber());
                                                                            semiSemi.setMaterialCode(semiSemiComponent.getMaterialCode());
                                                                            semiSemi.setMaterialContent(semiSemiComponent.getMaterialContent());
                                                                            semiSemi.setMaterialPurpose(semiSemiComponent.getMaterialPurpose());
                                                                            semiSemi.setRemark(semiSemiComponent.getRemark());
                                                                            semiSemi.setCreateTime(GenerateCreateTime.generate());
                                                                            boolean saveSemiSemiComponent = orderSemiComponentService.save(semiSemi);

                                                                        }
                                                                    }
                                                                }
                                                            }else {
                                                                semiComponent.setClassMark(0);
                                                            }
                                                            //上面的判断成分是否二级中间料的if里面的代码是将确定是二级中间料的组分的信息存入order_semi_formula和order_semi_component中的
                                                            //其本身的信息还是要存入order_semi_component中
                                                            //其中的第二个if就是将需要存入order_semi_component中的原料（非二级中间料）的classMark设置为0
                                                            //如果是二级中间料，那么其作为组分存入order_semi_component之前先要将其classMark设置为1
                                                            //如果没有进入上面的if（即不是二级中间料），那么保存二级中间料在order_semi_formula中的id的component_mark设置为0（即初始化的semiSemiId）
                                                            semiComponent.setComponentMark(semiSemiId);
                                                            semiComponent.setRankNumber(component.getRankNumber());
                                                            semiComponent.setMaterialCode(semiComponentCode);
                                                            semiComponent.setMaterialContent(component.getMaterialContent());
                                                            semiComponent.setMaterialPurpose(component.getMaterialPurpose());
                                                            semiComponent.setRemark(component.getRemark());
                                                            semiComponent.setCreateTime(GenerateCreateTime.generate());
                                                            boolean saveSemiComponent = orderSemiComponentService.save(semiComponent);

                                                        }
                                                    }
                                                }
                                            }
                                            //如果没有进入上面的if，则说明这个原料不是中间料，那么它也就不会在if中被赋予id值，只是保持为初始化时的0
                                            orderFormulaComponent.setOrderSemiId(orderSemiId);
                                            orderFormulaComponent.setMaterialContent(formulaComponent.getMaterialContent());
                                            orderFormulaComponent.setMaterialPurpose(formulaComponent.getMaterialPurpose());
                                            orderFormulaComponent.setRemark(formulaComponent.getRemark());
                                            orderFormulaComponent.setCreateTime(GenerateCreateTime.generate());
                                            boolean save2 = orderFormulaComponentService.save(orderFormulaComponent);
                                            saveComponent = saveComponent && save2;
                                        }
                                    }
                                    if(!saveComponent){
                                        failSaveComponent.add(formulaData.getFormulaCode());
                                    }
                                }

                            }else{      //如果是中间料配方则存入order_semi_formula
                                OrderSemiFormula orderSemiFormula = new OrderSemiFormula();
                                orderSemiFormula.setOrderProductId(orderProduct.getId());
                                orderSemiFormula.setSemiCode(formulaData.getFormulaCode());
                                orderSemiFormula.setClassMark(1);
                                orderSemiFormula.setRemark(formulaData.getRemark());
                                orderSemiFormula.setCreateTime(GenerateCreateTime.generate());
                                boolean saveSemi = orderSemiFormulaService.save(orderSemiFormula);
                                //然后获取中间料的配方的成分数据，并将其存入order_formula_component表中
                                List<FormulaComponent> listByCode = formulaComponentService.getListByCode(formulaData.getFormulaCode());
                                if(listByCode.size() > 0){
                                    for (FormulaComponent component : listByCode) {
                                        OrderFormulaComponent semiComponent = new OrderFormulaComponent();
                                        semiComponent.setOrderFormulaId(orderSemiFormula.getId());
                                        //semiComponent.setClassMark(1);
                                        semiComponent.setRankNumber(component.getRankNumber());
                                        semiComponent.setMaterialCode(component.getMaterialCode());
                                        semiComponent.setMaterialContent(component.getMaterialContent());
                                        semiComponent.setMaterialPurpose(component.getMaterialPurpose());
                                        semiComponent.setRemark(component.getRemark());
                                        semiComponent.setCreateTime(GenerateCreateTime.generate());
                                        boolean saveSemiComponent = orderFormulaComponentService.save(semiComponent);
                                    }
                                }

                                orderSemiFormulaService.save(orderSemiFormula);
                            }

                        }else{
                            emptyFormula.add(productFormula.getFormulaCode());
                        }

                    }
                    /*
                    用来保存不在formula_data中，却在product_formula中的配方编码
                    List<String> emptyFormula = new ArrayList<>();
                    //用来保存保存失败的formula_data的配方编码
                    List<String> failSaveFormula = new ArrayList<>();
                    //用来保存保存失败的配方成分的配方编码
                    List<String> failSaveComponent = new ArrayList<>();
                    */
                    String str1 = "";
                    String str2 = "";
                    String str3 = "";
                    if(emptyFormula.size() > 0){
                        str1 = "产品配方：";
                        for (String s : emptyFormula) {
                            str1 = str1 + "，" + s;
                        }
                        str1 = str1 + " 的数据不存在于数据库中";
                    }
                    if(failSaveFormula.size() > 0){
                        str2 = "产品配方：";
                        for (String s : emptyFormula) {
                            str2 = str2 + "，" + s;
                        }
                        str2 = str2 + " 的数据保存失败";
                    }
                    if(failSaveComponent.size() > 0){
                        str3 = "产品配方：";
                        for (String s : emptyFormula) {
                            str3 = str3 + "，" + s;
                        }
                        str3 = str3 + " 的成分保存失败";
                    }
                    if(!("".equals(str2))){
                        str1 = str1 + str2;
                    }
                    if(!("".equals(str3))){
                        str1 = str1 + str3;
                    }
                    if(str1.length() > 0){
                        return Result.ok(ReturnMessage.message("2","产品数据导入成功，但是" + str1));
                    }else{
                        return Result.ok(ReturnMessage.message("1","产品及关联的所有数据导入成功"));
                    }
                }else{
                    return Result.ok(ReturnMessage.message("2","获取所添加产品的配方失败，请联系管理员"));
                }

            }else {
                return Result.ok(ReturnMessage.message("0","产品数据库中无此产品，请确认"));
            }
        } else if ("从以往订单产品中选择".equals(selectModel)) {
            //注：这中模式下的productCode是由orderCode和productCode连接成的，两者之间通过 $#$ 区隔
            //但是前端传回来的orderCode和productCode里面的那个orderCode不是同一个
            //前端传回来的orderCode是需要增加产品的订单编码，productCode里面的那个orderCode是供选择的产品所属于的订单编码
            //根据sql的设置，需要增加产品的订单不能从自身已经存在的产品中选择一个来增加，所以前端传回来的orderCode和productCode里面的那个orderCode不可能是一样的
            int index = productCode.indexOf("$#$");
            if(index != -1){
                String str = productCode;
                productCode = productCode.substring(index +3);
                String originalOrderCode = "$#$" + productCode;
                originalOrderCode = str.replace(originalOrderCode,"");

                /*System.out.println("//////////////////////////////////");
                System.out.println("orderCode = " + originalOrderCode);
                System.out.println("productCode = " + productCode);*/

                QueryWrapper<OrderProduct> wrapper = new QueryWrapper<>();
                wrapper.eq("order_code",originalOrderCode);
                wrapper.eq("product_code",productCode);
                wrapper.eq("is_deleted",0);
                List<OrderProduct> list = orderProductService.list(wrapper);
                if(list.size() != 1){
                    return Result.ok(ReturnMessage.message("2","后端提取订单：" + originalOrderCode + " 中的产品：" + productCode + "失败，请联系管理员"));
                }else{
                    OrderProduct product = new OrderProduct();
                    Integer oldProductId;
                    oldProductId = 0;
                    for (OrderProduct orderProduct : list) {
                        oldProductId = orderProduct.getId();
                        product = orderProduct;
                    }
                    //提取了前端所选择的订单的产品信息后，将此产品相关联的所有信息写入新的订单的新产品中
                    product.setOrderCode(orderCode);             //这里的orderCode就是前端传回来的需要加产品的订单的orderCode
                    product.setProductAmount(new BigDecimal(0));
                    product.setProductType("");
                    product.setBatchNo("");
                    product.setCreateTime(GenerateCreateTime.generate());
                    boolean saveProduct = orderProductService.save(product);
                    if(!saveProduct){
                        return Result.ok(ReturnMessage.message("0","保存产品信息失败，请联系管理员"));
                    }
                    //以下逐级保存产品的配方、中间料配方数据
                    //由于formulaComponent中含有中间料的配方id，所以先处理中间料数据

                    //1 保存中间料配方数据、组分
                    //1.1 先保存数据
                    QueryWrapper<OrderSemiFormula> wrapper3 = new QueryWrapper<>();
                    wrapper3.eq("order_product_id",oldProductId);
                    wrapper3.eq("is_deleted",0);
                    //拿到原来的product的semiFormula
                    List<OrderSemiFormula> list1 = orderSemiFormulaService.list(wrapper3);
                    if(list1.size() > 0){
                        for (OrderSemiFormula orderSemiFormula : list1) {
                            Integer oldSemiId = orderSemiFormula.getId();
                            orderSemiFormula.setOrderProductId(product.getId());
                            orderSemiFormula.setCreateTime(GenerateCreateTime.generate());
                            boolean save = orderSemiFormulaService.save(orderSemiFormula);
                            if(!save){
                                return Result.ok(ReturnMessage.message("0","保存中间料信息失败，请联系管理员"));
                            }
                            //根据原来的product的semiFormula的id获取其componentList
                            QueryWrapper<OrderSemiComponent> wrapper4 = new QueryWrapper<>();
                            wrapper4.eq("order_semi_id",oldSemiId);
                            wrapper4.eq("is_deleted",0);
                            List<OrderSemiComponent> list2 = orderSemiComponentService.list(wrapper4);
                            if(list2.size() > 0){
                                for (OrderSemiComponent orderSemiComponent : list2) {
                                    orderSemiComponent.setOrderSemiId(orderSemiFormula.getId());
                                    orderSemiComponent.setCreateTime(GenerateCreateTime.generate());
                                    /*//这一部分代码可能要放在所有中间料数据拷贝完成之后
                                    if(orderSemiComponent.getClassMark() == 1){
                                        //orderSemiComponent.setComponentMark(orderSemiFormula.getId());
                                        QueryWrapper<OrderSemiFormula> wrapper6 = new QueryWrapper<>();
                                        wrapper6.eq("is_deleted",0);
                                        wrapper6.eq("order_product_id",product.getId());
                                        wrapper6.eq("semi_code",orderSemiComponent.getMaterialCode());
                                        List<OrderSemiFormula> list3 = orderSemiFormulaService.list(wrapper6);
                                        if(list3.size() > 0){
                                            for (OrderSemiFormula semiFormula : list3) {
                                                orderSemiComponent.setComponentMark(semiFormula.getId());
                                            }
                                            if(list3.size() != 1){
                                                orderSemiComponent.setRemark("数据可能存在错误");
                                            }
                                        }else{
                                            orderSemiComponent.setRemark("二级中间料组分数据错误");
                                            orderSemiComponent.setComponentMark(0);
                                        }
                                    }*/
                                    //对上面被注释的代码的补充
                                    //用于在拷贝过程中先将component_mark的置为-1，作为特殊的标记
                                    if(orderSemiComponent.getClassMark().intValue() == 1){
                                        orderSemiComponent.setComponentMark(-1);
                                    }
                                    boolean save1 = orderSemiComponentService.save(orderSemiComponent);
                                    if(!save1){
                                        return Result.ok(ReturnMessage.message("0","保存中间料组分失败，请联系管理员"));
                                    }
                                }
                            }
                        }
                    }


                    //1.2 下面处理order_semi_component表中的二级中间料的component_mark的值
                    //1.2.1 先获取上面代码保存的该产品下的中间料id
                    QueryWrapper<OrderSemiFormula> wrapper6 = new QueryWrapper<>();
                    wrapper6.eq("order_product_id",product.getId());
                    wrapper6.eq("is_deleted",0);
                    List<OrderSemiFormula> list3 = orderSemiFormulaService.list(wrapper6);
                    //1.2.2 二级中间料隶属于某一个一级中间料（也就是list3里面的某一个），那么就根据以下策略来找出order_semi_component中需要修改的数据
                    //第一步：找出component_mark == -1 的记录（这个是在拷贝中间料组分数据时对二级中间料做的标记）
                    //第二步：找出第一步的结果中的classMark == 1 且 order_semi_id 在list3中的数据，这里是为了防止以前拷贝的数据的component_mark仍为-1的情况
                    //第三步：比照material_code和list3中的semi_code对component_mark进行修改，如果一个material_code在list3中有多个semi_code对应，则需要对在remark上说明
                    List<Integer> idList = new ArrayList<>();
                    for (OrderSemiFormula orderSemiFormula : list3) {
                        idList.add(orderSemiFormula.getId());
                    }
                    QueryWrapper<OrderSemiComponent> wrapper7 = new QueryWrapper<>();
                    wrapper7.eq("is_deleted",0);
                    wrapper7.eq("component_mark",-1);
                    wrapper7.in("order_semi_id",idList);
                    List<OrderSemiComponent> semiSemiList = orderSemiComponentService.list(wrapper7);
                    for (OrderSemiComponent orderSemiComponent : semiSemiList) {
                        Integer k = 0;              //用于标记下面的循环中找到相同semiCode的个数
                        for (OrderSemiFormula orderSemiFormula : list3) {
                            String semiCode = orderSemiFormula.getSemiCode();
                            if(semiCode.equals(orderSemiComponent.getMaterialCode())){
                                k++;
                                orderSemiComponent.setComponentMark(orderSemiFormula.getId());
                            }
                        }
                        if(k.intValue() == 0){
                            orderSemiComponent.setRemark("数据拷贝异常，未取得此二级中间料的组分数据");
                        }
                        if(k.intValue() == 1){
                            orderSemiComponent.setRemark("");
                        }
                        if(k.intValue() > 1){
                            orderSemiComponent.setRemark("数据拷贝不严谨，配方中可能存在多个相同的二级中间料");
                        }
                        orderSemiComponentService.updateById(orderSemiComponent);
                    }


                    //2 保存产品配方数据、组分
                    QueryWrapper<OrderProductFormula> wrapper1 = new QueryWrapper<>();
                    wrapper1.eq("order_product_id",oldProductId);
                    wrapper1.eq("is_deleted",0);
                    /*System.out.println("/////////////////////////////////////////////////");
                    System.out.println(oldProductId);*/
                    //拿到原来的product的formula
                    List<OrderProductFormula> formulaList = orderProductFormulaService.list(wrapper1);
                    if(formulaList.size() > 0){
                        for (OrderProductFormula orderProductFormula : formulaList) {
                            Integer oldFormulaId = orderProductFormula.getId();
                            orderProductFormula.setOrderProductId(product.getId());
                            orderProductFormula.setCreateTime(GenerateCreateTime.generate());
                            boolean save = orderProductFormulaService.save(orderProductFormula);
                            if(!save){
                                return Result.ok(ReturnMessage.message("0","保存产品配方数据失败，请联系管理员"));
                            }
                            //根据原来的product的formula的id获取其componentList
                            QueryWrapper<OrderFormulaComponent> wrapper2 = new QueryWrapper<>();
                            wrapper2.eq("order_formula_id",oldFormulaId);
                            wrapper2.eq("is_deleted",0);
                            List<OrderFormulaComponent> oldFormulaList = orderFormulaComponentService.list(wrapper2);
                            if(oldFormulaList.size() > 0) {
                                for (OrderFormulaComponent orderFormulaComponent : oldFormulaList) {
                                    orderFormulaComponent.setOrderFormulaId(orderProductFormula.getId());
                                    orderFormulaComponent.setCreateTime(GenerateCreateTime.generate());
                                    boolean save1 = true;
                                    if(orderFormulaComponent.getClassMark().intValue() == 1){
                                        QueryWrapper<OrderSemiFormula> wrapper5 = new QueryWrapper<>();
                                        wrapper5.eq("is_deleted",0);
                                        wrapper5.eq("order_product_id",product.getId());
                                        wrapper5.eq("semi_code",orderFormulaComponent.getMaterialCode());
                                        List<OrderSemiFormula> list2 = orderSemiFormulaService.list(wrapper5);
                                        if(list2.size() > 0) {
                                            for (OrderSemiFormula orderSemiFormula : list2) {
                                                orderFormulaComponent.setOrderSemiId(orderSemiFormula.getId());
                                            }
                                            if(list2.size() != 1){
                                                orderFormulaComponent.setRemark("中间料组分数据不能确定是否正确");
                                            }
                                        } else{
                                            orderFormulaComponent.setRemark("中间料组分数据错误");
                                            orderFormulaComponent.setOrderSemiId(0);
                                        }
                                    }
                                    save1 = orderFormulaComponentService.save(orderFormulaComponent);
                                    if (!save1) {
                                        return Result.ok(ReturnMessage.message("0", "保存产品配方组分失败，请联系管理员"));
                                    }
                                }
                            }

                        }
                    }


                }
                return Result.ok(ReturnMessage.message("1","新增产品成功"));
            }else{
                return Result.ok(ReturnMessage.message("2","后端分离产品编码失败，请联系管理员"));
            }


        }else{
/*            System.out.println("/////////////////////////////////////////");
            System.out.println(vo.getSelectModel());*/
            return Result.ok(ReturnMessage.message("0","增加产品失败，前端传回的类型有误"));
        }
    }


    //根据产品id逻辑删除指定订单下的指定产品的数据
    //订单的指定是通过查看订单的产品信息时选中的订单来实现的
    @ApiOperation("根据id逻辑删除order_product中的记录（及关联记录）")
    @DeleteMapping("removeProductById/{productId}")
    public Result removeProductById(@PathVariable Integer productId){


        return null;
    }


    //根据产品id获取其订单列表
    @ApiOperation("根据产品id获取其订单列表")
    @GetMapping("getFormulaListByProductId/{productId}")
    public Result getFormulaListByProductId(@PathVariable Integer productId){
        QueryWrapper<OrderProductFormula> wrapper = new QueryWrapper<>();
        wrapper.eq("order_product_id",productId);
        List<OrderProductFormula> formulaList = orderProductFormulaService.list(wrapper);
        return Result.ok(formulaList);
    }


    //根据配方id获取其成分列表
    @ApiOperation("根据配方id获取其成分列表")
    @GetMapping("getComponentByFormulaId/{formulaId}")
    public Result getComponentByFormulaId(@PathVariable Integer formulaId){
        //首先要判断formula的类别
        OrderProductFormula formula = orderProductFormulaService.getById(formulaId);

        if(formula.getClassMark().intValue() == 0){    //产品配方
            QueryWrapper<OrderFormulaComponent> wrapper = new QueryWrapper<>();
            wrapper.eq("order_formula_id",formulaId);
            List<OrderFormulaComponent> list = orderFormulaComponentService.list(wrapper);
            return Result.ok(list);
        } else {
            return Result.ok(ReturnMessage.message("0","配方的类别出现问题，请联系管理员"));
        }
    }

    //根据中间料的id获取其成分列表
    @ApiOperation("根据中间料的id获取其成分列表")
    @GetMapping("getSemiComponentBySemiId/{semiId}")
    public Result getSemiComponentBySemiId(@PathVariable Integer semiId){
        QueryWrapper<OrderSemiComponent> wrapper = new QueryWrapper<>();
        wrapper.eq("order_semi_id",semiId);
        List<OrderSemiComponent> semiComponentList = orderSemiComponentService.list(wrapper);
        return Result.ok(semiComponentList);
    }

    //根据二级中间料的id获取其成分列表
    @ApiOperation("根据二级中间料的id获取其成分列表")
    @GetMapping("getSemiSemiComponentBySemiSemiId/{semiSemiId}")
    public Result getSemiSemiComponentBySemiSemiId(@PathVariable Integer semiSemiId){

        QueryWrapper<OrderSemiComponent> wrapper = new QueryWrapper<>();
        wrapper.eq("order_semi_id",semiSemiId);
        List<OrderSemiComponent> semiSemiComponentList = orderSemiComponentService.list(wrapper);
        return Result.ok(semiSemiComponentList);

    }








    //以下为自动保存修改的数据的接口
    @ApiOperation("自动保存修改的数据")
    @PutMapping("autoSave")
    public Result autoSave(@RequestBody OrderSearchVo vo){
        //System.out.println(vo.getModel());
        //vo中的model用于接收前端传回的修改的数据的类型
        //order：订单数据
        //product：订单的产品列表
        //formulaList：产品的配方列表
        //formulaComponent：配方的成分（原料、中间料）列表
        //semiComponent：中间料配方的原料列表
        String model = vo.getModel();
        boolean b = true;
        String code = "1";
        String msg = "修改成功";
        switch (model){
            case "order":
                OrderData orderData = vo.getOrderData();
                if(orderData.getOrderMoney() == null){
                    orderData.setOrderMoney(new BigDecimal(0));
                }
                b = orderDataService.updateById(orderData);
                break;

            case "product":
                OrderProduct orderProduct = vo.getOrderProduct();
                if(orderProduct.getProductAmount() == null){
                    orderProduct.setProductAmount(new BigDecimal(0));
                }
                b = orderProductService.updateById(orderProduct);
                break;

            case "formulaList":
                OrderProductFormula orderProductFormula = vo.getOrderProductFormula();
                if(orderProductFormula.getFormulaContent() == null){
                    orderProductFormula.setFormulaContent(new BigDecimal(0));
                }
                b = orderProductFormulaService.updateById(orderProductFormula);
                break;

            case "formulaComponent":
                OrderFormulaComponent orderFormulaComponent = vo.getOrderFormulaComponent();
                if(orderFormulaComponent.getMaterialContent() == null){
                    orderFormulaComponent.setMaterialContent(new BigDecimal(0));
                }
                b = orderFormulaComponentService.updateById(orderFormulaComponent);
                break;

            case "semiComponent":
            case "semiSemiComponent":
                OrderSemiComponent orderSemiComponent = vo.getOrderSemiComponent();
                if(orderSemiComponent.getMaterialContent() == null){
                    orderSemiComponent.setMaterialContent(new BigDecimal(0));
                }
                b = orderSemiComponentService.updateById(orderSemiComponent);
                break;

            default:
                code = "2";
                msg = "修改失败（后端Switch语句失效），请联系管理员";
                break;

        }
        if(!b){
            code = "0";
            msg = "修改失败，请联系管理员";
        }

        return Result.ok(ReturnMessage.message(code,msg));
    }


    //根据id和model进行逻辑删除
    //id和model封装在vo里面
    @ApiOperation("根据id和model进行删除")
    @DeleteMapping("remove")
    public Result remove(@RequestBody OrderSearchVo vo){
        String model = vo.getModel();
        Integer id = vo.getId();
        boolean b = true;
        String code = "1";
        String msg = "修改成功";
        switch (model){
            case "order":
                b = orderDataService.removeOrder(id);
                model = "订单数据";
                break;

            case "product":
                b = orderProductService.removeProduct(id);
                model = "订单中的产品";
                break;

            case "formulaList":
                b = orderProductFormulaService.removeFormula(id);
                model = "产品中的配方";
                break;

            case "formulaComponent":
                b = orderFormulaComponentService.removeFormulaComponent(id);
                model = "配方中的组分";
                break;

            case "semiComponent":
            case "semiSemiComponent":
                b = orderSemiComponentService.removeSemiComponent(id);
                model = "中间料配方中的原料";
                break;

            default:
                code = "2";
                msg = "删除失败（后端Switch语句失效），请联系管理员";
                break;

        }
        if(b) {
            return Result.ok(ReturnMessage.message("1", "删除 " + model + " 成功"));
        }else{
            return Result.ok(ReturnMessage.message("0", "删除 " + model + " 失败，请联系管理员"));
        }
    }


    //根据前端传回的信息新增配方/中间料/原料
    /*前端传回的信息有四部分
        1、model：表明新增的是什么，有：formula、formulaComponent、semiFormulaComponent、semiSemiFormulaComponent
        2、iDForAdd：表明所增加的数据所属的上级的id
        3、selectAddSource：表明所增加的数据的来源，有：数据库配方，空白配方，数据库原料，空白原料，数据库中间料，空白中间料
        4、newAddCode：表面所新增的数据的编码（配方编码、中间料编码、原料编码）
     */
    @ApiOperation("为产品、配方、中间料新增配方、中间料、原料")
    @PostMapping("confirmForAdd")
    public Result confirmForAdd(@RequestBody OrderSearchVo vo){
/*        System.out.println("///////////////////////////////////");
        System.out.println(vo);*/
        boolean a = true;
        String newCode = vo.getNewAddCode();
        Integer idForAdd = vo.getId();
        if(vo.getModel().equals("") || vo.getModel() == null){
            a = false;
        }
        if(vo.getId().equals(0) || vo.getId() == null){
            a = false;
        }
        if(vo.getSelectAddSource().equals("") || vo.getSelectAddSource() == null){
            a = false;
        }
        if(vo.getNewAddCode().equals("") || vo.getNewAddCode() == null){
            a = false;
        }
        if(!a){
            return Result.ok(ReturnMessage.message("2","前端传给后端的数据错误，请联系管理员！！！"));
        }
        String source = vo.getSelectAddSource();
        boolean isFromDataBase = true;              //此变量用于标记是否从数据库中提取数据
        int classMark  = 1;                                 //此变量用于标记所增加的数据所属类别：0表示原料，1表示中间料，2表示给二级中间料添加的原料
        if(source.contains("数据库")){
            isFromDataBase = true;
        }
        if(source.contains("空白")){
            isFromDataBase = false;
        }
        if(source.contains("中间料")){
            classMark = 1;
        }
        if(source.contains("原料")){
            classMark = 0;
        }
        Map<String,String> message = new HashMap<>();

        switch (vo.getModel()){
            case "formula":
                message = orderProductFormulaService.addNewFormula(idForAdd,newCode,isFromDataBase);
                break;
            case "formulaComponent":
                message = orderFormulaComponentService.addNewFormulaComponent(idForAdd,newCode,isFromDataBase,classMark);
                break;
            case "semiFormulaComponent":
                message = orderSemiComponentService.addNewSemiComponent(idForAdd,newCode,isFromDataBase,classMark);
                break;
            case "semiSemiFormulaComponent":
                if(classMark != 0){         //只有在classMark==0的情况下才能给二级中间料添加组分，即二级中间料所添加的组分只能是原料
                    return Result.ok(ReturnMessage.message("2","前端传给后端的selectAddSource错误，请联系管理员！！！"));
                }
                message = orderSemiComponentService.addNewSemiComponent(idForAdd,newCode,isFromDataBase,2);         //给二级中间料新增成分，需要将所新增的成分的component_mark置为null，这里将classMark设置为2就是为了区分这种情况
                break;
            default:
                return Result.ok(ReturnMessage.message("2","前端传给后端的数据model错误，请联系管理员！！！"));


        }


        return  Result.ok(message);
    }





}
















