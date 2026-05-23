import request from '@/utils/request';
import type { Result } from '@/types/api';

export interface TaskVO {
  taskId: string;
  taskName: string;
  assignee: string;
  processInstanceId: string;
  businessKey: string;
  processDefinitionKey: string;
  createTime: string;
  endTime?: string;
}

export interface ProcessInstanceVO {
  processInstanceId: string;
  processDefinitionKey: string;
  businessKey: string;
  startTime: string;
  endTime?: string;
  ended: boolean;
  currentTasks?: string[];
}

/** 查询待办任务 */
export function getTodoTasks(): Promise<Result<TaskVO[]>> {
  return request.get('/workflow/todo');
}

/** 查询已办任务 */
export function getDoneTasks(): Promise<Result<TaskVO[]>> {
  return request.get('/workflow/done');
}

/** 审批通过 */
export function approveTask(taskId: string, comment?: string): Promise<Result<void>> {
  return request.post(`/workflow/approve/${taskId}`, null, { params: { comment } });
}

/** 审批驳回 */
export function rejectTask(taskId: string, comment: string): Promise<Result<void>> {
  return request.post(`/workflow/reject/${taskId}`, null, { params: { comment } });
}

/** 查询流程实例 */
export function getProcessInstance(processInstanceId: string): Promise<Result<ProcessInstanceVO>> {
  return request.get(`/workflow/process/${processInstanceId}`);
}
