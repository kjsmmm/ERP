/** 车间 */
export interface Workshop {
  id: number;
  workshopCode: string;
  workshopName: string;
  address: string;
  manager: string;
  phone: string;
  description: string;
  status: number;
}

/** 车间表单 */
export interface WorkshopFormData {
  workshopCode: string;
  workshopName: string;
  address: string;
  manager: string;
  phone: string;
  description: string;
}

/** 班组 */
export interface Team {
  id: number;
  teamCode: string;
  teamName: string;
  workshopId: number;
  leaderId: number;
  memberCount: number;
  status: number;
  workshopName: string;
  leaderName: string;
}

/** 班组表单 */
export interface TeamFormData {
  teamCode: string;
  teamName: string;
  workshopId: number;
  leaderId: number;
  memberCount: number;
}

/** 设备类型 */
export interface EquipmentType {
  id: number;
  typeCode: string;
  typeName: string;
  description: string;
}

/** 设备类型表单 */
export interface EquipmentTypeFormData {
  typeCode: string;
  typeName: string;
  description: string;
}

/** 设备 */
export interface Equipment {
  id: number;
  equipmentCode: string;
  equipmentName: string;
  equipmentTypeId: number;
  workshopId: number;
  status: number;
  purchaseDate: string;
  lastMaintenanceDate: string;
  nextMaintenanceDate: string;
  equipmentTypeName: string;
  workshopName: string;
}

/** 设备表单 */
export interface EquipmentFormData {
  equipmentCode: string;
  equipmentName: string;
  equipmentTypeId: number;
  workshopId: number;
  purchaseDate: string;
  lastMaintenanceDate: string;
  nextMaintenanceDate: string;
}

/** 工艺步骤 */
export interface ProcessStep {
  id?: number;
  stepNo: number;
  stepName: string;
  standardTime: number;
  equipmentType: string;
  description: string;
}

/** 工艺路线 */
export interface ProcessRoute {
  id: number;
  productId: number;
  routeCode: string;
  routeName: string;
  version: number;
  isDefault: number;
  status: number;
  productName: string;
  steps: ProcessStep[];
}

/** 工艺路线表单 */
export interface ProcessRouteFormData {
  productId: number;
  routeCode: string;
  routeName: string;
  isDefault: number;
  steps: ProcessStep[];
}

/** 生产计划 */
export interface ProductionPlan {
  id: number;
  planCode: string;
  orderId: number;
  productId: number;
  plannedQty: number;
  startDate: string;
  endDate: string;
  status: number;
  remark: string;
  productName: string;
  orderNo: string;
}

/** 生产计划表单 */
export interface ProductionPlanFormData {
  planCode: string;
  orderId?: number;
  productId: number;
  plannedQty: number;
  startDate?: string;
  endDate?: string;
  remark?: string;
}

/** 工单 */
export interface WorkOrder {
  id: number;
  orderNo: string;
  planId: number;
  productId: number;
  workshopId: number;
  routeId: number;
  routeName: string;
  plannedQty: number;
  actualQty: number;
  status: number;
  startDate: string;
  endDate: string;
  remark: string;
  productName: string;
  workshopName: string;
  steps: WorkOrderStep[];
}

/** 工单步骤（快照） */
export interface WorkOrderStep {
  id: number;
  workOrderId: number;
  stepNo: number;
  stepName: string;
  standardTime: number;
  equipmentType: string;
  description: string;
}

/** 工单表单 */
export interface WorkOrderFormData {
  orderNo: string;
  planId?: number;
  productId: number;
  workshopId: number;
  routeId: number;
  plannedQty: number;
  startDate?: string;
  endDate?: string;
  remark?: string;
}

/** 报工记录 */
export interface WorkReport {
  id: number;
  workOrderId: number;
  stepNo: number;
  stepName: string;
  reportQty: number;
  actualHours: number;
  reportTime: string;
  reporterId: number;
  reporterName: string;
}

/** 报工表单 */
export interface WorkReportFormData {
  workOrderId: number;
  stepNo: number;
  stepName: string;
  reportQty: number;
  actualHours?: number;
  remark?: string;
}
