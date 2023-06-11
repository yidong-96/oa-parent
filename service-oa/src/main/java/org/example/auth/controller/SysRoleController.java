package org.example.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.auth.service.SysRoleService;
import org.example.common.config.exception.MyTestException;
import org.example.common.result.Result;
import org.example.model.system.SysRole;
import org.example.vo.system.AssginRoleVo;
import org.example.vo.system.SysRoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "角色管理接口")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    // http://localhost:8800/admin/system/sysRole/findAll

    // 注入service
    @Autowired
    private SysRoleService sysRoleService;

//    // 查询所有角色
//    @GetMapping("/findAll")
//    public List<SysRole> findAll() {
//        // 调用service的方法
//        List<SysRole> list = service.list();
//        return list;
//    }

    // 统一返回数据结果
    @ApiOperation("查询所有角色")
    @GetMapping("/findAll")
    public Result findAll() {
        // 调用service的方法
        List<SysRole> list = sysRoleService.list();
//        try {
//            // 模拟异常效果
//            int i = 10/0;
//        } catch (Exception e) {
//            // 抛出自定义异常
//            throw new MyTestException(20001,"执行了自定义异常处理...");
//        }
        return Result.ok(list);
    }

    // 条件分页查询
    // page代表当前页，limit代表每页显示记录数，sysRoleQueryVo代表条件对象
    @ApiOperation("条件分页查询")
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo) {
        // 调用service的方法实现
        // 创建page对象，传递分页相关参数
        Page<SysRole> pageParam = new Page<>(page,limit);
        // 封装条件，判断条件是否为空，不为空进行封装
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)) {
            // 封装  like表示模糊查询
            wrapper.like(SysRole::getRoleName,roleName);
        }
        // 调用方法实现
        IPage<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }

    // 添加角色
    @ApiOperation("添加角色")
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role) {
        // 调用service的方法
        boolean is_success = sysRoleService.save(role);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 修改角色-根据id查询
    @ApiOperation("根据id查询")
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }

    // 修改角色-最终修改
    @ApiOperation("修改角色")
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role) {
        // 调用service的方法
        boolean is_success = sysRoleService.updateById(role);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 根据id删除
    @ApiOperation("根据id删除")
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean is_success = sysRoleService.removeById(id);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 批量删除
    @ApiOperation("批量删除")
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean is_success = sysRoleService.removeByIds(idList);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 查询所有角色和当前用户所属角色
    @ApiOperation("获取角色")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String,Object> map = sysRoleService.findRoleDataByUserId(userId);
        return Result.ok(map);
    }

    // 为用户分配角色
    @ApiOperation("为用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }
}
