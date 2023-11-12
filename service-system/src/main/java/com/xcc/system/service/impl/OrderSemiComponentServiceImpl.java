package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.common.utils.GenerateCreateTime;
import com.xcc.common.utils.ReturnMessage;
import com.xcc.model.order.OrderSemiComponent;
import com.xcc.model.order.OrderSemiFormula;
import com.xcc.model.qc.MaterialBasedata;
import com.xcc.model.technology.FormulaComponent;
import com.xcc.model.technology.FormulaData;
import com.xcc.system.mapper.OrderSemiComponentMapper;
import com.xcc.system.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.Get;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单的配方表（隔离于配方表） 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-20
 */
@Service
public class OrderSemiComponentServiceImpl extends ServiceImpl<OrderSemiComponentMapper, OrderSemiComponent> implements OrderSemiComponentService {

    @Autowired
    OrderSemiComponentService orderSemiComponentService;

    @Autowired
    OrderSemiFormulaService orderSemiFormulaService;

    @Autowired
    MaterialBasedataService materialBasedataService;

    @Autowired
    FormulaDataService formulaDataService;

    @Autowired
    FormulaComponentService formulaComponentService;

    @Override
    public boolean removeSemiComponent(Integer id) {
        OrderSemiComponent byId = orderSemiComponentService.getById(id);
        boolean b;
        b = true;
        if(byId.getClassMark() == 0){
            b = orderSemiComponentService.removeById(id);
            return b;
        }else{
            Integer orderSemiId = byId.getComponentMark();
            boolean b1 = orderSemiComponentService.removeById(id);
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
                    boolean b3 = orderSemiComponentService.removeById(orderSemiComponent.getId());
                    b = b & b3;
                }
            }
            return b;
        }

    }

    //给订单产品的中间料添加新组分
    @Override
    public Map<String, String> addNewSemiComponent(Integer idForAdd, String newCode, boolean isFromDataBase, int classMark) {
        if(isFromDataBase){     //从数据库中添加，先要判断传回的newCode是否在数据库中
            //1 先处理classMark == 0 的情况，即newCode是materialCode
            if(classMark == 0){
                MaterialBasedata byCode = materialBasedataService.getByCode(newCode);
                if(byCode == null){
                    return ReturnMessage.message("0","未在数据库中找到原料：" + newCode);
                } else if (byCode.getIsConfirm().intValue() !=1 ) {
                    return ReturnMessage.message("0","原料：" + newCode + "仍在编辑或审核中，不可使用！！！");
                } else{
                    OrderSemiComponent orderSemiComponent = new OrderSemiComponent();
                    orderSemiComponent.setOrderSemiId(idForAdd);
                    orderSemiComponent.setClassMark(0);
                    orderSemiComponent.setComponentMark(0);
                    orderSemiComponent.setMaterialCode(newCode);
                    orderSemiComponent.setCreateTime(GenerateCreateTime.generate());
                    boolean save = orderSemiComponentService.save(orderSemiComponent);
                    if(save){
                        return ReturnMessage.message("1","给中间料新增原料：" + newCode + "成功");
                    }else{
                        return ReturnMessage.message("0","新增原料失败");
                    }
                }
            } else if (classMark == 2) {
                MaterialBasedata byCode = materialBasedataService.getByCode(newCode);
                if(byCode == null){
                    return ReturnMessage.message("0","未在数据库中找到原料：" + newCode);
                } else if (byCode.getIsConfirm().intValue() !=1 ) {
                    return ReturnMessage.message("0","原料：" + newCode + "仍在编辑或审核中，不可使用！！！");
                }
                else{
                    OrderSemiComponent orderSemiComponent = new OrderSemiComponent();
                    orderSemiComponent.setOrderSemiId(idForAdd);
                    orderSemiComponent.setClassMark(0);
                    //classMark 为0 和2 之间的区别就是在下面注释掉的一句
                    //orderSemiComponent.setComponentMark(0);
                    orderSemiComponent.setMaterialCode(newCode);
                    orderSemiComponent.setCreateTime(GenerateCreateTime.generate());
                    boolean save = orderSemiComponentService.save(orderSemiComponent);
                    if(save){
                        return ReturnMessage.message("1","给中间料新增原料：" + newCode + "成功");
                    }else{
                        return ReturnMessage.message("0","新增原料失败");
                    }
                }
            } else if (classMark == 1) {
                FormulaData byCode = formulaDataService.getByCode(newCode);
                if(byCode == null){
                    return ReturnMessage.message("0","未在数据库中找到中间料：" + newCode);
                } else if (byCode.getClassMark().intValue() != 1) {
                    return ReturnMessage.message("2","所录编码：" + newCode + " 为配方编码，不能作为中间料");
                }  else if (byCode.getIsConfirm().intValue() != 1) {
                    return ReturnMessage.message("0","中间料：" + newCode + "仍在编辑或审核中，不可使用！！！");
                }else {
                    //到这里，虽然byCode是中间料，但是这个作为一级中间料组分的中间料是不能含有二级中间料的
                    //因此需要对其组分做一个遍历，查看是否含有中间料
                    List<FormulaComponent> listByCode = formulaComponentService.getListByCode(newCode);
                    boolean isSemiSemi = false;
                    for (FormulaComponent component : listByCode) {
                        String materialCode = component.getMaterialCode();
                        MaterialBasedata byCode1 = materialBasedataService.getByCode(materialCode);
                        if(byCode1 == null){
                            isSemiSemi = true;
                            break;
                        }
                    }
                    if(isSemiSemi){
                        return ReturnMessage.message("2","中间料：" + newCode + "含有中间料，不能作为二级中间料！！！");
                    }else {
                        Map<String, String> message = addNewSemiSemiComponent(idForAdd, newCode);
                        return message;
                    }
                }
            }else{
                    return ReturnMessage.message("2","前端传给后端的selectAddSource错误，请联系管理员！！！");
            }
        }else{
            //保存空白的不是从数据库中获取的组分数据
            if(classMark == 0){         //给一级中间料添加原料
                OrderSemiComponent orderSemiComponent = new OrderSemiComponent();
                orderSemiComponent.setOrderSemiId(idForAdd);
                orderSemiComponent.setClassMark(0);
                orderSemiComponent.setComponentMark(0);
                orderSemiComponent.setMaterialCode(newCode);
                orderSemiComponent.setRemark("空白原料");
                orderSemiComponent.setCreateTime(GenerateCreateTime.generate());
                boolean save = orderSemiComponentService.save(orderSemiComponent);
                if(save){
                    return ReturnMessage.message("1","给中间料新增空白原料：" + newCode + "成功");
                }else{
                    return ReturnMessage.message("0","新增空白原料失败");
                }
            } else if (classMark == 2) {        //给二级中间料添加原料
                OrderSemiComponent orderSemiComponent = new OrderSemiComponent();
                orderSemiComponent.setOrderSemiId(idForAdd);
                orderSemiComponent.setClassMark(0);
                //classMark 为0 和2 之间的区别就是在下面注释掉的一句
                //orderSemiComponent.setComponentMark(0);
                orderSemiComponent.setMaterialCode(newCode);
                orderSemiComponent.setCreateTime(GenerateCreateTime.generate());
                orderSemiComponent.setRemark("空白原料");
                boolean save = orderSemiComponentService.save(orderSemiComponent);
                if(save){
                    return ReturnMessage.message("1","给中间料新增空白原料：" + newCode + "成功");
                }else{
                    return ReturnMessage.message("0","新增空白原料失败");
                }

            } else if (classMark == 1) {                //给一级中间料添加二级中间料
                OrderSemiFormula orderSemiFormula = new OrderSemiFormula();
                OrderSemiComponent orderSemiComponent = new OrderSemiComponent();
                orderSemiFormula.setOrderProductId(idForAdd);
                orderSemiFormula.setSemiCode(newCode);
                orderSemiFormula.setClassMark(1);
                orderSemiFormula.setCreateTime(GenerateCreateTime.generate());
                boolean save = orderSemiFormulaService.save(orderSemiFormula);
                if(!save){
                    return ReturnMessage.message("0","二级中间料的formula信息保存失败");
                }
                orderSemiComponent.setOrderSemiId(idForAdd);
                orderSemiComponent.setClassMark(1);
                orderSemiComponent.setComponentMark(orderSemiFormula.getId());
                orderSemiComponent.setMaterialCode(newCode);
                orderSemiComponent.setMaterialPurpose("");
                orderSemiComponent.setRemark("空白二级中间料");
                orderSemiComponent.setCreateTime(GenerateCreateTime.generate());
                boolean save1 = orderSemiComponentService.save(orderSemiComponent);
                if(save1){
                    return ReturnMessage.message("1","给中间料新增空白二级中间料：" + newCode + "成功");
                }else{
                    return ReturnMessage.message("0","新增空白二级中间料失败");
                }
            } else{
                return ReturnMessage.message("2","前端传给后端的selectAddSource错误，请联系管理员！！！");
            }
        }
    }

    //从数据库中获取数据用于新增二级中间料的代码
    @Override
    public Map<String, String> addNewSemiSemiComponent(Integer idForAdd, String newCode){
        OrderSemiFormula forAdd = orderSemiFormulaService.getById(idForAdd);
        List<FormulaComponent> semiSemiComponent = formulaComponentService.getListByCode(newCode);
        OrderSemiFormula orderSemiFormula = new OrderSemiFormula();
        //二级中间料所属的一级中间料（由iDForAdd取得）的order_product_id就是一级中间料的order_product_id
        orderSemiFormula.setOrderProductId(forAdd.getOrderProductId());
        orderSemiFormula.setSemiCode(newCode);
        orderSemiFormula.setClassMark(1);
        orderSemiFormula.setRemark(forAdd.getRemark());
        orderSemiFormula.setCreateTime(GenerateCreateTime.generate());

        if(semiSemiComponent.size() > 0) {
            boolean save = orderSemiFormulaService.save(orderSemiFormula);
            if(!save){
                return ReturnMessage.message("0","二级中间料的formula信息保存失败");
            }
            //二级中间料的formula信息保存成功之后，开始逐个保存其组分信息
            for (FormulaComponent component : semiSemiComponent) {
                OrderSemiComponent orderSemiSemiComponent = new OrderSemiComponent();
                orderSemiSemiComponent.setOrderSemiId(orderSemiFormula.getId());
                orderSemiSemiComponent.setClassMark(0);
                //orderSemiComponent.setComponentMark(null);
                orderSemiSemiComponent.setRankNumber(component.getRankNumber());
                orderSemiSemiComponent.setMaterialCode(component.getMaterialCode());
                orderSemiSemiComponent.setMaterialContent(component.getMaterialContent());
                orderSemiSemiComponent.setMaterialPurpose(component.getMaterialPurpose());
                orderSemiSemiComponent.setRemark(component.getRemark());
                orderSemiSemiComponent.setCreateTime(GenerateCreateTime.generate());
                boolean save1 = orderSemiComponentService.save(orderSemiSemiComponent);
                if(!save1){
                    return ReturnMessage.message("0","二级中间料的component信息保存失败");
                }
            }
            //如果能出循环，并且到这里，说明二级中间料的component全部保存成功
            //下一步要将二级中间料本身作为component保存到order_semi_component中
            OrderSemiComponent orderSemiComponent = new OrderSemiComponent();
            orderSemiComponent.setOrderSemiId(idForAdd);
            orderSemiComponent.setClassMark(1);
            orderSemiComponent.setComponentMark(orderSemiFormula.getId());
            orderSemiComponent.setMaterialCode(newCode);
            orderSemiComponent.setMaterialPurpose("");
            //orderSemiComponent.setRemark(orderSemiFormula.getRemark());
            orderSemiComponent.setCreateTime(GenerateCreateTime.generate());
            boolean save1 = orderSemiComponentService.save(orderSemiComponent);
            if(!save1){
                return ReturnMessage.message("0","二级中间料作为component信息保存失败");
            }
            return ReturnMessage.message("1","二级中间料保存成功");
        }else{
            //如果数据库中拿到的二级中间料的组分个数为0，则需要在二级中间料的formula的remark上加上下面的备注
            orderSemiFormula.setRemark("中间料的组分保存失败" + orderSemiFormula.getRemark());
            boolean save = orderSemiFormulaService.save(orderSemiFormula);
            if(!save){
                return ReturnMessage.message("0","二级中间料的formula信息保存失败");
            }else {
                return ReturnMessage.message("1","二级中间料保存成功，但是缺少其组分数据");
            }
        }

    }
}
