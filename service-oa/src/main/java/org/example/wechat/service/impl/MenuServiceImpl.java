package org.example.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.example.model.wechat.Menu;
import org.example.vo.wechat.MenuVo;
import org.example.wechat.mapper.MenuMapper;
import org.example.wechat.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author yidong
 * @since 2023-04-09
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private WxMpService wxMpService;

    // 获取全部菜单
    @Override
    public List<MenuVo> findMenuInfo() {
        List<MenuVo> list = new ArrayList<>();
        // 查询所有菜单list集合
        List<Menu> menuList = baseMapper.selectList(null);
        // 查询所有一级菜单parent_id=0,返回一级菜单list集合
        List<Menu> oneMenuList = menuList.stream().filter(menu -> menu.getParentId().longValue() == 0)
                .collect(Collectors.toList());
        // 遍历一级菜单list集合
        for (Menu oneMenu : oneMenuList) {
            // 一级菜单Menu   --->  MenuVo
            MenuVo oneMenuvo = new MenuVo();
            BeanUtils.copyProperties(oneMenu,oneMenuvo);
            // 获取每个一级菜单里面所有的二级菜单
            // 一级菜单的id和其它菜单的parent_id
            List<Menu> twoMenuList = menuList.stream()
                    .filter(menu -> menu.getParentId().longValue() == oneMenu.getId().longValue())
                    .collect(Collectors.toList());
            // 把一级菜单里面所有二级菜单获取到，封装一级菜单children集合里面
            // List<Menu>  --->  List<MenuVo>
            List<MenuVo> children = new ArrayList<>();
            for (Menu menu : twoMenuList) {
                MenuVo twoMenuvo = new MenuVo();
                BeanUtils.copyProperties(menu,twoMenuvo);
                children.add(twoMenuvo);
            }
            oneMenuvo.setChildren(children);
            // 把每个封装好的一级菜单放到最终list集合中
            list.add(oneMenuvo);
        }
        return list;
    }

    // 同步菜单
    @Override
    public void syncMenu() {
        // 菜单数据查询出来，封装微信要求的菜单格式
        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        for(MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if(CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://oaparent.5gzvip.91tunnel.com/#"+oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for(MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if(twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://oaparent.5gzvip.91tunnel.com#"+twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        //菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);

        // 调用工具里面的方法实现菜单推送
        try {
            wxMpService.getMenuService().menuCreate(button.toString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    // 删除菜单
    @Override
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
