package org.example.auth.service.impl;

import org.example.auth.service.SysMenuService;
import org.example.auth.service.SysUserService;
import org.example.model.system.SysUser;
import org.example.security.custom.CustomUser;
import org.example.security.custom.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名进行查询
        SysUser sysUser = sysUserService.getUserByUserName(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if (sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用！");
        }

        // 根据userid查询用户操作权限数据
        List<String> userPermsList = sysMenuService.findUserPermsByUserId(sysUser.getId());
        // 创建list集合，封装最终权限数据
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        // 查询list集合进行遍历
        for (String perm : userPermsList) {
            authList.add(new SimpleGrantedAuthority(perm.trim()));
        }
        return new CustomUser(sysUser, authList);
    }
}
