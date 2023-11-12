package com.xcc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.purchase.MaterialPurchase;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.qc.CallInspect;
import com.xcc.model.vo.CallInspectVo;
import com.xcc.model.vo.MaterialPurchaseVo;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 原料采购信息 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-06-10
 */
public interface MaterialPurchaseService extends IService<MaterialPurchase> {

    //条件分页查询
    IPage<MaterialPurchase> selectPage(Page<MaterialPurchase> pageParam, MaterialPurchaseVo vo);

    //从material_purchase表中获取不重复的付款方式
    List<String> payMethodList();

    //根据id更新备注
    boolean updateRemarkById(Integer id, String remark);

    //根据原料编码mc查询指定条目数limit的数据
    List<MaterialPurchase> getByCode(String mc, int limit);


    //获取采购数据用于报检
    List<MaterialPurchase> getListForCallInspect();

    //处理前端传回的报检id列表
    boolean callInspect(CallInspectVo vo);

    //根据入库日期生成入库单号
    String getEntryNumber(Date inDate,String supplierCode);
}
