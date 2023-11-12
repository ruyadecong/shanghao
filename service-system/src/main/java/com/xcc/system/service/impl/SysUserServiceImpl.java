package com.xcc.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xcc.model.system.SysRole;
import com.xcc.model.system.SysUser;
import com.xcc.model.vo.RouterVo;
import com.xcc.model.vo.SysRoleQueryVo;
import com.xcc.model.vo.SysUserQueryVo;
import com.xcc.system.mapper.SysUserMapper;
import com.xcc.system.service.SysMenuService;
import com.xcc.system.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-05-12
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {


    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public IPage<SysUser> selectPage(IPage<SysUser> pageParam, SysUserQueryVo vo) {

        IPage<SysUser> pageModel = baseMapper.selectPage(pageParam,vo);

        return pageModel;
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        SysUser sysUser = baseMapper.selectById(id);
        sysUser.setStatus(status);
        baseMapper.updateById(sysUser);
    }

    //根据username查询数据库
    @Override
    public SysUser getUserInfoByUserName(String username) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);

        return baseMapper.selectOne(wrapper);
    }

    @Override
    public Map<String, Object> getUserInfo(String username) {
        //根据username查询用户基本信息
        SysUser sysUser = this.getUserInfoByUserName(username);

        //根据userid查询菜单权限值
        List<RouterVo> routerVoList = sysMenuService.getUserMenuList(sysUser.getId());

        //根据userid查询按钮权限值
        List<String> permsList = sysMenuService.getUserButtonList(sysUser.getId());

        Map<String,Object> result = new HashMap<>();

        result.put("name",username);
        result.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        result.put("roles","[admin]");
        //菜单权限数据
        result.put("routers",routerVoList);
        //按钮权限数据
        result.put("buttons",permsList);

        return result;
    }

    //根据用户名获取其角色信息（用户名为unique）
    @Override
    public List<SysRole> getUserRoleByUserName(String username) {
        List<SysRole> list = baseMapper.getUserRoleByUserName(username);
        return list;
    }
}
