package org.example.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.auth.service.SysMenuService;
import org.example.auth.service.SysUserService;
import org.example.common.config.exception.MyTestException;
import org.example.common.jwt.JwtHelper;
import org.example.common.result.Result;
import org.example.common.utils.MD5;
import org.example.model.system.SysUser;
import org.example.vo.system.LoginVo;
import org.example.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 后台登录登出
 * </p>
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    // login
    @ApiOperation("登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        // {"code":20000,"data":{"token":"admin-token"}}
//        Map<String,Object> map = new HashMap<>();
//        map.put("token","admin-token");
//        return Result.ok(map);
        // 获取用户名和密码
        String username = loginVo.getUsername();
        String password_input = MD5.encrypt(loginVo.getPassword());
        // 根据用户名查询数据库
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = sysUserService.getOne(wrapper);
        // 用户信息是否存在
        if (sysUser == null) {
            throw new MyTestException(201,"用户不存在");
        }
        // 判断密码
        String password_db = sysUser.getPassword();
        if (!password_db.equals(password_input)) {
            throw new MyTestException(201,"密码错误");
        }

        // 判断用户是否被禁用
        if (sysUser.getStatus().intValue() == 0) {
            throw new MyTestException(201,"用户已经被禁用");
        }
        // 使用jwt根据用户id和用户名称生成token字符串
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        // 返回
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }
    // info
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        // {"code":20000,"data":{"roles":["admin"],"introduction":"I am a super administrator",
        // "avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif",
        // "name":"Super Admin"}}

        // 从请求头获取用户信息(获取请求头token字符串)
        String token = request.getHeader("token");
        // 从token字符串获取用户id或者用户名称
        Long userId = JwtHelper.getUserId(token);
        // 根据用户id查询数据库，把用户信息获取出来
        SysUser sysUser = sysUserService.getById(userId);
        // 根据用户id获取用户可以操作的菜单列表
        // 查询数据库动态构建路由结构，进行显示
        List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);
        // 根据用户id获取用户可以操作的按钮列表
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);
        // 返回相应的数据
        Map<String,Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("introduction","I am a super administrator");
        map.put("name",sysUser.getName());
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        // 返回用户可以操作的菜单
        map.put("routers",routerList);
        // 返回用户可以操作的按钮
        map.put("buttons",permsList);
        return Result.ok(map);
    }

    /**
     * 退出
     * @return
     */
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
