package org.example.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ProcessTestGateway {

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

    // 部署流程定义
    @Test
    public void deployProcess() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia003.bpmn20.xml")
                .addClasspathResource("process/qingjia003.png")
                .name("请假申请流程003")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    // 启动流程实例
    @Test
    public void startProcess() {
        Map<String,Object> map = new HashMap<>();
        // 设置请假天数
        map.put("day","3");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia003");
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }

    // 查询当前个人待执行的任务
    @Test
    public void findTaskList() {
//        String assign = "gouwa";
        String assign = "xiaoli";
//        String assign = "wang5";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assign).list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    // 处理当前任务
    @Test
    public void completeTask() {
        // 查询负责人需要处理的任务，返回一条
        Task task = taskService.createTaskQuery()
//                .taskAssignee("xiaoli")
                .taskAssignee("gouwa")
//                .taskAssignee("wang5")
                .singleResult();
        // 完成任务,参数是任务id
        taskService.complete(task.getId());
    }
}
