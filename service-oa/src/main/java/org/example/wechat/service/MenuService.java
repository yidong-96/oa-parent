package org.example.wechat.service;

import org.example.model.wechat.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author yidong
 * @since 2023-04-09
 */
public interface MenuService extends IService<Menu> {

    // 获取全部菜单
    List<MenuVo> findMenuInfo();

    // 同步菜单
    void syncMenu();

    // 删除菜单
    void removeMenu();
}
