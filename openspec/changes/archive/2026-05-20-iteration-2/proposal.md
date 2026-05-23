## Why

迭代 0（基础设施）和迭代 1（认证权限 + 客户管理）已完成。产品管理是制造业 ERP 的核心模块，直接影响后续的订单管理、生产管理、采购管理和成本核算。BOM（物料清单）是连接产品与生产的桥梁，没有它无法进行生产计划和物料需求计算。

## What Changes

- 新增**产品管理**模块：产品 CRUD、产品分类管理、产品图片多图上传、标准售价/成本管理
- 新增 **BOM 物料清单**管理：支持树形 BOM 结构（成品 → 半成品 → 原材料），含用量和损耗率
- 新增**文件上传**能力：通用图片上传接口，存储到本地 uploads 目录
- 新增 4 张数据库表：`product_category`、`product`、`product_image`、`bom_item`
- 新增后端模块 `erp-product`
- 新增前端页面：产品列表、产品详情（含图片和 BOM Tab）、分类管理

## Capabilities

### New Capabilities

- `product-management`: 产品全生命周期管理，包括产品的增删改查、分类管理、多图上传、启用/停用状态管理、按分类和关键词搜索
- `bom-management`: BOM 物料清单管理，支持为半成品和成品定义子项物料（含用量、损耗率），支持递归展开完整 BOM 树，含环检测校验

### Modified Capabilities

（无已有能力的变更）

## Impact

- **后端**：新增 `erp-product` Maven 模块，依赖 `erp-common` 和 `erp-system`
- **前端**：新增 `/product` 路由组，包含产品列表、产品详情、分类管理三个页面
- **数据库**：新增 4 张表（product_category、product、product_image、bom_item）
- **基础设施**：在 `erp-common` 中新增文件上传工具类和配置，Nginx 需配置 `/uploads/` 静态资源路径
- **API**：新增约 15 个 REST 接口（产品 CRUD、图片管理、BOM 管理、分类管理）
