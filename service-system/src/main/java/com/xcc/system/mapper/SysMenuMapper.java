package com.xcc.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.system.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-05-17
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    //根据userid查询其有权限操作的菜单的list
    List<SysMenu> findMenuListByUserId(@Param("userId") Integer userId);
}
