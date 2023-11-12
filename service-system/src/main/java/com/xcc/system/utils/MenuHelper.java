package com.xcc.system.utils;

import com.xcc.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {

    //构建树形结构
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        //创建用于封装最终数据的集合
        List<SysMenu> trees = new ArrayList<>();
        //遍历所有菜单的集合
        for(SysMenu sysMenu:sysMenuList){
            //找到递归入口
            if(sysMenu.getParentId().longValue() == 0){
                trees.add(findChildren(sysMenu,sysMenuList));
            }
        }
        return trees;
    }

    //从根节点进行递归查询，查询子节点
    //判断 parentid == id 是否为true，如果是true，则说明 parentid 这条数据是 id 那条数据的子节点
    private static SysMenu findChildren(SysMenu sysMenu, List<SysMenu> treeNodes) {
        //数据初始化
        sysMenu.setChildren(new ArrayList<SysMenu>());

        //递归遍历查找

        for(SysMenu it:treeNodes){
/*            //获取当前菜单id
            Integer id = sysMenu.getId();
            //获取所有菜单parentid
            Integer parentId = it.getParentId().intValue();
            //比对*/
            if(sysMenu.getId() == it.getParentId().intValue()){
                if(sysMenu.getChildren() == null){
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(findChildren(it,treeNodes));
            }
        }
        return sysMenu;
    }
}
