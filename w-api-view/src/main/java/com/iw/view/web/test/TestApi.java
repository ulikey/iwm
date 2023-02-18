package com.iw.view.web.test;

import com.alibaba.cloud.commons.lang.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "测试api")
@RestController
@RequestMapping("/test")
@RefreshScope
public class TestApi {
    @Value("${aa.bb:}")
    private String aa_bb;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /**
     * 测试-nacos配置是否生效
     * @return
     */
    @Operation(summary = "info", description = "bar")
    @GetMapping("/info")
    public String info() {
        return aa_bb;
    }

    /**
     * 测试-查寻所有流程定义
     * @param key:  test[a1234]
     * @return
     */
    @GetMapping(value = {"/deploy/{key}","/deploy"})
    public List<String> getDeployList(@PathVariable(name = "key",required = false) String key) {
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> definitionList;
        if (StringUtils.isNotBlank(key)) {
            definitionList = definitionQuery
                    .processDefinitionName(key)
                    .list();
        } else {
            definitionList = definitionQuery.list();
        }
        // 提取所有的流程定义名称
        List<String> deployList = new ArrayList<>();
        for (ProcessDefinition processDefinition : definitionList) {
            deployList.add(processDefinition.getName() + "  : " + processDefinition.getKey() + " : " + processDefinition.getDeploymentId());
        }
        return deployList;
    }

    /**
     * 删除流程定义
     * @param deployId
     * @return
     */
    @Delete("/deploy/{deployId}")
    public String delDeployment(@PathVariable(name  = "deployId") String deployId) {
        repositoryService.deleteDeployment(deployId);
        // 设置为true，级联删除流程定义。即使该流程有流程实例启动，也可以删除
        // repositoryService.deleteDeployment(deployId, true);
        return "success";
    }

    /**
     * 测试-启动一个流程实例
     * @param key: test[a1234]
     * @return
     */
    @PostMapping("process/start/{key}")
    public String startProcess(@PathVariable(name = "key") String key,  @RequestParam(name = "businessId") String businessId){
        Map<String,Object> map = new HashMap<>();
        map.put("user","acc_user");
        map.put("admin","boss");
        // 启动 key 标识的流程定义，并指定 流程定义中的参数assignee：${user}/${admin}
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, businessId, map);
        String processDefinitionId =  processInstance.getProcessDefinitionId();
        String instanceId =  processInstance.getId();
        return key + " : " + processDefinitionId + " // " + instanceId;
    }

    /**
     * 测试-查询待处理的任务流程
     * @param key
     * @param id
     * @param assignee
     * @return
     */
    @GetMapping(value = {"/process"})
    public List<String> getProcessList(@RequestParam(name = "key") String key, @RequestParam(name = "id") String id, @RequestParam(name = "assignee") String assignee) {
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(key)      // 可以通过流程定义的key来查询 应该可以省略
                .processInstanceId(id)          // 可以通过某个流程实例的id查询 应该可以省略
                .taskAssignee(assignee)         // 按照处理人查询 应该可以省略
                .list();
        List<String> processList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (Task task : list) {
                String taskId = task.getId();
                String name = task.getName();
                String deployId = task.getProcessDefinitionId();
                processList.add(deployId + ":" + name + " : " + taskId);
            }
        }
        return processList;
    }


    /**
     * 测试-提交当前节点
     * @param id: 流程实例id
     * @param assigne 节点分配用户
     * @return
     */
    @PostMapping("/process/complete")
    public String completeProcess(@RequestParam(name = "id") String id, @RequestParam(name = "assignee")String assigne){
        List<Task> tasks = taskService.createTaskQuery().processDefinitionId(id)
                .taskAssignee(assigne)
                .list();
        if (!CollectionUtils.isEmpty(tasks)) {
            for (Task task : tasks) {
                // 完成当前节点
                taskService.complete(task.getId());
            }
        }
        return "success";
    }

    /**
     * 测试-查看某个流程定义所有的实例 或者 某个流程实例的执行情况
     * @param instanceId
     * @param deployId
     * @return
     */
    @GetMapping("/processFull")
    public List<HistoricActivityInstance> getProcessListFull(@RequestParam(name="instanceId") String instanceId, @RequestParam(name="deployId")String deployId) {
        List<HistoricActivityInstance> activityInstanceList  = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(instanceId) // 根据流程实例Id，查询单个流程实例的执行情况
                // 根据流程定义ID，查询该流程所有实例的执行情况
                // .processDefinitionId(deployId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();
        return activityInstanceList;
    }
}
