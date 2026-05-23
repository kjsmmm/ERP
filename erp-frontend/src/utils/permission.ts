import type { App, Directive, DirectiveBinding } from 'vue';
import { useUserStore } from '@/stores/user';

/** v-permission 指令：根据权限码控制元素显示 */
const permissionDirective: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
    const userStore = useUserStore();
    const value = binding.value;

    if (!value) return;

    const permissions = Array.isArray(value) ? value : [value];
    const hasPermission = permissions.some((perm) => userStore.hasPermission(perm));

    if (!hasPermission) {
      el.parentNode?.removeChild(el);
    }
  },
};

export function setupPermissionDirective(app: App) {
  app.directive('permission', permissionDirective);
}
