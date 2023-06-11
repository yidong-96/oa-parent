package org.example.process.service.impl;

import org.example.auth.service.SysUserService;
import org.example.model.process.ProcessRecord;
import org.example.model.system.SysUser;
import org.example.process.mapper.OaProcessRecordMapper;
import org.example.process.service.OaProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.security.custom.LoginUserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author yidong
 * @since 2023-04-06
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public void record(Long processId, Integer status, String description) {
        Long userId = LoginUserInfoHelper.getUserId();
        SysUser sysUser = sysUserService.getById(userId);
        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUser(sysUser.getName());
        processRecord.setOperateUserId(userId);
        baseMapper.insert(processRecord);
    }
}
