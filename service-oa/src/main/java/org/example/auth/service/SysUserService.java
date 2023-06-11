package org.example.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.system.SysUser;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author yidong
 * @since 2023-03-28
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);

    SysUser getUserByUserName(String username);

    Map<String, Object> getCurrentUser();
}
