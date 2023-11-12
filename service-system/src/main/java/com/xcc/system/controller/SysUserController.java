package com.xcc.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.common.utils.MD5;
import com.xcc.model.system.SysUser;
import com.xcc.model.vo.SysUserQueryVo;
import com.xcc.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author xcc
 * @since 2023-05-12
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("用户条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result findPageQuerySysUser(@PathVariable Integer page,
                                       @PathVariable Integer limit,
                                       SysUserQueryVo vo){

        IPage<SysUser> pageParam = new Page<>(page,limit);
        IPage<SysUser> pageModel = sysUserService.selectPage(pageParam,vo);

        return Result.ok(pageModel);
    }

    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser sysUser){

        //把输入的密码用MD5进行加密
        String encrypt = MD5.encrypt(sysUser.getPassword());
        sysUser.setPassword(encrypt);

        boolean b = sysUserService.save(sysUser);

        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("根据id查询")
    @GetMapping("getUser/{id}")
    public Result getUser(@PathVariable Integer id){

        SysUser sysUser = sysUserService.getById(id);

        return Result.ok(sysUser);
    }

    @ApiOperation("修改用户")
    @PostMapping("update")
    public Result update(@RequestBody SysUser sysUser){

        boolean b = sysUserService.updateById(sysUser);

        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }


    @ApiOperation("删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Integer id){

        boolean b = sysUserService.removeById(id);

        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("更改用户状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Integer id,
                               @PathVariable Integer status){

/*        SysUser sysUser = sysUserService.getById(id);

        if(status == 1){
            sysUser.setStatus(0);
        }else if(status == 0){
            sysUser.setStatus(1);
        }
        boolean b = sysUserService.updateById(sysUser);

        if (b){
            return Result.ok();
        }else{
            return Result.fail();
        }*/

        sysUserService.updateStatus(id,status);

        return Result.ok();
    }

}

