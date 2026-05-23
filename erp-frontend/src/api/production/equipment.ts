import request from '@/utils/request';
import type { Result, PageResult } from '@/types/api';
import type { Equipment, EquipmentFormData } from '@/types/production';

/** 设备分页查询 */
export function getEquipmentPage(params?: { workshopId?: number; equipmentTypeId?: number; status?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<Equipment>>> {
  return request.get('/equipments', { params });
}

/** 创建设备 */
export function createEquipment(data: EquipmentFormData): Promise<Result<void>> {
  return request.post('/equipments', data);
}

/** 更新设备 */
export function updateEquipment(id: number, data: EquipmentFormData): Promise<Result<void>> {
  return request.put(`/equipments/${id}`, data);
}

/** 删除设备 */
export function deleteEquipment(id: number): Promise<Result<void>> {
  return request.delete(`/equipments/${id}`);
}
