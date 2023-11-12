package com.xcc.system.controller;

import com.xcc.common.result.Result;
import com.xcc.common.utils.JwtHelper;
import com.xcc.common.utils.MD5;
import com.xcc.model.system.SysUser;
import com.xcc.model.vo.LoginVo;
import com.xcc.system.exception.GuiguException;
import com.xcc.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(tags ="用户登录接口" )
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    //login
    @ApiOperation("用户登录接口")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo vo){
        //根据username查询数据库
        SysUser sysUser = sysUserService.getUserInfoByUserName(vo.getUsername());

        //如果查询为空
        if(sysUser==null){
            throw new GuiguException(20001,"用户不存在");
        }

        //如果查到了，则判断密码是否一致
        String password = vo.getPassword();
        String md5Password = MD5.encrypt(password);
        if(!sysUser.getPassword().equals(md5Password)){
            throw new GuiguException(20001,"密码不正确");
        }

        //密码正确后，判断用户是否可用，即status字段值是否为1
        if(sysUser.getStatus().intValue()==0){
            throw new GuiguException(20001,"用户已经被禁用，请联系管理员");
        }

        //根据userid和username生成token字符串，通过map返回
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());

        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }

    //info
    /*  map.put("roles","[admin]");
        map.put("introduction","I am a super administrator at ShangHao");
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name","Super Admin at ShangHao");*/
    @ApiOperation("获取用户信息接口")
    @GetMapping("info")
    public Result info(HttpServletRequest request){
        //获取请求头token字符串
        String token = request.getHeader("token");

        //从token字符串中获取用户名称（id）
        String username = JwtHelper.getUsername(token);

        //根据用户名称获取用户信息（包括基本信息和菜单权限和按钮权限）
        Map<String,Object>  map = sysUserService.getUserInfo(username);

        return Result.ok(map);
    }

}
