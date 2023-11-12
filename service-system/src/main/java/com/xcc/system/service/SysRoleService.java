package com.xcc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.system.SysRole;
import com.xcc.model.vo.AssignRoleVo;
import com.xcc.model.vo.SysRoleQueryVo;


import java.util.Map;


public interface SysRoleService extends IService<SysRole> {


    IPage<SysRole> selectPage(Page<SysRole> pageParam, SysRoleQueryVo vo);


    //获取用户角色
    Map<String, Object> getRoleById(Integer userId);

    //给用户重新分配角色
    public void doAssign(AssignRoleVo vo);
}
