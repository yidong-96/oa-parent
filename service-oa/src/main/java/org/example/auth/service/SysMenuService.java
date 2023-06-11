package org.example.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.system.SysMenu;
import org.example.vo.system.AssginMenuVo;
import org.example.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author yidong
 * @since 2023-03-29
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    void removeMenuById(Long id);

    List<SysMenu> findMenuByRoleId(Long roleId);

    void doAssign(AssginMenuVo assginMenuVo);

    List<RouterVo> findUserMenuListByUserId(Long userId);

    List<String> findUserPermsByUserId(Long userId);
}
