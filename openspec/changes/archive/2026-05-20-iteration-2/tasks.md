## 1. 数据库

- [x] 1.1 创建 product_category 表 DDL
- [x] 1.2 创建 product 表 DDL
- [x] 1.3 创建 product_image 表 DDL
- [x] 1.4 创建 bom_item 表 DDL
- [x] 1.5 插入初始产品分类数据
- [x] 1.6 插入产品管理相关权限数据（product:view, product:add, product:edit, product:delete）

## 2. 后端模块搭建

- [x] 2.1 创建 erp-product Maven 模块 pom.xml，依赖 erp-common 和 erp-system
- [x] 2.2 在 erp-backend/pom.xml 中注册 erp-product 子模块
- [x] 2.3 在 erp-boot/pom.xml 中添加 erp-product 依赖

## 3. 产品分类管理（后端）

- [x] 3.1 创建 ProductCategory 实体类
- [x] 3.2 创建 CategoryDTO 请求对象
- [x] 3.3 创建 CategoryMapper 接口
- [x] 3.4 创建 CategoryService 接口和实现（CRUD + 树形查询）
- [x] 3.5 创建 CategoryController（GET /product/category/tree, POST, PUT /{id}, DELETE /{id}）

## 4. 产品管理（后端）

- [x] 4.1 创建 ProductType 枚举（原材料/半成品/成品）
- [x] 4.2 创建 Product 实体类
- [x] 4.3 创建 ProductDTO、ProductQueryDTO 请求对象
- [x] 4.4 创建 ProductDetailVO（产品详情，含图片列表和 BOM 列表）
- [x] 4.5 创建 ProductMapper 接口
- [x] 4.6 创建 ProductService 接口和实现（CRUD + 分页查询 + 启停用）
- [x] 4.7 创建 ProductController（GET /product/page, GET /{id}, POST, PUT /{id}, DELETE /{id}, PUT /{id}/status）
- [x] 4.8 实现删除产品时校验是否被 BOM 引用的逻辑

## 5. 文件上传（基础设施）

- [x] 5.1 在 erp-common 中添加文件上传配置类（UploadProperties：路径、大小限制、允许类型）
- [x] 5.2 在 erp-common 中添加 FileUploadUtils 工具类
- [x] 5.3 在 application.yml 中添加上传配置项
- [x] 5.4 在 docker/nginx 配置中添加 /uploads/ 静态资源路径

## 6. 产品图片管理（后端）

- [x] 6.1 创建 ProductImage 实体类
- [x] 6.2 创建 ProductImageMapper 接口
- [x] 6.3 创建 ProductImageService 接口和实现（上传、列表、设主图、删除）
- [x] 6.4 创建 ProductImageController（POST /product/{id}/images, GET /product/{id}/images, PUT /product/image/{id}/primary, DELETE /product/image/{id}）

## 7. BOM 管理（后端）

- [x] 7.1 创建 BomItem 实体类
- [x] 7.2 创建 BomItemDTO 请求对象
- [x] 7.3 创建 BomItemMapper 接口（含递归 CTE 查询方法）
- [x] 7.4 编写 BomItemMapper.xml（递归展开 BOM 的 SQL）
- [x] 7.5 创建 BomService 接口和实现（获取 BOM、整体替换更新、递归展开、环检测）
- [x] 7.6 创建 BomController（GET /product/{id}/bom, PUT /product/{id}/bom, GET /product/{id}/bom/expand）

## 8. 前端类型和 API

- [x] 8.1 创建 src/types/product.ts（Product, ProductDetail, ProductFormData, ProductQuery, ProductCategory, ProductImage, BomItem 类型定义）
- [x] 8.2 创建 src/api/product/product.ts（产品 CRUD API）
- [x] 8.3 创建 src/api/product/category.ts（分类管理 API）
- [x] 8.4 创建 src/api/product/image.ts（图片管理 API）
- [x] 8.5 创建 src/api/product/bom.ts（BOM 管理 API）

## 9. 前端产品列表页

- [x] 9.1 创建 src/views/product/list/index.vue（搜索区 + 表格 + 分页 + CRUD 弹窗）
- [x] 9.2 实现搜索筛选（关键词、分类、产品类型、状态）
- [x] 9.3 实现新增/编辑产品弹窗（含分类选择、产品类型选择）
- [x] 9.4 实现删除确认和启停用切换

## 10. 前端产品详情页

- [x] 10.1 创建 src/views/product/detail/index.vue（el-page-header + el-tabs 骨架）
- [x] 10.2 实现基本信息 Tab（el-descriptions 展示产品信息）
- [x] 10.3 实现图片管理 Tab（图片列表 + 上传 + 设主图 + 删除）
- [x] 10.4 实现 BOM 管理 Tab（树形表格 + 增删子项 + 编辑用量/损耗率）

## 11. 前端分类管理页

- [x] 11.1 创建 src/views/product/category/index.vue（树形表格 + CRUD 弹窗）

## 12. 路由和权限配置

- [x] 12.1 在 src/router/index.ts 中添加 /product 路由组（产品列表、产品详情、分类管理）
- [x] 12.2 配置路由 meta（title, icon, permission, hidden, activeMenu）
