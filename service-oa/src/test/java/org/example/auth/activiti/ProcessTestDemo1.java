package org.example.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class ProcessTestDemo1 {
    // 注入RepositoryService
    @Autowired
    private RepositoryService repositoryService;

    // 注入RuntimeService
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * 部署流程定义：uel-method实现
     */
    @Test
    public void deployProcess01() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban01.bpmn20.xml")
                .addClasspathResource("process/jiaban.png")
                .name("加班申请流程01")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    // 启动流程实例
    @Test
    public void startProcessInstance01() {
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("jiaban01");
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }

    /**
     * 部署流程定义：uel-value实现
     */
    @Test
    public void deployProcess() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban.bpmn20.xml")
                .addClasspathResource("process/jiaban.png")
                .name("加班申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    // 启动流程实例
    @Test
    public void startProcessInstance() {
        Map<String,Object> map = new HashMap<>();
        // 设置任务人
        map.put("assignee1","lucy");
        map.put("assignee2","mary");
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("jiaban",map);
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }

    /**
     * 监听器分配任务
     */
    @Test
    public void deployProcess02() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban02.bpmn20.xml")
                .addClasspathResource("process/jiaban.png")
                .name("加班申请流程02")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    // 启动流程实例
    @Test
    public void startProcessInstance02() {
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("jiaban02");
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }
}
