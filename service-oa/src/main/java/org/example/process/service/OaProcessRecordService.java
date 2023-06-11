package org.example.process.service;

import org.example.model.process.ProcessRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author yidong
 * @since 2023-04-06
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {

    //
    void record(Long processId,Integer status,String description);

}
