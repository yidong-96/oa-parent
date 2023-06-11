package org.example.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.auth.mapper.SysMenuMapper;
import org.example.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.auth.service.SysRoleMenuService;
import org.example.auth.utils.MenuHelper;
import org.example.common.config.exception.MyTestException;
import org.example.model.system.SysMenu;
import org.example.model.system.SysRoleMenu;
import org.example.vo.system.AssginMenuVo;
import org.example.vo.system.MetaVo;
import org.example.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author yidong
 * @since 2023-03-29
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    // 菜单列表接口
    @Override
    public List<SysMenu> findNodes() {
        // 查询所有菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);

        // 构建树形结构
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);


        return resultList;
    }

    // 删除菜单
    @Override
    public void removeMenuById(Long id) {
        // 判断当前菜单是否有下一层菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new MyTestException(201,"菜单不能删除");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        // 查询所有菜单，条件：ststus=1
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus,1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);

        // 根据角色id、roleId，查询 角色菜单关系表里面，角色id对应所有的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);

        // 根据获取的菜单id，获取对应菜单对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());

        // 菜单id 和 所有菜单集合里面的id进行比较，如果相同则封装
        allSysMenuList.stream().forEach(item -> {
            if (menuIdList.contains(item.getId())) {
                item.setSelect(true);
            } else {
                item.setSelect(false);
            }
        });

        // 返回规定格式菜单列表
        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
        return sysMenuList;
    }

    // 为角色分配菜单

    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        // 根据角色id，删除菜单角色表，分配数据
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assginMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);
        // 从参数里面获取角色新分配菜单id列表
        // 进行遍历，把每个id数据添加到菜单角色表
        List<Long> menuIdList = assginMenuVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            if (StringUtils.isEmpty(menuId)) {
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assginMenuVo.getRoleId());
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    // 根据用户id获取用户可以操作的菜单列表
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        // 判断当前用户是否是管理员，UserId=1是管理员
        // 如果是管理员，查询所有菜单列表
        if (userId.longValue() == 1) {
            // 查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            // 如果不是管理员，根据userId查询可以操作菜单列表
            // 多表关联查询：用户角色关系表、角色菜单关系表、菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        // 把查询出来的数据列表构建成框架要求的路由数据结构
        // 使用菜单操作工具类构建树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
        // 构建成框架要求的路由数据结构
        List<RouterVo> routerList = this.buildRouter(sysMenuTreeList);
        return routerList;
    }

    // 构建成框架要求的路由数据结构
    private List<RouterVo> buildRouter(List<SysMenu> menus) {
        // 创建list集合，存储最终数据
        List<RouterVo> routers = new ArrayList<>();
        // menus遍历
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(),menu.getIcon()));
            // 下一层数据部分
            List<SysMenu> children = menu.getChildren();
            if (menu.getType().intValue() == 1) {
                // 加载出来下面的隐藏路由
                List<SysMenu> hiddenMenuList = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    // true表示隐藏路由
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    if (children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    // 递归
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    // 根据用户id获取用户可以操作的按钮列表
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        // 判断当前用户是否是管理员，UserId=1是管理员
        // 如果是管理员，查询所有按钮列表
        if (userId.longValue() == 1) {
            // 查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            // 如果不是管理员，根据userId查询可以操作按钮列表
            // 多表关联查询：用户角色关系表、角色菜单关系表、菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        // 从查询出来的数据里面，获取可以操作按钮值的list集合
        List<String> permsList = sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(item -> item.getPerms())
                .collect(Collectors.toList());
        return permsList;
    }
}
