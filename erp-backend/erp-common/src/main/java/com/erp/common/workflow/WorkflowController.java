package com.erp.common.workflow;

import com.erp.common.context.UserIdProvider;
import com.erp.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;
    private final UserIdProvider userIdProvider;

    @GetMapping("/todo")
    public Result<List<TaskVO>> getTodoTasks() {
        String assignee = String.valueOf(userIdProvider.getCurrentUserId());
        return Result.success(workflowService.getTodoTasks(assignee));
    }

    @GetMapping("/done")
    public Result<List<TaskVO>> getDoneTasks() {
        String assignee = String.valueOf(userIdProvider.getCurrentUserId());
        return Result.success(workflowService.getDoneTasks(assignee));
    }

    @PostMapping("/approve/{taskId}")
    public Result<Void> approve(@PathVariable String taskId,
                                @RequestParam(required = false) String comment) {
        String assignee = String.valueOf(userIdProvider.getCurrentUserId());
        workflowService.approveTask(taskId, assignee, comment);
        return Result.success();
    }

    @PostMapping("/reject/{taskId}")
    public Result<Void> reject(@PathVariable String taskId,
                               @RequestParam String comment) {
        String assignee = String.valueOf(userIdProvider.getCurrentUserId());
        workflowService.rejectTask(taskId, assignee, comment);
        return Result.success();
    }

    @GetMapping("/process/{processInstanceId}")
    public Result<ProcessInstanceVO> getProcessInstance(@PathVariable String processInstanceId) {
        return Result.success(workflowService.getProcessInstance(processInstanceId));
    }
}
