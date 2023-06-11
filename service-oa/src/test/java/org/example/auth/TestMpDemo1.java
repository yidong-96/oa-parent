package org.example.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.auth.mapper.SysRoleMapper;
import org.example.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TestMpDemo1 {

    // 注入
    @Autowired
    private SysRoleMapper mapper;

    // 查询所有记录
    @Test
    public void getAll(){
        List<SysRole> list = mapper.selectList(null);
        System.out.println(list);
    }

    // 添加操作
    @Test
    public void add(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("角色管理员");
        sysRole.setRoleCode("role");
        sysRole.setDescription("角色管理员");

        int rows = mapper.insert(sysRole);
        System.out.println(rows);
    }

    // 修改操作
    @Test
    public void update() {
        // 根据id查询
        SysRole role = mapper.selectById(10);
        // 设置修改值
        role.setRoleName("总经理");
        // 调用方法实现最终修改
        int rows = mapper.updateById(role);
        System.out.println(rows);
    }

    // 删除操作
    @Test
    public void deleteId() {
        // 根据id删除
        int rows = mapper.deleteById(11);
        System.out.println(rows);
    }

    // 批量删除操作
    @Test
    public void delete() {
        // 逻辑删除id为1和10的数据
        int rows = mapper.deleteBatchIds(Arrays.asList(1,10));
        System.out.println(rows);
    }

    // 条件查询
    @Test
    public void testQuery1() {
        // 创建QueryWrapper对象，调用方法封装条件
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name","总经理");
        //调用mp方法实现查询操作
        List<SysRole> list = mapper.selectList(wrapper);
        System.out.println(list);
    }

    @Test
    public void testQuery2(){
        // 创建LambdaQueryWrapper对象，调用方法封装条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName,"总经理");
        //调用mp方法实现查询操作
        List<SysRole> list = mapper.selectList(wrapper);
        System.out.println(list);
    }
}
