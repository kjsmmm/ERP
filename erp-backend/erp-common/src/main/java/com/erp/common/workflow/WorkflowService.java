package com.erp.common.workflow;

import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    /**
     * 发起流程
     */
    public String startProcess(String processDefinitionKey, String businessKey,
                               String initiator, Map<String, Object> variables) {
        Map<String, Object> vars = variables != null ? new HashMap<>(variables) : new HashMap<>();
        vars.put("initiator", initiator);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                processDefinitionKey, businessKey, vars);
        return instance.getId();
    }

    /**
     * 查询待办任务
     */
    public List<TaskVO> getTodoTasks(String assignee) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned(assignee)
                .orderByTaskCreateTime().desc()
                .list();

        return tasks.stream().map(task -> {
            TaskVO vo = new TaskVO();
            vo.setTaskId(task.getId());
            vo.setTaskName(task.getName());
            vo.setAssignee(task.getAssignee());
            vo.setProcessInstanceId(task.getProcessInstanceId());
            vo.setCreateTime(task.getCreateTime());

            ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            if (instance != null) {
                vo.setBusinessKey(instance.getBusinessKey());
                vo.setProcessDefinitionKey(instance.getProcessDefinitionKey());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询已办任务
     */
    public List<TaskVO> getDoneTasks(String assignee) {
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(assignee)
                .finished()
                .orderByHistoricTaskInstanceEndTime().desc()
                .list();

        return tasks.stream().map(task -> {
            TaskVO vo = new TaskVO();
            vo.setTaskId(task.getId());
            vo.setTaskName(task.getName());
            vo.setAssignee(task.getAssignee());
            vo.setProcessInstanceId(task.getProcessInstanceId());
            vo.setCreateTime(task.getCreateTime());
            vo.setEndTime(task.getEndTime());

            HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            if (instance != null) {
                vo.setBusinessKey(instance.getBusinessKey());
                vo.setProcessDefinitionKey(instance.getProcessDefinitionId());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 审批通过
     */
    public void approveTask(String taskId, String assignee, String comment) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(assignee)
                .singleResult();
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND, "任务不存在或无权操作");
        }

        if (comment != null && !comment.isEmpty()) {
            taskService.addComment(taskId, task.getProcessInstanceId(), "APPROVE", comment);
        }

        Map<String, Object> vars = new HashMap<>();
        vars.put("approved", true);
        taskService.complete(taskId, vars);
    }

    /**
     * 审批驳回
     */
    public void rejectTask(String taskId, String assignee, String comment) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(assignee)
                .singleResult();
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND, "任务不存在或无权操作");
        }

        if (comment == null || comment.isEmpty()) {
            throw new BusinessException(ErrorCode.REJECT_COMMENT_REQUIRED, "驳回时必须填写审批意见");
        }

        taskService.addComment(taskId, task.getProcessInstanceId(), "REJECT", comment);

        Map<String, Object> vars = new HashMap<>();
        vars.put("approved", false);
        taskService.complete(taskId, vars);
    }

    /**
     * 查询流程实例信息
     */
    public ProcessInstanceVO getProcessInstance(String processInstanceId) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (instance != null) {
            ProcessInstanceVO vo = new ProcessInstanceVO();
            vo.setProcessInstanceId(instance.getId());
            vo.setProcessDefinitionKey(instance.getProcessDefinitionKey());
            vo.setBusinessKey(instance.getBusinessKey());
            vo.setStartTime(instance.getStartTime());
            vo.setEnded(false);

            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .list();
            vo.setCurrentTasks(tasks.stream().map(Task::getName).collect(Collectors.toList()));
            return vo;
        }

        // 查历史
        HistoricProcessInstance historic = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (historic == null) {
            return null;
        }

        ProcessInstanceVO vo = new ProcessInstanceVO();
        vo.setProcessInstanceId(historic.getId());
        vo.setProcessDefinitionKey(historic.getProcessDefinitionId());
        vo.setBusinessKey(historic.getBusinessKey());
        vo.setStartTime(historic.getStartTime());
        vo.setEndTime(historic.getEndTime());
        vo.setEnded(true);
        return vo;
    }
}
