package com.erp.common.workflow;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProcessInstanceVO {

    private String processInstanceId;
    private String processDefinitionKey;
    private String businessKey;
    private Date startTime;
    private Date endTime;
    private boolean ended;
    private List<String> currentTasks;
}
