package com.xcc.system.controller;


import com.xcc.common.result.Result;
import com.xcc.model.system.SysMenu;
import com.xcc.model.vo.AssignMenuVo;
import com.xcc.system.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author xcc
 * @since 2023-05-17
 */

@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @ApiOperation("给角色分配菜单权限")
    @PostMapping("doAssign")
    public Result doAssign(@RequestBody AssignMenuVo vo){

        sysMenuService.doAssign(vo);

        return Result.ok();
    }

    @ApiOperation("根据角色获取菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Integer roleId){
        List<SysMenu> list = sysMenuService.findMenuByRoleId(roleId);
        return Result.ok(list);
    }

    @ApiOperation("菜单列表")
    @GetMapping("findNodes")
    public Result findNodes(){

        List<SysMenu> list = sysMenuService.findNodes();

        return Result.ok(list);
    }

    @ApiOperation("添加菜单")
    @PostMapping("save")
    public Result save(@RequestBody SysMenu sysMenu){

        boolean b = sysMenuService.save(sysMenu);

        if(b) {
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("根据id查询菜单")
    @GetMapping("findNode/{id}")
    public Result findNode(@PathVariable Integer id ){

        SysMenu sysMenu = sysMenuService.getById(id);

        return Result.ok(sysMenu);
    }


    @ApiOperation("修改菜单")
    @PostMapping("update")
    public Result update(@RequestBody SysMenu sysMenu){

        boolean b = sysMenuService.updateById(sysMenu);

        if(b) {
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Integer id) {

        sysMenuService.removeMenuById(id);

        return Result.ok();
    }

    @ApiOperation("状态转换")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Integer id,
                               @PathVariable Integer status){
        sysMenuService.updateStatus(id,status);

        return Result.ok();
    }
}

