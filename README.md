# ERP-OpenSpec

面向中小型工厂的企业级 ERP 系统，覆盖从客户、产品、订单到生产、采购、库存、质量、财务的全流程管理。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 (Composition API) + Element Plus + TypeScript + Vite 5 + Pinia + Axios |
| 后端 | Spring Boot 3.2 + JDK 17 + Spring Security 6 + JWT + MyBatis-Plus + Flowable 7.x |
| 数据库 | MySQL 8.0 + Redis 7 |
| 部署 | Docker + Docker Compose |
| 文档 | SpringDoc (OpenAPI 3) |

## 项目结构

```
ERP-OpenSpec/
├── erp-frontend/                         # 前端项目 (Vue 3)
│   └── src/
│       ├── api/                           #   API 接口层
│       ├── views/                         #   业务页面
│       │   ├── dashboard/                 #     首页仪表盘
│       │   ├── system/                    #     系统管理
│       │   ├── customer/                  #     客户管理
│       │   ├── product/                   #     产品管理
│       │   ├── order/                     #     订单/发货/退货
│       │   ├── inventory/                 #     库存管理
│       │   ├── production/                #     生产管理
│       │   ├── purchase/                  #     采购管理
│       │   ├── quality/                   #     质量管理
│       │   └── finance/                   #     财务管理
│       ├── router/                        #   路由配置
│       ├── types/                         #   TypeScript 类型
│       └── utils/                         #   工具函数
├── erp-backend/                           # 后端项目 (Spring Boot 多模块)
│   ├── erp-common/                        #   公共模块 (BaseEntity, Result, ErrorCode, 工具类)
│   ├── erp-system/                        #   系统管理 (用户/角色/部门/权限/日志)
│   ├── erp-auth/                          #   认证授权 (JWT + Spring Security)
│   ├── erp-customer/                      #   客户管理 (客户/定价)
│   ├── erp-product/                       #   产品管理 (产品/分类/BOM)
│   ├── erp-order/                         #   订单管理 (销售订单/发货/退货)
│   ├── erp-inventory/                     #   库存管理 (仓库/库存/出入库/预警)
│   ├── erp-production/                    #   生产管理 (车间/班组/设备/工艺/计划/工单/报工)
│   ├── erp-purchase/                      #   采购管理 (供应商/采购申请/采购单/入库)
│   ├── erp-quality/                       #   质量管理 (检验标准/IQC/OQC/不合格品处理)
│   ├── erp-finance/                       #   财务管理 (应收/应付)
│   └── erp-boot/                          #   启动模块 (Spring Boot 入口 + BPMN流程)
├── docker/                                # Docker 配置
│   ├── docker-compose.yml
│   ├── .env.example
│   └── mysql/init.sql
├── sql/                                   # 数据库建表脚本 (按迭代版本)
├── docs/                                  # 项目文档
│   ├── development-guide.md
│   └── audit-reports/                     #   安全审计报告
└── openspec/                              # OpenSpec 工作流 (规范驱动开发)
    ├── specs/                             #   主规范 (37个)
    └── changes/archive/                   #   已归档变更
```

## 功能模块

| 模块 | 迭代 | 功能 |
|------|------|------|
| 系统管理 | 初始 | 用户、角色、部门、权限、操作日志 |
| 认证授权 | 初始 | JWT 登录/刷新、Spring Security |
| 客户管理 | 初始 | 客户列表、详情、产品定价 |
| 产品管理 | 初始 | 产品、分类、BOM 物料清单 |
| 订单管理 | 3a/6a | 销售订单、变更审批、发货(部分发货)、退货(审批流)、物流跟踪 |
| 库存管理 | 3a | 仓库、库存查询、出入库、库存预留、预警 |
| 生产管理 | 4a/4b/4c | 车间、班组、设备、工艺路线、生产计划、工单、报工、物料扣减 |
| 采购管理 | 5a | 供应商、采购申请(审批流)、采购单、采购入库、IQC联动 |
| 质量管理 | 5b | 检验标准、IQC来料检验、OQC成品检验、不合格品处理(审批流) |
| 财务管理 | 6b | 应收账款(自动生成+发票+收款核销)、应付账款(自动生成+发票+付款核销) |
| 审批中心 | -- | Flowable 工作流引擎、待办/已办、通过/驳回 |

## 核心业务流

```
客户 → 销售订单 ──→ 生产计划 ──→ 工单 ──→ 报工 ──→ OQC检验 ──→ 成品入库
                      │                      │
                      │                  物料扣减 ←── BOM
                      │                      │
                  采购申请(审批) → 采购单 → 采购入库 ← IQC检验
                      │                      │
                  供应商管理              应付账款 ←── (自动生成)
                                          付款核销

销售发货(部分) → 出库扣库存 → 应收账款(自动生成) → 收款核销 → 物流签收
                                          │
销售退货(审批) → 退货入库 ←── (审批通过后)
```

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- Node.js 18+
- Docker & Docker Compose

### 1. 启动数据库
```bash
cd docker
cp .env.example .env
# 编辑 .env，修改密码和密钥
docker-compose up -d
```

### 2. 启动后端
```bash
cd erp-backend
mvn clean install -DskipTests
java -jar erp-boot/target/erp-boot-1.0.0.jar
```

### 3. 启动前端
```bash
cd erp-frontend
npm install
npm run dev
```

### 4. 访问
| 入口 | 地址 |
|------|------|
| 前端 | http://localhost:3000 |
| API | http://localhost:8080/api |
| Swagger | http://localhost:8080/api/swagger-ui.html |
| Adminer | http://localhost:8080 |

### 5. 默认管理员
- 用户名: `admin`
- 密码: `admin123456`（首次登录请修改）

## 开发指南

详见 [docs/development-guide.md](docs/development-guide.md)

## 审计报告

安全审计报告归档于 [docs/audit-reports/](docs/audit-reports/)

## 许可证

私有项目。未经授权禁止商用。
