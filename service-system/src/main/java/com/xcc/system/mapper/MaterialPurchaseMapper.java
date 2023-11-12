package com.xcc.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.purchase.MaterialPurchase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.vo.MaterialPurchaseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 原料采购信息 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-06-10
 */
@Repository
public interface MaterialPurchaseMapper extends BaseMapper<MaterialPurchase> {

    //条件分页查询
    IPage<MaterialPurchase> selectPage(Page<MaterialPurchase> pageParam, @Param("vo") MaterialPurchaseVo vo);

    //从material_purchase表中获取不重复的付款方式
    List<String> getPayMethodList();

    //根据id更新备注
    void updateRemarkById(@Param("id") Integer id,@Param("remark") String remark);

    //根据原料编码mc查询指定条目数limit的数据
    List<MaterialPurchase> getByCode(@Param("mc") String mc,@Param("limit") int limit);
}
