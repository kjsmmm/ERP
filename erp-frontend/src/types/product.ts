/** 产品 */
export interface Product {
  id: number;
  productCode: string;
  productName: string;
  categoryId: number;
  productType: number;
  spec: string;
  unit: string;
  weight: number;
  standardCost: number;
  standardPrice: number;
  status: number;
  createdAt: string;
  remark: string;
}

/** 产品详情（含图片+BOM） */
export interface ProductDetail extends Product {
  categoryName: string;
  images: ProductImage[];
  bomItems: BomItem[];
}

/** 产品表单 */
export interface ProductFormData {
  id?: number;
  productCode: string;
  productName: string;
  categoryId: number;
  productType: number;
  spec: string;
  unit: string;
  weight: number;
  standardCost: number;
  standardPrice: number;
  remark: string;
}

/** 产品查询参数 */
export interface ProductQuery {
  keyword?: string;
  categoryId?: number;
  productType?: number;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

/** 产品分类 */
export interface ProductCategory {
  id: number;
  name: string;
  parentId: number;
  sortOrder: number;
  status: number;
  children?: ProductCategory[];
}

/** 分类表单 */
export interface CategoryFormData {
  id?: number;
  name: string;
  parentId: number;
  sortOrder: number;
  remark: string;
}

/** 产品图片 */
export interface ProductImage {
  id: number;
  productId: number;
  imageUrl: string;
  sortOrder: number;
  isPrimary: number;
  createdAt: string;
}

/** BOM项 */
export interface BomItem {
  id: number;
  productId: number;
  materialId: number;
  quantity: number;
  wasteRate: number;
  sortOrder: number;
  materialName: string;
  materialCode: string;
  materialUnit: string;
  materialSpec: string;
  materialType: number;
}

/** BOM项表单 */
export interface BomItemFormData {
  materialId: number;
  quantity: number;
  wasteRate: number;
  sortOrder: number;
}
