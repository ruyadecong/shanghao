package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.common.utils.GenerateCreateTime;
import com.xcc.common.utils.ReturnMessage;
import com.xcc.model.order.OrderFormulaComponent;
import com.xcc.model.order.OrderProductFormula;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.technology.FormulaComponent;
import com.xcc.model.technology.FormulaData;
import com.xcc.system.mapper.OrderProductFormulaMapper;
import com.xcc.system.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单产品的配方表（隔离于产品的配方表） 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Service
public class OrderProductFormulaServiceImpl extends ServiceImpl<OrderProductFormulaMapper, OrderProductFormula> implements OrderProductFormulaService {

    @Autowired
    OrderProductFormulaService orderProductFormulaService;

    @Autowired
    OrderFormulaComponentService orderFormulaComponentService;

    @Autowired
    FormulaDataService formulaDataService;

    @Autowired
    FormulaComponentService formulaComponentService;

    @Autowired
    MaterialBasedataService materialBasedataService;

    //逻辑删除产品的某个配方
    @Override
    public boolean removeFormula(Integer id) {
        QueryWrapper<OrderFormulaComponent> wrapper = new QueryWrapper<>();
        wrapper.eq("order_formula_id",id);
        List<OrderFormulaComponent> list = orderFormulaComponentService.list(wrapper);
        boolean b = orderProductFormulaService.removeById(id);
        if(!b){
            return false;
        }else {
            if(list.size() > 0) {
                for (OrderFormulaComponent orderFormulaComponent : list) {
                    boolean b1 = orderFormulaComponentService.removeFormulaComponent(orderFormulaComponent.getId());
                    b = b & b1;
                }
            }
            return b;
        }
    }


    //给订单产品添加新配方
    @Override
    public Map<String, String> addNewFormula(Integer idForAdd, String newCode, boolean isFromDataBase) {
        if(isFromDataBase){
            FormulaData byCode = formulaDataService.getByCode(newCode);
            if(byCode == null){
                return ReturnMessage.message("0","未在数据库中找到配方：" + newCode);
            } else if (byCode.getClassMark().intValue() != 0) {
                return ReturnMessage.message("0","所输入的编码：" + newCode + "不是配方编码，不能添加！！！");
            } else if (byCode.getIsConfirm().intValue() != 1) {
                return ReturnMessage.message("0","配方："  + newCode + "仍在编辑或审核中，不可使用！！！");
            }else{
                //先保存配方的信息到order_product_formula中
                OrderProductFormula orderProductFormula = new OrderProductFormula();
                orderProductFormula.setOrderProductId(idForAdd);
                orderProductFormula.setFormulaCode(newCode);
                orderProductFormula.setClassMark(0);
                orderProductFormula.setRemark(byCode.getRemark());
                orderProductFormula.setCreateTime(GenerateCreateTime.generate());
                List<FormulaComponent> componentList = formulaComponentService.getListByCode(newCode);
                if(componentList.size() > 0) {
                    boolean save = orderProductFormulaService.save(orderProductFormula);
                    if (!save) {
                        return ReturnMessage.message("0", "新增配方失败，请联系管理员！！！");
                    }
                    //开始逐个保存配方的组分
                    for (FormulaComponent component : componentList) {
                        //如果component是原料，直接保存，如果是半成品，则调用OrderFormulaComponentService的addNewSemiFormula(Integer idForAdd, String newCode)方法来进行保存
                        String code = component.getMaterialCode();
                        MaterialBasedata byCode1 = materialBasedataService.getByCode(code);
                        if(byCode1 != null){
                            OrderFormulaComponent orderFormulaComponent = new OrderFormulaComponent();
                            orderFormulaComponent.setOrderFormulaId(orderProductFormula.getId());
                            orderFormulaComponent.setClassMark(0);
                            orderFormulaComponent.setOrderSemiId(0);
                            orderFormulaComponent.setRankNumber(component.getRankNumber());
                            orderFormulaComponent.setMaterialCode(code);
                            orderFormulaComponent.setMaterialContent(component.getMaterialContent());
                            orderFormulaComponent.setMaterialPurpose(component.getMaterialPurpose());
                            orderFormulaComponent.setRemark(component.getRemark());
                            orderFormulaComponent.setCreateTime(GenerateCreateTime.generate());
                            boolean save1 = orderFormulaComponentService.save(orderFormulaComponent);
                            if(!save1){
                                return ReturnMessage.message("0","配方组分保存失败！！！");
                            }
                        }else{
                            Map<String, String> message = orderFormulaComponentService.addNewSemiFormula(orderProductFormula.getId(), code);
                            if(!message.get("code").equals("1")){
                                return message;
                            }
                            //补充配方中的一级中间料的rankNumber和materialContent
                            //根据order_formula_id即orderProductFormula.getId()和newCode找到一级中间料在order_formula_component中的记录
                            QueryWrapper<OrderFormulaComponent> wrapper = new QueryWrapper<>();
                            wrapper.eq("order_formula_id",orderProductFormula.getId());
                            wrapper.eq("material_code",code);
                            wrapper.eq("is_deleted",0);
                            List<OrderFormulaComponent> list = orderFormulaComponentService.list(wrapper);
                            if(list.size() > 0){
                                for (OrderFormulaComponent orderFormulaComponent : list) {
                                    orderFormulaComponent.setMaterialContent(component.getMaterialContent());
                                    orderFormulaComponent.setRankNumber(component.getRankNumber());
                                    boolean b = orderFormulaComponentService.updateById(orderFormulaComponent);
                                    if(!b){
                                        return ReturnMessage.message("0",message.get("msg") + "中间料的含量和序号保存失败");
                                    }
                                }
                            }
                        }
                    }
                    return ReturnMessage.message("1","添加配方成功");
                }else{
                    orderProductFormula.setRemark("配方的组分保存失败！" + orderProductFormula.getRemark());
                }
            }
        }else{
            OrderProductFormula orderProductFormula = new OrderProductFormula();
            orderProductFormula.setOrderProductId(idForAdd);
            orderProductFormula.setFormulaCode(newCode);
            orderProductFormula.setClassMark(0);
            orderProductFormula.setCreateTime(GenerateCreateTime.generate());
            boolean save = orderProductFormulaService.save(orderProductFormula);
            if(save){
                return ReturnMessage.message("1","新增空白配方：" + newCode + "成功");
            }else{
                return ReturnMessage.message("0","新增空白配方失败");
            }
        }
        return null;
    }
}
