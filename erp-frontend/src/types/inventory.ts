/** 仓库 */
export interface Warehouse {
  id: number;
  warehouseCode: string;
  warehouseName: string;
  address: string;
  manager: string;
  phone: string;
  status: number;
  remark: string;
}

/** 仓库表单 */
export interface WarehouseFormData {
  warehouseCode: string;
  warehouseName: string;
  address: string;
  manager: string;
  phone: string;
  remark: string;
}

/** 库存记录 */
export interface InventoryItem {
  id: number;
  productId: number;
  warehouseId: number;
  onHandQty: number;
  reservedQty: number;
  availableQty: number;
  safetyStock: number;
  productName: string;
  productCode: string;
  unit: string;
  warehouseName: string;
}

/** 库存查询参数 */
export interface InventoryQuery {
  productId?: number;
  warehouseId?: number;
  keyword?: string;
  pageNum?: number;
  pageSize?: number;
}

/** 入库表单 */
export interface StockInFormData {
  productId: number;
  warehouseId: number;
  quantity: number;
  referenceNo: string;
  remark: string;
}

/** 出库表单 */
export interface StockOutFormData {
  productId: number;
  warehouseId: number;
  quantity: number;
  referenceNo: string;
  remark: string;
}
