package org.example.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.auth.mapper.SysRoleMapper;
import org.example.auth.service.SysRoleService;
import org.example.auth.service.SysUserRoleService;
import org.example.model.system.SysRole;
import org.example.model.system.SysUserRole;
import org.example.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysUserRoleService sysUserRoleService;
    // 查询所有角色和当前用户所属角色
    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        // 查询所有角色，返回list集合
        List<SysRole> allRoleList = baseMapper.selectList(null);

        // 根据userid查询角色用户关系表，查询userid对应所有角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> existUserRoleList = sysUserRoleService.list(wrapper);

        // 从查询出的用户id对应角色list集合，获取所有角色id
//        List<Long> list = new ArrayList<>();
//        for (SysUserRole sysUserRole : existUserRoleList) {
//            Long roleId = sysUserRole.getRoleId();
//            list.add(roleId);
//        }
        List<Long> existUserRoleIdList = existUserRoleList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());
        // 根据查询所有角色id，找到对应角色信息
        // 根据角色id到所有角色的list集合中进行比较
        List<SysRole> assignRoleList = new ArrayList<>();
        for (SysRole sysRole : allRoleList) {
            // 比较
            if (existUserRoleIdList.contains(sysRole.getId())) {
                assignRoleList.add(sysRole);
            }
        }

        // 把得到的两部分数据封装map集合
        Map<String,Object> roleMap = new HashMap<>();
        roleMap.put("assignRoleList",assignRoleList);
        roleMap.put("allRoleList",allRoleList);
        return roleMap;
    }

    // 为用户分配角色
    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        // 把用户之前分配角色数据删除，用户角色关系表里面，根据userid删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        sysUserRoleService.remove(wrapper);


        // 重新进行分配
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for (Long roleId : roleIdList) {
            if (StringUtils.isEmpty(roleId)) {
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleService.save(sysUserRole);
        }

    }

//    @Autowired
//    private SysRoleMapper sysRoleMapper;
}
