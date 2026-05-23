import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { Team, TeamFormData } from '@/types/production';

/** 班组分页查询 */
export function getTeamPage(params?: { workshopId?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<Team>>> {
  return request.get('/teams', { params });
}

/** 按车间查询班组列表 */
export function getTeamsByWorkshopId(workshopId: number): Promise<Result<Team[]>> {
  return request.get(`/teams/workshop/${workshopId}`);
}

/** 创建班组 */
export function createTeam(data: TeamFormData): Promise<Result<void>> {
  return request.post('/teams', data);
}

/** 更新班组 */
export function updateTeam(id: number, data: TeamFormData): Promise<Result<void>> {
  return request.put(`/teams/${id}`, data);
}

/** 删除班组 */
export function deleteTeam(id: number): Promise<Result<void>> {
  return request.delete(`/teams/${id}`);
}
