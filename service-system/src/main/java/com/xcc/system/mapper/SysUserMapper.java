package com.xcc.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xcc.model.system.SysRole;
import com.xcc.model.system.SysUser;
import com.xcc.model.vo.SysUserQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-05-12
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    IPage<SysUser> selectPage(IPage<SysUser> pageParam,@Param("vo") SysUserQueryVo vo);

    //根据用户名获取其角色信息（用户名为unique）
    List<SysRole> getUserRoleByUserName(@Param("userName") String username);
}
