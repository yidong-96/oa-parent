package org.example.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.activiti.bpmn.model.Task;
import org.example.model.process.Process;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.vo.process.ApprovalVo;
import org.example.vo.process.ProcessFormVo;
import org.example.vo.process.ProcessQueryVo;
import org.example.vo.process.ProcessVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author yidong
 * @since 2023-04-05
 */
public interface OaProcessService extends IService<Process> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    // 部署流程定义
    void deployByZip(String deployPath);

    // 启动流程
    void startUp(ProcessFormVo processFormVo);

    // 查询待处理任务列表
    IPage<ProcessVo> findFindPending(Page<Process> pageParam);

    Map<String, Object> show(Long id);

    // 审批
    void approve(ApprovalVo approvalVo);

    IPage<ProcessVo> findProcessed(Page<Process> pageParam);

    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
