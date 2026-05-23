## ADDED Requirements

### Requirement: User management

The system SHALL provide CRUD operations for user management.

#### Scenario: Create user

- **WHEN** administrator submits user creation request
- **THEN** system validates required fields (username, password, name, email)
- **AND** system checks username uniqueness
- **AND** password is hashed using BCrypt
- **AND** user record is created in `sys_user` table
- **AND** user ID is returned in response

#### Scenario: Query user list

- **WHEN** administrator requests user list with pagination
- **THEN** system returns paginated user list
- **AND** supports filtering by username, name, status, department
- **AND** supports sorting by creation time, username
- **AND** password field is excluded from response

#### Scenario: Update user

- **WHEN** administrator submits user update request
- **THEN** system validates required fields
- **AND** system checks username uniqueness (if changed)
- **AND** user record is updated in database
- **AND** updated user information is returned

#### Scenario: Delete user

- **WHEN** administrator submits user deletion request
- **THEN** system performs logical deletion (sets `deleted` flag)
- **AND** user cannot login after deletion
- **AND** user data is retained for audit purposes

#### Scenario: Reset user password

- **WHEN** administrator resets user password
- **THEN** system generates new temporary password
- **AND** password is hashed and stored
- **AND** user is required to change password on next login

#### Scenario: Enable/disable user

- **WHEN** administrator changes user status
- **THEN** user status is updated in database
- **AND** disabled users cannot login
- **AND** enabled users can login normally

### Requirement: Role management

The system SHALL provide CRUD operations for role management.

#### Scenario: Create role

- **WHEN** administrator submits role creation request
- **THEN** system validates required fields (role name, role code)
- **AND** system checks role code uniqueness
- **AND** role record is created in `sys_role` table
- **AND** role ID is returned in response

#### Scenario: Query role list

- **WHEN** administrator requests role list
- **THEN** system returns all roles
- **AND** supports filtering by role name, status
- **AND** includes role permission count

#### Scenario: Update role

- **WHEN** administrator submits role update request
- **THEN** system validates required fields
- **AND** system checks role code uniqueness (if changed)
- **AND** role record is updated in database

#### Scenario: Delete role

- **WHEN** administrator submits role deletion request
- **THEN** system checks if role is assigned to any users
- **AND** if assigned, deletion is rejected with error message
- **AND** if not assigned, role is logically deleted

#### Scenario: Assign permissions to role

- **WHEN** administrator assigns permissions to role
- **THEN** system validates permission IDs exist
- **AND** existing role-permission mappings are replaced
- **AND** new mappings are created in `sys_role_permission` table

### Requirement: Department management

The system SHALL provide CRUD operations for department management.

#### Scenario: Create department

- **WHEN** administrator submits department creation request
- **THEN** system validates required fields (department name)
- **AND** system supports parent department (tree structure)
- **AND** department record is created in `sys_dept` table
- **AND** department ID is returned in response

#### Scenario: Query department tree

- **WHEN** administrator requests department list
- **THEN** system returns department tree structure
- **AND** tree is built based on parent_id relationships
- **AND** includes department name, code, sort order, status

#### Scenario: Update department

- **WHEN** administrator submits department update request
- **THEN** system validates required fields
- **AND** department record is updated in database
- **AND** parent department can be changed (with validation to prevent circular reference)

#### Scenario: Delete department

- **WHEN** administrator submits department deletion request
- **THEN** system checks if department has child departments
- **AND** system checks if department has assigned users
- **AND** if has children or users, deletion is rejected with error message
- **AND** if no children or users, department is logically deleted

### Requirement: Operation log

The system SHALL record operation logs for audit purposes.

#### Scenario: Record operation log

- **WHEN** user performs create, update, or delete operation
- **THEN** system records operation log including:
  - Operation type (CREATE, UPDATE, DELETE)
  - Operator ID and username
  - Operation time
  - Request method and URL
  - Request parameters
  - Response result
  - IP address
  - Execution time

#### Scenario: Query operation logs

- **WHEN** administrator requests operation log list
- **THEN** system returns paginated log list
- **AND** supports filtering by operator, operation type, time range
- **AND** supports sorting by operation time

#### Scenario: Log retention

- **WHEN** operation logs are older than 1 year
- **THEN** logs can be archived or deleted
- **AND** retention policy is configurable

### Requirement: Permission management

The system SHALL manage permissions for RBAC (Role-Based Access Control).

#### Scenario: Permission structure

- **WHEN** system defines permissions
- **THEN** permissions include:
  - Menu permissions (page access)
  - Button permissions (operation access)
  - Data permissions (data scope)
- **AND** permissions are hierarchical (menu contains buttons)

#### Scenario: Query permission list

- **WHEN** administrator requests permission list
- **THEN** system returns permission tree structure
- **AND** includes menu items and button permissions
- **AND** shows permission name, code, type, status

#### Scenario: User permission aggregation

- **WHEN** system checks user permissions
- **THEN** system aggregates permissions from all assigned roles
- **AND** duplicate permissions are merged
- **AND** effective permissions are cached in Redis for performance

### Requirement: Data scope

The system SHALL support data scope control for different roles.

#### Scenario: Data scope levels

- **WHEN** role is assigned data scope
- **THEN** data scope levels include:
  - All data (admin level)
  - Department and sub-department data
  - Department data only
  - Self-created data only

#### Scenario: Data scope enforcement

- **WHEN** user queries data with data scope filter
- **THEN** system automatically adds data scope conditions to SQL
- **AND** user can only see data permitted by their role's data scope
- **AND** data scope is applied transparently (no manual filtering needed)
