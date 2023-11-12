package com.xcc.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcc.model.system.SysMenu;
import com.xcc.model.system.SysRoleMenu;
import com.xcc.model.vo.AssignMenuVo;
import com.xcc.model.vo.RouterVo;
import com.xcc.system.exception.GuiguException;
import com.xcc.system.mapper.SysMenuMapper;
import com.xcc.system.mapper.SysRoleMenuMapper;
import com.xcc.system.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xcc.system.utils.MenuHelper;
import com.xcc.system.utils.RouterHelper;
import org.apache.commons.math3.ml.neuralnet.twod.util.QuantizationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-05-17
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Override
    public List<SysMenu> findNodes() {
        //获取所有菜单
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        //将所有菜单数据转换为element-ui要求的树形格式
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);

        return resultList;
    }

    @Override
    public void removeMenuById(Integer id) {
        //查询当前删除的菜单下面是否有子菜单
        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
        if(count>0){//有子菜单
            throw  new GuiguException(201,"请先删除子菜单");
        }
        //调用删除
        baseMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Integer id, Integer status) {

        SysMenu sysMenu = baseMapper.selectById(id);
        sysMenu.setStatus(status);
        baseMapper.updateById(sysMenu);

    }

    //根据角色获取菜单
    @Override
    public List<SysMenu> findMenuByRoleId(Integer roleId) {
        //获取所有status=1的菜单
        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("status",1);
        List<SysMenu> menuList = baseMapper.selectList(wrapper);

        //根据角色id查询角色分配过的菜单列表
        QueryWrapper<SysRoleMenu> wrapperRoleMenu = new QueryWrapper<>();
        wrapperRoleMenu.eq("role_id",roleId);
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(wrapperRoleMenu);

        //从第二步查询列表中，获取角色分配的所有菜单的id
        List<Integer> roleMenuIds = new ArrayList<>();
        for (SysRoleMenu roleMenu : roleMenus) {
            roleMenuIds.add(roleMenu.getMenuId());
        }

        //数据处理：isSelect     拿着分配的所有菜单的id和所有菜单的id比对，如果相同则说明菜单选中为true，反之为false
        for (SysMenu sysMenu : menuList) {
            if(roleMenuIds.contains(sysMenu.getId())){
                sysMenu.setSelect(true);
            }else{
                sysMenu.setSelect(false);
            }
        }

        //转换成树形结构用于最终的前端显示
        List<SysMenu> sysMenuTree = MenuHelper.buildTree(menuList);

        return sysMenuTree;
    }

    //给角色分配菜单权限
    @Override
    public void doAssign(AssignMenuVo vo) {
        //根据角色id删除已经存在的菜单权限
        QueryWrapper<SysRoleMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id",vo.getRoleId());
        sysRoleMenuMapper.delete(wrapper);

        //遍历菜单id列表，一个一个添加
        List<Integer> menuIdList = vo.getMenuIdList();
        for (Integer menuId : menuIdList) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(vo.getRoleId());
            sysRoleMenuMapper.insert(sysRoleMenu);
        }

    }

    //根据userid查询菜单权限值
    @Override
    public List<RouterVo> getUserMenuList(Integer userId) {
        List<SysMenu> sysMenuList = null;
        //username是admin即userid是1的话，则是超级管理员，能操作所有内容，直接查询所有权限数据即可
        if(userId == 1){
            QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
            wrapper.eq("status",1);
            wrapper.orderByAsc("sort_value");
            sysMenuList = baseMapper.selectList(wrapper);//这里是SysMenu的service，所以使用baseMapper直接就是调用SysMenu的对应mapper？？？
        }else{
            //不是admin即userid不是1的话.，则查询相应的权限
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        //构建树形结构
        List<SysMenu> sysMenuTree = MenuHelper.buildTree(sysMenuList);
        //转化成前端路由要求格式的数据
        List<RouterVo> routerVoList = RouterHelper.buildRouters(sysMenuTree);


        return routerVoList;
    }

    //根据userid查询按钮权限值
    @Override
    public List<String> getUserButtonList(Integer userId) {
        List<SysMenu> sysMenuList = null;
        //判断是否admin
        if(userId == 1){
            sysMenuList = baseMapper.selectList(new QueryWrapper<SysMenu>().eq("status", 1));
        }else{
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        //遍历sysMenuList，将其中 sysMenu.type = 2 的数据（2即代表button）放入一个新的集合中返回
        List<String> permissionList = new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            if(sysMenu.getType() == 2){
                String perms = sysMenu.getPerms();
                permissionList.add(perms);
            }
        }
        return permissionList;
    }
}
