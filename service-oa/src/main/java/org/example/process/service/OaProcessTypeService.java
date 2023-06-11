package org.example.process.service;

import org.example.model.process.ProcessType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author yidong
 * @since 2023-04-05
 */
public interface OaProcessTypeService extends IService<ProcessType> {

    // 查询所有审批分类和每个分类所有的审批模板
    List<ProcessType> findProcessType();
}
