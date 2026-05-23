/** 检验标准 */
export interface QualityStandard {
  id: number;
  standardCode: string;
  standardName: string;
  applicableType: number;
  categoryId: number;
  status: number;
  items: QualityStandardItem[];
}

/** 检验项目 */
export interface QualityStandardItem {
  id?: number;
  standardId?: number;
  itemName: string;
  inspectionMethod: string;
  standardValue: string;
  judgmentRule: string;
}

/** 检验标准表单 */
export interface QualityStandardFormData {
  standardCode: string;
  standardName: string;
  applicableType: number;
  categoryId?: number;
  items: QualityStandardItem[];
}

/** 来料检验 */
export interface IqInspection {
  id: number;
  inspectionNo: string;
  purchaseOrderId: number;
  orderNo: string;
  supplierId: number;
  supplierName: string;
  inspectionResult: number;
  status: number;
  inspectorId: number;
  inspectorName: string;
  inspectionTime: string;
  remark: string;
  items: IqInspectionItem[];
}

/** 来料检验项目 */
export interface IqInspectionItem {
  id?: number;
  iqInspectionId?: number;
  itemName: string;
  inspectionMethod: string;
  standardValue: string;
  actualValue: string;
  judgment: number;
}

/** 来料检验表单 */
export interface IqInspectionFormData {
  purchaseOrderId: number;
  remark: string;
  items: IqInspectionItem[];
}

/** 成品检验 */
export interface OqInspection {
  id: number;
  inspectionNo: string;
  workOrderId: number;
  workOrderNo: string;
  productId: number;
  productName: string;
  quantity: number;
  inspectionResult: number;
  status: number;
  inspectorId: number;
  inspectorName: string;
  inspectionTime: string;
  remark: string;
  items: OqInspectionItem[];
}

/** 成品检验项目 */
export interface OqInspectionItem {
  id?: number;
  oqInspectionId?: number;
  itemName: string;
  inspectionMethod: string;
  standardValue: string;
  actualValue: string;
  judgment: number;
}

/** 成品检验表单 */
export interface OqInspectionFormData {
  workOrderId: number;
  remark: string;
  items: OqInspectionItem[];
}

/** 不合格品记录 */
export interface DefectRecord {
  id: number;
  recordNo: string;
  sourceType: number;
  sourceId: number;
  inspectionNo: string;
  productId: number;
  productName: string;
  quantity: number;
  defectReason: string;
  handleType: number;
  handleRemark: string;
  status: number;
  processInstanceId: string;
  createdAt: string;
}

/** 不合格品记录表单 */
export interface DefectRecordFormData {
  sourceType: number;
  sourceId: number;
  productId: number;
  quantity: number;
  defectReason: string;
  handleType: number;
  handleRemark: string;
}
