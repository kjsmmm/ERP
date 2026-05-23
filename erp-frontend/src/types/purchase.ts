/** 供应商 */
export interface Supplier {
  id: number;
  supplierCode: string;
  supplierName: string;
  contactName: string;
  phone: string;
  address: string;
  status: number;
}

/** 供应商表单 */
export interface SupplierFormData {
  supplierCode: string;
  supplierName: string;
  contactName: string;
  phone: string;
  address: string;
}

/** 采购申请 */
export interface PurchaseRequest {
  id: number;
  requestNo: string;
  requestType: number;
  status: number;
  remark: string;
  createdAt: string;
  items: PurchaseRequestItem[];
}

/** 采购申请明细 */
export interface PurchaseRequestItem {
  id?: number;
  purchaseRequestId?: number;
  productId: number;
  productName: string;
  quantity: number;
  unit: string;
  remark: string;
}

/** 采购申请表单 */
export interface PurchaseRequestFormData {
  requestType: number;
  remark: string;
  items: PurchaseRequestItem[];
}

/** 采购单 */
export interface PurchaseOrder {
  id: number;
  orderNo: string;
  supplierId: number;
  supplierName: string;
  purchaseRequestId: number;
  requestNo: string;
  status: number;
  totalAmount: number;
  remark: string;
  createdAt: string;
  items: PurchaseOrderItem[];
}

/** 采购单明细 */
export interface PurchaseOrderItem {
  id?: number;
  purchaseOrderId?: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  amount: number;
  unit: string;
  remark: string;
}

/** 采购单表单 */
export interface PurchaseOrderFormData {
  supplierId: number;
  purchaseRequestId?: number;
  remark: string;
  items: PurchaseOrderItem[];
}

/** 采购入库 */
export interface PurchaseReceipt {
  id: number;
  receiptNo: string;
  purchaseOrderId: number;
  orderNo: string;
  warehouseId: number;
  warehouseName: string;
  status: number;
  inspectionStatus: number;
  remark: string;
  createdAt: string;
  items: PurchaseReceiptItem[];
}

/** 采购入库明细 */
export interface PurchaseReceiptItem {
  id?: number;
  purchaseReceiptId?: number;
  productId: number;
  productName: string;
  quantity: number;
  unit: string;
  remark: string;
}

/** 采购入库表单 */
export interface PurchaseReceiptFormData {
  purchaseOrderId: number;
  warehouseId: number;
  remark: string;
  items: PurchaseReceiptItem[];
}
