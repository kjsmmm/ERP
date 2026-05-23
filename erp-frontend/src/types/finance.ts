/** 应收单 */
export interface ArRecord {
  id: number;
  receivableNo: string;
  deliveryId: number;
  orderId: number;
  customerId: number;
  customerName: string;
  amount: number;
  paidAmount: number;
  invoiceNo: string;
  invoiceDate: string;
  invoiceStatus: number;
  status: number;
  remark: string;
  createdAt: string;
}

/** 收款记录 */
export interface ArPayment {
  id: number;
  paymentNo: string;
  arId: number;
  amount: number;
  paymentMethod: string;
  paymentDate: string;
  remark: string;
}

/** 应付单 */
export interface ApRecord {
  id: number;
  payableNo: string;
  receiptId: number;
  purchaseOrderId: number;
  supplierId: number;
  supplierName: string;
  amount: number;
  paidAmount: number;
  invoiceNo: string;
  invoiceDate: string;
  invoiceStatus: number;
  status: number;
  remark: string;
  createdAt: string;
}

/** 付款记录 */
export interface ApPayment {
  id: number;
  paymentNo: string;
  apId: number;
  amount: number;
  paymentMethod: string;
  paymentDate: string;
  remark: string;
}

/** 应收/应付状态 */
export const AR_STATUS_MAP: Record<number, { label: string; type: string }> = {
  0: { label: '未收', type: 'danger' },
  1: { label: '部分收', type: 'warning' },
  2: { label: '已收', type: 'success' },
};

export const AP_STATUS_MAP: Record<number, { label: string; type: string }> = {
  0: { label: '未付', type: 'danger' },
  1: { label: '部分付', type: 'warning' },
  2: { label: '已付', type: 'success' },
};

export const INVOICE_STATUS_MAP: Record<number, { label: string; type: string }> = {
  0: { label: '未开票', type: 'warning' },
  1: { label: '已开票', type: 'success' },
};
