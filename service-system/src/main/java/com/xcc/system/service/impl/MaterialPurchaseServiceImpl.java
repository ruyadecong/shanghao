package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.purchase.MaterialPurchase;
import com.xcc.model.qc.CallInspect;
import com.xcc.model.vo.CallInspectVo;
import com.xcc.model.vo.MaterialPurchaseVo;
import com.xcc.system.mapper.CallInspectMapper;
import com.xcc.system.mapper.MaterialPurchaseMapper;
import com.xcc.system.service.MaterialPurchaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 原料采购信息 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-06-10
 */
@Service
public class MaterialPurchaseServiceImpl extends ServiceImpl<MaterialPurchaseMapper, MaterialPurchase> implements MaterialPurchaseService {
    @Autowired
    MaterialPurchaseMapper materialPurchaseMapper;

    @Autowired
    CallInspectMapper callInspectMapper;

    //条件分页查询
    @Override
    public IPage<MaterialPurchase> selectPage(Page<MaterialPurchase> pageParam, MaterialPurchaseVo vo) {
        
        IPage<MaterialPurchase> list = baseMapper.selectPage(pageParam, vo);

        return list;
    }

    //从material_purchase表中获取不重复的付款方式
    @Override
    public List<String> payMethodList() {

        List<String> list = materialPurchaseMapper.getPayMethodList();
        return list;
    }


    //根据id更新备注
    @Override
    public boolean updateRemarkById(Integer id, String remark) {

        boolean b = true;

        materialPurchaseMapper.updateRemarkById(id,remark);

        return b;
    }

    //根据原料编码mc查询指定条目数limit的数据
    @Override
    public List<MaterialPurchase> getByCode(String mc, int limit) {
        List<MaterialPurchase> list = materialPurchaseMapper.getByCode(mc,limit);
        return list;
    }


    //获取采购数据用于报检
    @Override
    public List<MaterialPurchase> getListForCallInspect() {
        QueryWrapper<MaterialPurchase> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted",0);
        wrapper.eq("is_call_inspect",0);
        wrapper.isNotNull("in_date");
        wrapper.orderByAsc("in_date");
        List<MaterialPurchase> list = baseMapper.selectList(wrapper);

        return list;
    }

    //处理前端传回的报检id列表
    @Override
    public boolean callInspect(CallInspectVo vo) {
        boolean b;
        b=true;
        //生成当天的日期字符串，作为报检序列号
        Date date = vo.getDate();
        DateFormat af = new SimpleDateFormat("yyyyMMdd");
        String serialNumber = af.format(date);
        QueryWrapper<CallInspect> wrapper = new QueryWrapper<>();
        //获取和当天serialNumber相似的数据库中的数据
        wrapper.like("serial_number",serialNumber);
        List<CallInspect> callInspects = callInspectMapper.selectList(wrapper);
        //判断获得的数据记录数是否为0，如果为0，则加后缀01
        if(callInspects.size() == 0 || callInspects == null){
            serialNumber = serialNumber + "01";
        }else{      //如果有记录，则要获取当前已经存在的相似的serialNumber中的最大值，然后再 + 1
            List<Integer> serialNumberList = new ArrayList<>();
            for (CallInspect callInspect : callInspects) {
                serialNumberList.add(Integer.parseInt(callInspect.getSerialNumber()));
            }
            Integer max = Collections.max(serialNumberList);
            max = max + 1;
            serialNumber = Integer.toString(max);
        }
        List<Integer> idList = vo.getIdList();

        String remark = vo.getRemark();
        if(idList.size()>0 && idList != null){
            for (Integer id : idList) {
                CallInspect inspect = new CallInspect();
                inspect.setInspectDate(date);
                inspect.setPurchaseId(id);
                inspect.setSerialNumber(serialNumber);
                inspect.setRemark(remark);
                inspect.setCreateTime(new Date(System.currentTimeMillis()));

                int insert = callInspectMapper.insert(inspect);
                b = b && (insert>0?true:false);
            }

        }
        return b;
    }


    //根据入库日期生成入库单号
    @Override
    public String getEntryNumber(Date inDate, String supplierCode) {
        String dateString;
        dateString="";
        if(inDate != null){
            DateFormat af = new SimpleDateFormat("yyyyMMdd");
            dateString = af.format(inDate);                                  //根据入库日期生成日期序列号
            QueryWrapper<MaterialPurchase> wrapper = new QueryWrapper<>();
            wrapper.like("entry_number",dateString);
            List<MaterialPurchase> list = baseMapper.selectList(wrapper);     //获得数据库中有相同日期序列号（入库单号的前8位）的记录
            if(list.size() == 0){
                dateString = dateString + "01";                                         //如果记录条数为0，说明这个日期下还没有入库单号，此时入库单号从01开始
            }else{                                                                      //如果记录条数不为0，则要进行供应商对比
                boolean find;
                find = false;
                for (MaterialPurchase purchase : list) {                                //遍历记录中的供应商代码，与要保存的信息的供应商代码进行比较，如果相同，则直接获取相同供应商代码的入库单号，并赋值给要保存的信息的入库单号
                    if(supplierCode.equals(purchase.getSupplierCode())){
                        QueryWrapper<MaterialPurchase> wrapper1 = new QueryWrapper<>();
                        wrapper1.eq("entry_number",purchase.getEntryNumber());
                        Integer count = materialPurchaseMapper.selectCount(wrapper);    //
                        if ( count < 20 ){
                            dateString =purchase.getEntryNumber();
                            find = true;
                        }else{
                            find = false;
                        }
                                                                          //find用于标记记录中有相同日期序列号和供应商编码的记录
                    }
                }
                if(!find){                                                              //如果相同日期序列号的记录中没有发现相同的供应商编码，则要赋予一个新的（递增的）入库单号
                    List<Integer> listOfNo = new ArrayList<>();
                    for (MaterialPurchase purchase : list) {
                        String entryNumber = purchase.getEntryNumber();
                        listOfNo.add(Integer.parseInt(entryNumber));
                    }
                    Integer max = Collections.max(listOfNo);
                    max = max + 1;
                    dateString = Integer.toString(max);
                }
            }
        }

        if(dateString != ""){
            return dateString;
        }else{
            return null;
        }
    }


}
