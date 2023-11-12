package com.xcc.system.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.system.SysRole;
import com.xcc.model.system.SysUser;
import com.xcc.model.vo.SysUserQueryVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-05-12
 */
public interface SysUserService extends IService<SysUser> {

    IPage<SysUser> selectPage(IPage<SysUser> pageParam, SysUserQueryVo vo);

    void updateStatus(Integer id, Integer status);


    //根据username查询数据库
    SysUser getUserInfoByUserName(String username);

    Map<String, Object> getUserInfo(String username);

    //根据用户名获取其角色信息（用户名为unique）
    List<SysRole> getUserRoleByUserName(String username);
}
