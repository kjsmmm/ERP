## ADDED Requirements

### Requirement: Permission management API

The system SHALL provide API endpoints for permission tree management.

#### Scenario: Query permission tree

- **WHEN** administrator requests permission list
- **THEN** system returns permission tree structure
- **AND** tree is built based on parent_id relationships
- **AND** includes permission name, code, type (目录/菜单/按钮), path, icon, sort_order
- **AND** root nodes have parent_id = 0

#### Scenario: Create permission

- **WHEN** administrator submits permission creation request
- **THEN** system validates required fields (perm_name, perm_code, perm_type)
- **AND** system checks perm_code uniqueness
- **AND** permission record is created in `sys_permission` table

#### Scenario: Update permission

- **WHEN** administrator submits permission update request
- **THEN** system validates required fields
- **AND** system checks perm_code uniqueness (if changed, excluding self)
- **AND** permission record is updated

#### Scenario: Delete permission

- **WHEN** administrator submits permission deletion request
- **THEN** system checks if permission has child permissions
- **AND** if has children, deletion is rejected
- **AND** if no children, permission is deleted and role-permission mappings are cleaned up

### Requirement: User management frontend

The system SHALL provide a user management page with full CRUD operations.

#### Scenario: User list page

- **WHEN** administrator navigates to user management
- **THEN** system displays user list table with columns: username, nickname, department, status, created_at
- **AND** table supports pagination
- **AND** search area supports filtering by username, real name, status, department
- **AND** action buttons include: Add User, Export

#### Scenario: Add/edit user dialog

- **WHEN** administrator clicks add or edit button
- **THEN** system shows dialog with form fields: username, password (add only), nickname, realName, email, phone, gender, department, roles, remark
- **AND** form validates required fields before submission
- **AND** on success, dialog closes and list refreshes

#### Scenario: Reset password

- **WHEN** administrator clicks reset password for a user
- **THEN** system shows confirmation dialog
- **AND** on confirm, system calls reset password API
- **AND** system shows the new temporary password to administrator

#### Scenario: Enable/disable user

- **WHEN** administrator toggles user status
- **THEN** system calls change status API
- **AND** status switch updates immediately

### Requirement: Role management frontend

The system SHALL provide a role management page with permission assignment.

#### Scenario: Role list page

- **WHEN** administrator navigates to role management
- **THEN** system displays role list table with columns: role_name, role_code, status, sort_order
- **AND** action buttons include: Add Role

#### Scenario: Assign permissions to role

- **WHEN** administrator clicks assign permissions for a role
- **THEN** system shows permission tree with checkboxes
- **AND** currently assigned permissions are pre-checked
- **AND** on save, system calls assign permissions API with selected permission IDs

### Requirement: Department management frontend

The system SHALL provide a department management page with tree structure.

#### Scenario: Department tree page

- **WHEN** administrator navigates to department management
- **THEN** system displays department tree on the left side
- **AND** clicking a node shows department details on the right side
- **AND** action buttons include: Add Department, Edit, Delete

### Requirement: Permission management frontend

The system SHALL provide a permission management page.

#### Scenario: Permission tree page

- **WHEN** administrator navigates to permission management
- **THEN** system displays permission tree structure
- **AND** each node shows permission name, code, type
- **AND** action buttons include: Add Permission, Edit, Delete

### Requirement: Operation log frontend

The system SHALL provide an operation log page.

#### Scenario: Log list page

- **WHEN** administrator navigates to operation log
- **THEN** system displays log list table with columns: module, operation, operator, IP, status, time
- **AND** table supports pagination
- **AND** search area supports filtering by module, operation, operator, status, time range
- **AND** clicking a row shows full log details in dialog

### Requirement: Personal center

The system SHALL provide a personal center page for the current user.

#### Scenario: View personal info

- **WHEN** user navigates to personal center
- **THEN** system displays current user's info: username, nickname, realName, email, phone, avatar, department, roles
- **AND** info is read-only except for editable fields

#### Scenario: Change password

- **WHEN** user submits password change request
- **THEN** system validates old password
- **AND** system validates new password meets complexity requirements
- **AND** password is updated and user is prompted to re-login

### Requirement: Audit logging on write operations

All Controller write operations (create, update, delete) SHALL be annotated with `@Log` for audit tracking.

#### Scenario: Operation log recording

- **WHEN** user performs create, update, or delete on any Controller
- **THEN** `@Log` annotation triggers `LogAspect`
- **AND** operation log is recorded with module name, operation description, request details, operator info, execution time
