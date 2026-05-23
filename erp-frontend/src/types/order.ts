/** 销售订单 */
export interface SalesOrder {
  id: number;
  orderNo: string;
  customerId: number;
  customerName: string;
  customerCode: string;
  status: number;
  totalAmount: number;
  deliveryDate: string;
  deliveryAddress: string;
  contactPhone: string;
  createdAt: string;
  remark: string;
}

/** 订单详情 */
export interface OrderDetail extends SalesOrder {
  items: OrderItem[];
  processInstanceId?: string;
  approvalStatus?: string;
}

/** 订单明细 */
export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  sortOrder: number;
  productName: string;
  productCode: string;
  unit: string;
  spec: string;
}

/** 订单表单 */
export interface OrderFormData {
  customerId: number;
  deliveryDate: string;
  deliveryAddress: string;
  contactPhone: string;
  remark: string;
  items: OrderItemFormData[];
}

/** 订单明细表单 */
export interface OrderItemFormData {
  productId: number;
  quantity: number;
  unitPrice: number;
  sortOrder: number;
}

/** 订单查询参数 */
export interface OrderQuery {
  keyword?: string;
  status?: number;
  customerId?: number;
  startDate?: string;
  endDate?: string;
  pageNum?: number;
  pageSize?: number;
}

/** 订单状态映射 */
export const ORDER_STATUS_MAP: Record<number, { label: string; type: string }> = {
  1: { label: '草稿', type: 'info' },
  2: { label: '已确认', type: '' },
  3: { label: '生产中', type: 'warning' },
  4: { label: '已完成', type: 'success' },
  5: { label: '已关闭', type: 'info' },
  6: { label: '已取消', type: 'danger' },
  7: { label: '已暂停', type: 'warning' },
  8: { label: '变更审批中', type: 'warning' },
};

/** 销售发货单 */
export interface SalesDelivery {
  id: number;
  deliveryNo: string;
  orderId: number;
  orderNo: string;
  customerId: number;
  customerName: string;
  deliveryDate: string;
  logisticsCompany: string;
  trackingNo: string;
  warehouseId: number;
  status: number;
  remark: string;
  createdAt: string;
  items?: SalesDeliveryItem[];
}

/** 发货明细 */
export interface SalesDeliveryItem {
  id: number;
  deliveryId: number;
  productId: number;
  productName: string;
  quantity: number;
}

/** 发货表单 */
export interface SalesDeliveryFormData {
  orderId: number;
  deliveryDate: string;
  logisticsCompany: string;
  trackingNo: string;
  warehouseId: number;
  remark: string;
  items: SalesDeliveryItemForm[];
}

export interface SalesDeliveryItemForm {
  productId: number;
  quantity: number;
}

/** 销售退货单 */
export interface SalesReturn {
  id: number;
  returnNo: string;
  deliveryId: number;
  orderId: number;
  customerId: number;
  customerName: string;
  returnReason: string;
  warehouseId: number;
  status: number;
  processInstanceId: string;
  remark: string;
  createdAt: string;
  items?: SalesReturnItem[];
}

/** 退货明细 */
export interface SalesReturnItem {
  id: number;
  returnId: number;
  productId: number;
  productName: string;
  quantity: number;
  reason: string;
}

/** 退货表单 */
export interface SalesReturnFormData {
  deliveryId: number;
  returnReason: string;
  warehouseId: number;
  remark: string;
  items: SalesReturnItemForm[];
}

export interface SalesReturnItemForm {
  productId: number;
  quantity: number;
  reason: string;
}

/** 发货状态映射 */
export const DELIVERY_STATUS_MAP: Record<number, { label: string; type: string }> = {
  0: { label: '草稿', type: 'info' },
  1: { label: '待出库', type: 'warning' },
  2: { label: '已出库', type: '' },
  3: { label: '已签收', type: 'success' },
};

/** 退货状态映射 */
export const RETURN_STATUS_MAP: Record<number, { label: string; type: string }> = {
  0: { label: '待审批', type: 'info' },
  1: { label: '审批中', type: 'warning' },
  2: { label: '已通过', type: 'success' },
  3: { label: '已驳回', type: 'danger' },
  4: { label: '已入库', type: 'success' },
};
