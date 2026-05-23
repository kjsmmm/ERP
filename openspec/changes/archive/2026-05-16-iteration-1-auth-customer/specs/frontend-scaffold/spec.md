## ADDED Requirements

### Requirement: Admin layout

The system SHALL provide a standard admin layout with sidebar navigation, header, and content area.

#### Scenario: Layout structure

- **WHEN** user accesses any authenticated page
- **THEN** system displays sidebar on the left with navigation menu
- **AND** system displays header at the top with breadcrumb and user info
- **AND** system displays content area in the center with router view
- **AND** layout is responsive and supports collapse/expand sidebar

#### Scenario: Dynamic menu

- **WHEN** user logs in with assigned permissions
- **THEN** sidebar menu items are generated based on user's menu permissions
- **AND** menu items user has no permission for are hidden
- **AND** menu supports multi-level nesting (directory → menu → button)

### Requirement: Frontend routing

The system SHALL implement client-side routing with permission guards.

#### Scenario: Route definition

- **WHEN** application starts
- **THEN** routes are registered for all modules:
  - `/login` — Login page (public)
  - `/dashboard` — Dashboard (authenticated)
  - `/system/user` — User management
  - `/system/role` — Role management
  - `/system/dept` — Department management
  - `/system/permission` — Permission management
  - `/system/log` — Operation log
  - `/customer/list` — Customer list
  - `/customer/detail/:id` — Customer detail

#### Scenario: Route guard

- **WHEN** user navigates to a protected route without authentication
- **THEN** system redirects to login page
- **WHEN** user navigates to a route without required permission
- **THEN** system shows 403 Forbidden page

### Requirement: Token management

The system SHALL manage JWT tokens on the client side.

#### Scenario: Token storage

- **WHEN** user logs in successfully
- **THEN** access token is stored in memory (not localStorage)
- **AND** refresh token is stored in httpOnly cookie or secure storage
- **AND** token expiration time is recorded

#### Scenario: Automatic token refresh

- **WHEN** access token is about to expire (within 60 seconds)
- **THEN** system automatically calls `/api/auth/refresh` with refresh token
- **AND** new access token replaces the old one
- **AND** pending requests are retried with new token

#### Scenario: Token cleanup on logout

- **WHEN** user logs out
- **THEN** all stored tokens are cleared
- **AND** user is redirected to login page

### Requirement: API request layer

The system SHALL provide a centralized API request layer using Axios.

#### Scenario: Request interceptor

- **WHEN** API request is made
- **THEN** interceptor adds `Authorization: Bearer <token>` header
- **AND** interceptor adds `Content-Type: application/json` header

#### Scenario: Response interceptor

- **WHEN** API response is received
- **THEN** interceptor checks response code
- **AND** if code is 401, triggers token refresh or redirect to login
- **AND** if code is 403, shows permission denied message
- **AND** if code is business error, shows error message to user

#### Scenario: Unified error handling

- **WHEN** network error or server error occurs
- **THEN** system shows user-friendly error message
- **AND** error details are logged to console for debugging

### Requirement: Permission directives

The system SHALL provide Vue directives for permission-based UI control.

#### Scenario: Button permission

- **WHEN** element has `v-permission="'system:user:add'"` directive
- **THEN** element is visible only if user has `system:user:add` permission
- **AND** element is removed from DOM if user lacks permission

#### Scenario: Role-based visibility

- **WHEN** element has `v-role="'admin'"` directive
- **THEN** element is visible only if user has `admin` role

### Requirement: State management

The system SHALL use Pinia for client-side state management.

#### Scenario: User store

- **WHEN** user logs in
- **THEN** user store is populated with user info (username, nickname, roles, permissions)
- **AND** user store is cleared on logout

#### Scenario: Permission store

- **WHEN** user info is loaded
- **THEN** permission store caches user's permission codes
- **AND** permission checks use cached data (no repeated API calls)

### Requirement: TypeScript type safety

The system SHALL use TypeScript for type-safe development.

#### Scenario: API response types

- **WHEN** API calls are made
- **THEN** response data is typed with TypeScript interfaces
- **AND** type errors are caught at compile time

#### Scenario: Component props

- **WHEN** components receive props
- **THEN** props are typed with TypeScript interfaces
- **AND** invalid prop types show compile-time errors
