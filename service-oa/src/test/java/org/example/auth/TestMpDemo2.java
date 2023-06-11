package org.example.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.auth.mapper.SysRoleMapper;
import org.example.auth.service.SysRoleService;
import org.example.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TestMpDemo2 {

    // 注入
    @Autowired
    private SysRoleService service;

    // 查询所有记录
    @Test
    public void getAll(){
        List<SysRole> list = service.list();
        System.out.println(list);
    }
}
