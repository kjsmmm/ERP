import request from '@/utils/request';
import type { Result } from '@/types/api';
import type { EquipmentType, EquipmentTypeFormData } from '@/types/production';

/** 设备类型列表 */
export function getEquipmentTypeList(): Promise<Result<EquipmentType[]>> {
  return request.get('/equipment-types');
}

/** 创建设备类型 */
export function createEquipmentType(data: EquipmentTypeFormData): Promise<Result<void>> {
  return request.post('/equipment-types', data);
}

/** 更新设备类型 */
export function updateEquipmentType(id: number, data: EquipmentTypeFormData): Promise<Result<void>> {
  return request.put(`/equipment-types/${id}`, data);
}

/** 删除设备类型 */
export function deleteEquipmentType(id: number): Promise<Result<void>> {
  return request.delete(`/equipment-types/${id}`);
}
