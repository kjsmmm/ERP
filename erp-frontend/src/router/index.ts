import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import { useUserStore } from '@/stores/user';

NProgress.configure({ showSpinner: false });

const Layout = () => import('@/layouts/index.vue');

export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true },
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled', affix: true },
      },
    ],
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User', permission: 'system:user' },
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled', permission: 'system:role' },
      },
      {
        path: 'dept',
        name: 'SystemDept',
        component: () => import('@/views/system/dept/index.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding', permission: 'system:dept' },
      },
      {
        path: 'permission',
        name: 'SystemPermission',
        component: () => import('@/views/system/permission/index.vue'),
        meta: { title: '权限管理', icon: 'Lock', permission: 'system:permission' },
      },
      {
        path: 'log',
        name: 'SystemLog',
        component: () => import('@/views/system/log/index.vue'),
        meta: { title: '操作日志', icon: 'Document', permission: 'system:log' },
      },
    ],
  },
  {
    path: '/customer',
    component: Layout,
    redirect: '/customer/list',
    meta: { title: '客户管理', icon: 'User' },
    children: [
      {
        path: 'list',
        name: 'CustomerList',
        component: () => import('@/views/customer/list/index.vue'),
        meta: { title: '客户列表', icon: 'List', permission: 'customer:view' },
      },
      {
        path: 'detail/:id',
        name: 'CustomerDetail',
        component: () => import('@/views/customer/detail/index.vue'),
        meta: { title: '客户详情', icon: 'View', permission: 'customer:view', hidden: true, activeMenu: '/customer/list' },
      },
      {
        path: 'price/:id',
        name: 'CustomerPrice',
        component: () => import('@/views/customer/price/index.vue'),
        meta: { title: '客户定价', icon: 'PriceTag', permission: 'customer:view', hidden: true, activeMenu: '/customer/list' },
      },
    ],
  },
  {
    path: '/product',
    component: Layout,
    redirect: '/product/list',
    meta: { title: '产品管理', icon: 'Goods' },
    children: [
      {
        path: 'list',
        name: 'ProductList',
        component: () => import('@/views/product/list/index.vue'),
        meta: { title: '产品列表', icon: 'List', permission: 'product:view' },
      },
      {
        path: 'detail/:id',
        name: 'ProductDetail',
        component: () => import('@/views/product/detail/index.vue'),
        meta: { title: '产品详情', icon: 'View', permission: 'product:view', hidden: true, activeMenu: '/product/list' },
      },
      {
        path: 'category',
        name: 'ProductCategory',
        component: () => import('@/views/product/category/index.vue'),
        meta: { title: '分类管理', icon: 'Tree', permission: 'product:category' },
      },
    ],
  },
  {
    path: '/order',
    component: Layout,
    redirect: '/order/list',
    meta: { title: '订单管理', icon: 'Document' },
    children: [
      {
        path: 'list',
        name: 'OrderList',
        component: () => import('@/views/order/list/index.vue'),
        meta: { title: '订单列表', icon: 'List', permission: 'order:view' },
      },
      {
        path: 'detail/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/detail/index.vue'),
        meta: { title: '订单详情', icon: 'View', permission: 'order:view', hidden: true, activeMenu: '/order/list' },
      },
      {
        path: 'delivery',
        name: 'SalesDelivery',
        component: () => import('@/views/order/delivery/index.vue'),
        meta: { title: '销售发货', icon: 'Van', permission: 'order:delivery' },
      },
      {
        path: 'return',
        name: 'SalesReturn',
        component: () => import('@/views/order/return/index.vue'),
        meta: { title: '销售退货', icon: 'RefreshLeft', permission: 'order:return' },
      },
    ],
  },
  {
    path: '/inventory',
    component: Layout,
    redirect: '/inventory/stock',
    meta: { title: '库存管理', icon: 'Box' },
    children: [
      {
        path: 'stock',
        name: 'InventoryStock',
        component: () => import('@/views/inventory/stock/index.vue'),
        meta: { title: '库存查询', icon: 'Search', permission: 'inventory:view' },
      },
      {
        path: 'warehouse',
        name: 'InventoryWarehouse',
        component: () => import('@/views/inventory/warehouse/index.vue'),
        meta: { title: '仓库管理', icon: 'House', permission: 'inventory:warehouse' },
      },
      {
        path: 'inbound',
        name: 'InventoryInbound',
        component: () => import('@/views/inventory/inbound/index.vue'),
        meta: { title: '入库管理', icon: 'Bottom', permission: 'inventory:stock' },
      },
      {
        path: 'outbound',
        name: 'InventoryOutbound',
        component: () => import('@/views/inventory/outbound/index.vue'),
        meta: { title: '出库管理', icon: 'Top', permission: 'inventory:stock' },
      },
    ],
  },
  {
    path: '/production',
    component: Layout,
    redirect: '/production/workshop',
    meta: { title: '生产管理', icon: 'SetUp' },
    children: [
      {
        path: 'workshop',
        name: 'ProductionWorkshop',
        component: () => import('@/views/production/workshop/index.vue'),
        meta: { title: '车间管理', icon: 'OfficeBuilding', permission: 'production:workshop' },
      },
      {
        path: 'team',
        name: 'ProductionTeam',
        component: () => import('@/views/production/team/index.vue'),
        meta: { title: '班组管理', icon: 'UserFilled', permission: 'production:team' },
      },
      {
        path: 'equipment-type',
        name: 'EquipmentType',
        component: () => import('@/views/production/equipment-type/index.vue'),
        meta: { title: '设备类型', icon: 'Grid', permission: 'production:equipment' },
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/production/equipment/index.vue'),
        meta: { title: '设备管理', icon: 'Monitor', permission: 'production:equipment' },
      },
      {
        path: 'plan',
        name: 'ProductionPlan',
        component: () => import('@/views/production/plan/index.vue'),
        meta: { title: '生产计划', icon: 'Calendar', permission: 'production:plan' },
      },
      {
        path: 'work-order',
        name: 'WorkOrder',
        component: () => import('@/views/production/work-order/index.vue'),
        meta: { title: '工单管理', icon: 'Tickets', permission: 'production:workorder' },
      },
    ],
  },
  {
    path: '/purchase',
    component: Layout,
    redirect: '/purchase/supplier',
    meta: { title: '采购管理', icon: 'ShoppingCart' },
    children: [
      {
        path: 'supplier',
        name: 'PurchaseSupplier',
        component: () => import('@/views/purchase/supplier/index.vue'),
        meta: { title: '供应商管理', icon: 'User', permission: 'purchase:supplier' },
      },
      {
        path: 'request',
        name: 'PurchaseRequest',
        component: () => import('@/views/purchase/request/index.vue'),
        meta: { title: '采购申请', icon: 'Document', permission: 'purchase:request' },
      },
      {
        path: 'order',
        name: 'PurchaseOrder',
        component: () => import('@/views/purchase/order/index.vue'),
        meta: { title: '采购单', icon: 'Tickets', permission: 'purchase:order' },
      },
      {
        path: 'receipt',
        name: 'PurchaseReceipt',
        component: () => import('@/views/purchase/receipt/index.vue'),
        meta: { title: '采购入库', icon: 'Bottom', permission: 'purchase:receipt' },
      },
    ],
  },
  {
    path: '/quality',
    component: Layout,
    redirect: '/quality/standard',
    meta: { title: '质量管理', icon: 'CircleCheck' },
    children: [
      {
        path: 'standard',
        name: 'QualityStandard',
        component: () => import('@/views/quality/standard/index.vue'),
        meta: { title: '检验标准', icon: 'Document', permission: 'quality:standard' },
      },
      {
        path: 'iqc',
        name: 'QualityIqc',
        component: () => import('@/views/quality/iqc/index.vue'),
        meta: { title: '来料检验', icon: 'Search', permission: 'quality:iqc' },
      },
      {
        path: 'oqc',
        name: 'QualityOqc',
        component: () => import('@/views/quality/oqc/index.vue'),
        meta: { title: '成品检验', icon: 'Finished', permission: 'quality:oqc' },
      },
      {
        path: 'defect',
        name: 'QualityDefect',
        component: () => import('@/views/quality/defect/index.vue'),
        meta: { title: '不合格品处理', icon: 'Warning', permission: 'quality:defect' },
      },
    ],
  },
  {
  {
    path: '/finance',
    component: Layout,
    redirect: '/finance/ar',
    meta: { title: '财务管理', icon: 'Money' },
    children: [
      {
        path: 'ar',
        name: 'FinanceAR',
        component: () => import('@/views/finance/ar/index.vue'),
        meta: { title: '应收账款', icon: 'Upload', permission: 'finance:ar' },
      },
      {
        path: 'ap',
        name: 'FinanceAP',
        component: () => import('@/views/finance/ap/index.vue'),
        meta: { title: '应付账款', icon: 'Download', permission: 'finance:ap' },
      },
    ],
  },
  {
    path: '/workflow',
    component: Layout,
    redirect: '/workflow/todo',
    meta: { title: '审批中心', icon: 'Stamp' },
    children: [
      {
        path: 'todo',
        name: 'WorkflowTodo',
        component: () => import('@/views/workflow/todo/index.vue'),
        meta: { title: '我的待办', icon: 'Bell', permission: 'workflow:view' },
      },
    ],
  },
  {
    path: '/profile',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'User', hidden: true },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
});

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  NProgress.start();

  const userStore = useUserStore();

  if (to.path === '/login') {
    if (userStore.isLoggedIn) {
      next('/');
    } else {
      next();
    }
    return;
  }

  if (!userStore.isLoggedIn) {
    next(`/login?redirect=${to.path}`);
    return;
  }

  // 已登录但未获取用户信息
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo();
      next({ ...to, replace: true });
    } catch {
      userStore.clearAuth();
      next(`/login?redirect=${to.path}`);
    }
    return;
  }

  next();
});

router.afterEach(() => {
  NProgress.done();
});

export default router;
