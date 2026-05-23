package com.erp.common.workflow;

import lombok.Data;

import java.util.Date;

@Data
public class TaskVO {

    private String taskId;
    private String taskName;
    private String assignee;
    private String processInstanceId;
    private String businessKey;
    private String processDefinitionKey;
    private Date createTime;
    private Date endTime;
}
