package com.xcc.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.common.result.Result;
import com.xcc.model.system.SysRole;
import com.xcc.model.vo.AssignRoleVo;
import com.xcc.model.vo.SysRoleQueryVo;
import com.xcc.system.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;


@Api(tags = "角色管理接口")
@RestController                 //如果这里不是@RestController而是@Controller，那么PostMapping会返回404错误，同时这个注解也使得返回的数据为jason格式
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("查询所有的记录")
    @GetMapping("findAll")
    public Result findAllRole(){

/*        //TODO 模拟异常效果

        try{
            int i =9/0;
        }catch (Exception e){
            //手动抛出异常
            throw new GuiguException(20001,"执行了自定义异常处理");
        }*/

        List<SysRole> list = sysRoleService.list(null);
        return Result.ok(list);
    }


    @ApiOperation("逻辑删除接口")
    @DeleteMapping("remove/{id}")
    public Result removeRole(@PathVariable Integer id){

        boolean b = sysRoleService.removeById(id);

        if (b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result findPageQuerySysRole(@PathVariable Long page,
                                    @PathVariable Long limit,
                                    SysRoleQueryVo vo){
        //创建page对象
        Page<SysRole> pageParam = new Page<>(page,limit);
        //调用service方法
        IPage<SysRole> pageModel  = sysRoleService.selectPage(pageParam,vo);

        return Result.ok(pageModel);
    }

    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result saveRole(@RequestBody SysRole sysRole){
        //@RequestBody 不能用于get提交方式，其传递jason格式数据，把jason格式数据封装到对象即sysRole里面
        //第53行的SysRoleQueryVo vo和这个其实是差不多的
        //这里不加@RequestBody其实也是没问题的
        //至于具体的jason数据格式的sysRole对象，是需要前端来提交的，这里只是接口
        boolean b = sysRoleService.save(sysRole);
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("根据id查询")
    @PostMapping("findRoleById/{id}")
    public Result findRoleById(@PathVariable Integer id){

        SysRole sysRole = sysRoleService.getById(id);

        return Result.ok(sysRole);
    }

    @ApiOperation("修改角色")
    @PostMapping("update")
    public Result updateRole(@RequestBody SysRole sysRole){  //前端提交sysRole
        boolean b = sysRoleService.updateById(sysRole);
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }


    @ApiOperation("批量逻辑删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Integer> ids){
        //传入多个id值[...]，这在jason中会组装成数组格式
        //jason中的数组对应java中的List对象，所以参数列表中要使用List，以此来得到前端传过来的jason数组
        //再swagger中进行测试时，要求提供的参数就是[...]格式的
        boolean b = sysRoleService.removeByIds(ids);
        if(b){
            return Result.ok();
        }else{
            return Result.fail();
        }

    }

    //获取用户角色
    @ApiOperation("获取用户角色数据")
    @GetMapping("toAssign/{userId}")
    public Result toAssign(@PathVariable Integer userId){

        Map<String, Object> roleMap= sysRoleService.getRoleById(userId);

        return Result.ok(roleMap);
    }


    @ApiOperation("给用户重新分配角色")
    @PostMapping("doAssign")
    public Result doAssign(@RequestBody AssignRoleVo vo){

        sysRoleService.doAssign(vo);

        return Result.ok();
    }


}
