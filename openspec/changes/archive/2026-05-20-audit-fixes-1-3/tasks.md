## 1. 高危修复

- [x] 1.1 SysUser.password 加 @JsonIgnore，隐藏密码哈希和安全字段
- [x] 1.2 UserController.changePassword 加 @PreAuthorize
- [x] 1.3 RedisConfig 反序列化改用 BasicPolymorphicTypeValidator 白名单
- [x] 1.4 JWT Secret 改用环境变量，移除硬编码默认值
- [x] 1.5 修复客户搜索 .or() 逻辑，改用嵌套条件
- [x] 1.6 Service 层 RuntimeException 改为 BusinessException
- [x] 1.7 删除产品时级联清理关联图片记录和磁盘文件
- [x] 1.8 BOM 环检测覆盖新提交批次内部的循环引用

## 2. 中危修复

- [x] 2.1 PasswordUtils 改用 SecureRandom
- [x] 2.2 Refresh Token 改为 @RequestBody 传递
- [x] 2.3 LogAspect 设置 operatorId
- [x] 2.4 QueryDTO pageSize 加 @Max 上限（100）
- [x] 2.5 FileUploadUtils.delete 加路径校验防止穿越
- [x] 2.6 FileUploadUtils 加文件扩展名白名单
- [x] 2.7 application.yml type-aliases-package 扫描多模块
- [x] 2.8 BomItemDTO.quantity 加 @DecimalMin 正数校验
- [x] 2.9 客户编码生成改用数据库查询当日最大序号
- [x] 2.10 删除客户时检查关联联系人/跟进记录
- [x] 2.11 前端 BOM 表单验证回调检查 valid 参数
- [x] 2.12 前端分类编辑排除自身和后代防止循环引用

## 3. 低危修复

- [x] 3.1 Docker 端口统一为 3306
- [x] 3.2 .env.example JWT Secret 加长到 32+ 字节
- [x] 3.3 AuthController login/logout 加 @Log
- [x] 3.4 CustomerFollow transient 改为 @TableField(exist=false)
- [x] 3.5 权限删除确认文案修正
- [x] 3.6 前端状态切换失败回滚 UI
- [x] 3.7 前端分类下拉扁平化显示所有层级
