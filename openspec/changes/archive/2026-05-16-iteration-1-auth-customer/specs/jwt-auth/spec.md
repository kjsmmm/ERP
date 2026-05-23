## MODIFIED Requirements

### Requirement: User authentication with JWT

The system SHALL authenticate users using JWT (JSON Web Token) mechanism. User permissions SHALL be loaded from the database through role-permission association, not hardcoded.

#### Scenario: Successful login

- **WHEN** user submits valid username and password
- **THEN** system verifies credentials against database
- **AND** password is verified using BCrypt hash
- **AND** system loads user's roles from `sys_user_role` table
- **AND** system loads permissions from `sys_role_permission` → `sys_permission` tables
- **AND** system generates Access Token (15 minutes expiration)
- **AND** system generates Refresh Token (7 days expiration)
- **AND** both tokens are stored in Redis for active session tracking
- **AND** tokens are returned to client in response

#### Scenario: Load user permissions from database

- **WHEN** `UserDetailsServiceImpl.loadUserByUsername()` is called
- **THEN** system queries `sys_user` by username
- **AND** system queries user's role codes from `sys_user_role` → `sys_role`
- **AND** system queries user's permission codes from `sys_role_permission` → `sys_permission`
- **AND** constructs `UserDetails` with authorities = role codes + permission codes
- **AND** authorities are prefixed: roles as `ROLE_xxx`, permissions as raw perm_code

#### Scenario: Invalid credentials

- **WHEN** user submits invalid username or password
- **THEN** system returns 401 Unauthorized
- **AND** error message indicates "Invalid username or password"
- **AND** failed attempt is logged

#### Scenario: Account lockout

- **WHEN** user fails to login 5 consecutive times
- **THEN** account is locked for 30 minutes
- **AND** subsequent login attempts return 403 Forbidden
- **AND** error message indicates "Account locked, try again later"

### Requirement: Role-based access control

The system SHALL enforce role-based access control using permissions loaded from database.

#### Scenario: Method-level authorization

- **WHEN** request is made to a controller method annotated with `@PreAuthorize`
- **THEN** system checks user's authorities from `SecurityContextHolder`
- **AND** if user has required permission, request proceeds
- **AND** if user lacks permission, 403 Forbidden is returned

#### Scenario: Permission check

- **WHEN** controller method has `@PreAuthorize("hasAuthority('system:user:add')")`
- **THEN** user must have `system:user:add` in their permission list
- **AND** permission is loaded from database (not hardcoded)
