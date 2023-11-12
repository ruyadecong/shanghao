package com.xcc.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.system.SysMenu;
import com.xcc.model.vo.AssignMenuVo;
import com.xcc.model.vo.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-05-17
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    void removeMenuById(Integer id);

    void updateStatus(Integer id, Integer status);

    List<SysMenu> findMenuByRoleId(Integer roleId);

    //给角色分配菜单权限
    void doAssign(AssignMenuVo vo);

    //根据userid查询菜单权限值
    List<RouterVo> getUserMenuList(Integer id);

    //根据userid查询按钮权限值
    List<String> getUserButtonList(Integer id);
}
