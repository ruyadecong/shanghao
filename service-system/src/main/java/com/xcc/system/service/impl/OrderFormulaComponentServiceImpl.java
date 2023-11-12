package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.QueryResult;
import com.xcc.common.utils.GenerateCreateTime;
import com.xcc.common.utils.ReturnMessage;
import com.xcc.model.order.OrderFormulaComponent;
import com.xcc.model.order.OrderProductFormula;
import com.xcc.model.order.OrderSemiComponent;
import com.xcc.model.order.OrderSemiFormula;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.technology.FormulaComponent;
import com.xcc.model.technology.FormulaData;
import com.xcc.system.mapper.OrderFormulaComponentMapper;
import com.xcc.system.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单的配方表（隔离于配方表） 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Service
public class OrderFormulaComponentServiceImpl extends ServiceImpl<OrderFormulaComponentMapper, OrderFormulaComponent> implements OrderFormulaComponentService {

    @Autowired
    OrderFormulaComponentService orderFormulaComponentService;

    @Autowired
    OrderSemiFormulaService orderSemiFormulaService;

    @Autowired
    OrderSemiComponentService orderSemiComponentService;

    @Autowired
    MaterialBasedataService materialBasedataService;

    @Autowired
    FormulaDataService formulaDataService;

    @Autowired
    FormulaComponentService formulaComponentService;

    @Autowired
    OrderProductFormulaService orderProductFormulaService;

    @Override
    public boolean removeFormulaComponent(Integer id) {
        OrderFormulaComponent byId = orderFormulaComponentService.getById(id);
        boolean b;
        b = true;
        if(byId.getClassMark() == 0){
            b = orderFormulaComponentService.removeById(id);
            return b;
        }else{
            Integer orderSemiId = byId.getOrderSemiId();
            boolean b1 = orderFormulaComponentService.removeById(id);
            if(!b1){
                return false;
            }
            boolean b2 = orderSemiFormulaService.removeById(orderSemiId);
            if(!b2){
                return false;
            }
            QueryWrapper<OrderSemiComponent> wrapper = new QueryWrapper<>();
            wrapper.eq("order_semi_id",orderSemiId);
            List<OrderSemiComponent> list = orderSemiComponentService.list(wrapper);
            if(list.size() > 0) {
                for (OrderSemiComponent orderSemiComponent : list) {
                    boolean b3 = orderSemiComponentService.removeSemiComponent(orderSemiComponent.getId());
                    b = b & b3;
                }
            }
            return b;
        }

    }

    //给订单产品添加新组分
    @Override
    public Map<String, String> addNewFormulaComponent(Integer idForAdd, String newCode, boolean isFromDataBase, int classMark) {
        //先根据isFromDataBase来判断是否从数据库中取数据
        if(isFromDataBase){
            //添加数据库中取得的配方组分
            if(classMark == 0){         //原料
                //先判断原料数据库中是否有数据
                MaterialBasedata byCode = materialBasedataService.getByCode(newCode);
                if(byCode == null){
                    return ReturnMessage.message("0","未在数据库中找到原料：" + newCode);
                } else if (byCode.getIsConfirm().intValue() != 1) {
                    return ReturnMessage.message("0","原料：" + newCode + "仍在编辑或审核中，不可使用！！！");
                } else{
                    OrderFormulaComponent orderFormulaComponent = new OrderFormulaComponent();
                    orderFormulaComponent.setOrderFormulaId(idForAdd);
                    orderFormulaComponent.setClassMark(0);
                    orderFormulaComponent.setOrderSemiId(0);
                    orderFormulaComponent.setMaterialCode(newCode);
                    orderFormulaComponent.setCreateTime(GenerateCreateTime.generate());
                    boolean save = orderFormulaComponentService.save(orderFormulaComponent);
                    if(save){
                        return ReturnMessage.message("1","给配方新增原料：" + newCode + "成功");
                    }else{
                        return ReturnMessage.message("0","新增原料失败");
                    }
                }
            } else if (classMark == 1) {  //中间料（一级）
                //先判断配方数据库中是否有数据
                FormulaData byCode = formulaDataService.getByCode(newCode);
                if(byCode == null){
                    return ReturnMessage.message("0","未在数据库中找到中间料：" + newCode);
                } else if (byCode.getClassMark().intValue() != 1) {
                    return ReturnMessage.message("2","所录编码：" + newCode + " 为配方编码，不能作为中间料");
                }  else if (byCode.getIsConfirm().intValue() != 1) {
                    return ReturnMessage.message("0","中间料：" + newCode + "仍在编辑或审核中，不可使用！！！");
                }else{
                    Map<String, String> message = addNewSemiFormula(idForAdd, newCode);
                    return message;

                }

            }else{
                return ReturnMessage.message("2","前端传给后端的selectAddSource错误，请联系管理员！！！");
            }
        }else{
            //添加空白的不是从数据库中取得的配方组分
            if(classMark == 0){         //原料
                OrderFormulaComponent orderFormulaComponent = new OrderFormulaComponent();
                orderFormulaComponent.setOrderFormulaId(idForAdd);
                orderFormulaComponent.setClassMark(0);
                orderFormulaComponent.setOrderSemiId(0);
                orderFormulaComponent.setMaterialCode(newCode);
                orderFormulaComponent.setRemark("空白原料");
                orderFormulaComponent.setCreateTime(GenerateCreateTime.generate());
                boolean save = orderFormulaComponentService.save(orderFormulaComponent);
                if(save){
                    return ReturnMessage.message("1","给配方新增空白原料：" + newCode + "成功");
                }else{
                    return ReturnMessage.message("0","新增空白原料失败");
                }
            } else if (classMark == 1) {  //中间料（一级）
                //新增中间料先要在order_semi_formula和中order_semi_component添加中间料的数据，然后再在order_formula_component中添加作为配方组分的记录
                OrderSemiFormula orderSemiFormula = new OrderSemiFormula();
                OrderSemiComponent orderSemiComponent = new OrderSemiComponent();
                OrderFormulaComponent orderFormulaComponent = new OrderFormulaComponent();
                //先要根据iDForAdd从order_product_formula中获取order_product_id
                OrderProductFormula orderFormula = orderProductFormulaService.getById(idForAdd);
                orderSemiFormula.setOrderProductId(orderFormula.getOrderProductId());
                orderSemiFormula.setSemiCode(newCode);
                orderSemiFormula.setClassMark(1);
                orderSemiFormula.setCreateTime(GenerateCreateTime.generate());
                orderSemiFormula.setRemark("空白中间料");
                boolean save = orderSemiFormulaService.save(orderSemiFormula);
                if(!save){
                    return ReturnMessage.message("0","中间料数据保存失败");
                }
                //由于是空白中间料，所以order_semi_component不用保存
                orderFormulaComponent.setOrderFormulaId(idForAdd);
                orderFormulaComponent.setClassMark(1);
                orderFormulaComponent.setOrderSemiId(orderSemiFormula.getId());
                orderFormulaComponent.setMaterialCode(newCode);
                orderFormulaComponent.setRemark("空白中间料");
                orderFormulaComponent.setCreateTime(GenerateCreateTime.generate());
                boolean save1 = orderFormulaComponentService.save(orderFormulaComponent);
                if(save1){
                    return ReturnMessage.message("1","给配方新增空白中间料：" + newCode + "成功");
                }else{
                    return ReturnMessage.message("0","新增空白中间料失败");
                }
            }else{
                return ReturnMessage.message("2","前端传给后端的selectAddSource错误，请联系管理员！！！");
            }
        }

    }


    //给配方新增数据库中已有的中间料作为组分
    //这个方法是用于在order_formula_component中添加数据库中的中间料组分用的
    //其中idForAdd是中间料组分所属的配方的id
    //newCode则是所需要添加的中间料的编码
    //在调用这个方法之前，先要判断newCode是否在formula_data中，如果不在，则不能调用此方法
    //所以此方法中无需再判断newCode是否在formula_data中，但是对根据newCode提取出的formulaComponentList数据需要判断是否为空
    @Override
    public Map<String, String> addNewSemiFormula(Integer idForAdd, String newCode){
        FormulaData formulaData = formulaDataService.getByCode(newCode);
        List<FormulaComponent> componentList = formulaComponentService.getListByCode(newCode);
        OrderProductFormula orderProductFormula = orderProductFormulaService.getById(idForAdd);         //用于获取配方所属的product的id
        OrderFormulaComponent orderFormulaComponent = new OrderFormulaComponent();
        OrderSemiFormula orderSemiFormula = new OrderSemiFormula();
        //1 要先存入order_semi_component，这样才能获得order_semi_id，这里要用到根据idForAdd所取得的配方所属的product的id
        orderSemiFormula.setOrderProductId(orderProductFormula.getOrderProductId());
        orderSemiFormula.setSemiCode(newCode);
        orderSemiFormula.setClassMark(1);
        orderSemiFormula.setCreateTime(GenerateCreateTime.generate());
        boolean save = orderSemiFormulaService.save(orderSemiFormula);
        if(!save){
            return ReturnMessage.message("0","一级中间料数据存入order_semi_formula失败！！！");
        }
        //2 将newCode存入order_formula_component，这里要用到第1步保存semi_formula所得到的semi_formula的id
        Integer orderSemiId = orderSemiFormula.getId();
        orderFormulaComponent.setOrderFormulaId(idForAdd);
        orderFormulaComponent.setClassMark(1);
        orderFormulaComponent.setOrderSemiId(orderSemiId);
        orderFormulaComponent.setMaterialCode(newCode);
        orderFormulaComponent.setRemark(formulaData.getRemark());
        orderFormulaComponent.setCreateTime(GenerateCreateTime.generate());
        if(componentList.size() > 0) {
            boolean save1 = orderFormulaComponentService.save(orderFormulaComponent);
            if (!save1) {
                return ReturnMessage.message("0", "一级中间料数据存入order_formula_component失败！！！");
            }
            //3 将newCode存入order_formula_component和order_semi_component成功之后，要将从formula_component获得的中间料组分存入order_semi_component中
            //这一步是难点，因为逐个保存component时可能会遇到二级中间料
            //二级中间料需要使用OrderSemiComponentService中的addNewSemiSemiComponent方法来存入
            for (FormulaComponent component : componentList) {
                String code = component.getMaterialCode();
                MaterialBasedata byCode = materialBasedataService.getByCode(code);
                if(byCode != null){         //byCode != null，说明这个是原料，不是二级中间料
                    OrderSemiComponent orderSemiComponent = new OrderSemiComponent();
                    orderSemiComponent.setOrderSemiId(orderSemiId);
                    orderSemiComponent.setClassMark(0);
                    orderSemiComponent.setComponentMark(0);
                    orderSemiComponent.setRankNumber(component.getRankNumber());
                    orderSemiComponent.setMaterialCode(code);
                    orderSemiComponent.setMaterialContent(component.getMaterialContent());
                    orderSemiComponent.setMaterialPurpose(component.getMaterialPurpose());
                    orderSemiComponent.setRemark(component.getRemark());
                    orderSemiComponent.setCreateTime(GenerateCreateTime.generate());
                    boolean save2 = orderSemiComponentService.save(orderSemiComponent);
                    if(!save2){
                        return ReturnMessage.message("0","一级中间料的原料组分存入order_semi_component中失败！！！");
                    }
                }else{         //byCode == null，说明这个不是原料，是二级中间料
                    //调用OrderSemiComponentService中的addNewSemiSemiComponent(Integer idForAdd, String newCode)方法来存入
                    //这里重点在于所传递的参数
                    //idForAdd应该是二级中间料所属的一级中间料的id，即保存orderSemiFormula后所生成的id
                    //newCode则是二级中间料的编码，即遍历获得的component的material_code
                    Map<String, String> message = orderSemiComponentService.addNewSemiSemiComponent(orderSemiFormula.getId(), code);
                    //添加完二级中间料之后，还需要在order_semi_component里面添加其含量，即二级中间料在一级中间料中的含量
                    //这个含量的数据在component中
                    //还需要给二级中间料加上rank_number
                    if(!message.get("code").equals("1")){         //如果message中的code != 1，说明二级中间料的保存出现问题，退出程序
                        return message;
                    }else{
                        //先根据order_semi_id和material_code从order_semi_component中二级中间料本身的记录
                        QueryWrapper<OrderSemiComponent> wrapper = new QueryWrapper<>();
                        wrapper.eq("order_semi_id",orderSemiFormula.getId());
                        wrapper.eq("material_code",code);
                        wrapper.eq("is_deleted",0);
                        List<OrderSemiComponent> list = orderSemiComponentService.list(wrapper);
                        if(list.size() > 0){
                            for (OrderSemiComponent orderSemiComponent : list) {
                                orderSemiComponent.setMaterialContent(component.getMaterialContent());
                                orderSemiComponent.setRankNumber(component.getRankNumber());
                                boolean b = orderSemiComponentService.updateById(orderSemiComponent);
                                if(!b){
                                    return ReturnMessage.message("0",message.get("msg") + "二级中间料的含量和序号保存失败");
                                }
                            }
                        }
                    }
                }
            }
            return ReturnMessage.message("1","新增中间料：" + newCode + "成功");

        }else{      //如果中间料的组分未取到，需要在备注中说明
            orderFormulaComponent.setRemark("中间料的组分保存失败；" + orderFormulaComponent.getRemark());
            boolean save1 = orderFormulaComponentService.save(orderFormulaComponent);
            if (!save1) {
                return ReturnMessage.message("0", "一级中间料数据存入order_formula_component失败！！！");
            }else{
                return ReturnMessage.message("1", "一级中间料数据存入order_formula_component成功，但中间料的组分数据保存失败！！！");
            }
        }
    }
}
