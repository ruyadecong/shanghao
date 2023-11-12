package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xcc.model.system.SysRole;
import com.xcc.model.system.SysUserRole;
import com.xcc.model.vo.AssignRoleVo;
import com.xcc.model.vo.SysRoleQueryVo;
import com.xcc.system.mapper.SysRoleMapper;
import com.xcc.system.mapper.SysUserRoleMapper;
import com.xcc.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public IPage<SysRole> selectPage(Page<SysRole> pageParam, SysRoleQueryVo vo) {

        IPage<SysRole> pageModel = baseMapper.selectPage(pageParam, vo);

        return pageModel;
    }

    //获取用户角色
    @Override
    public Map<String, Object> getRoleById(Integer userId) {

        //获取所有角色
        List<SysRole> roles = baseMapper.selectList(null);

        //根据用户id获取其已经分配的所有角色id
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();

        wrapper.eq("user_id",userId);

        List<SysUserRole> userRolesList = sysUserRoleMapper.selectList(wrapper);

        List<Integer> userRoleIds = new ArrayList<>();

        for(SysUserRole userRole:userRolesList){
            Integer roleId = userRole.getRoleId();
            userRoleIds.add(roleId);
        }
        //封装到map集合中

        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("allRoles",roles);
        returnMap.put("userRoleIds",userRoleIds);

        return returnMap;
    }

    //给用户重新分配角色
    @Override
    public void doAssign(AssignRoleVo vo) {
        //先根据用户id删除之前分配的角色
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",vo.getUserId());
        sysUserRoleMapper.delete(wrapper);
        //获取所有角色id，添加角色用户关系表
        List<Integer> roleIdList = vo.getRoleIdList();
        for(Integer roleId:roleIdList){
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(vo.getUserId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleMapper.insert(sysUserRole);
        }
    }


}
