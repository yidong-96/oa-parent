package org.example.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.model.system.SysUserRole;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户角色 Mapper 接口
 * </p>
 *
 * @author yidong
 * @since 2023-03-28
 */

@Repository
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

}
