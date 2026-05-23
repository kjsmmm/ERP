## ADDED Requirements

### Requirement: User authentication with JWT

The system SHALL authenticate users using JWT (JSON Web Token) mechanism.

#### Scenario: Successful login

- **WHEN** user submits valid username and password
- **THEN** system verifies credentials against database
- **AND** password is verified using BCrypt hash
- **AND** system generates Access Token (15 minutes expiration)
- **AND** system generates Refresh Token (7 days expiration)
- **AND** both tokens are stored in Redis for active session tracking
- **AND** tokens are returned to client in response

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

### Requirement: Token refresh mechanism

The system SHALL allow refreshing Access Token using Refresh Token.

#### Scenario: Successful token refresh

- **WHEN** client sends valid Refresh Token to `/api/auth/refresh`
- **THEN** system verifies Refresh Token is valid and not expired
- **AND** system generates new Access Token
- **AND** new Access Token is stored in Redis
- **AND** old Access Token is invalidated
- **AND** new Access Token is returned to client

#### Scenario: Invalid refresh token

- **WHEN** client sends invalid or expired Refresh Token
- **THEN** system returns 401 Unauthorized
- **AND** error message indicates "Invalid refresh token"
- **AND** client must re-authenticate

### Requirement: Token validation

The system SHALL validate JWT tokens on protected endpoints.

#### Scenario: Valid token in request

- **WHEN** client sends request with valid Access Token in `Authorization` header
- **THEN** system extracts user information from token
- **AND** system verifies token is not expired
- **AND** system verifies token exists in Redis (not invalidated)
- **AND** request proceeds to endpoint handler

#### Scenario: Expired token

- **WHEN** client sends request with expired Access Token
- **THEN** system returns 401 Unauthorized
- **AND** error message indicates "Token expired"
- **AND** client should use Refresh Token to get new Access Token

#### Scenario: Missing token

- **WHEN** client sends request to protected endpoint without token
- **THEN** system returns 401 Unauthorized
- **AND** error message indicates "Token required"

### Requirement: User logout

The system SHALL support user logout with token invalidation.

#### Scenario: Successful logout

- **WHEN** user sends logout request with valid token
- **THEN** system removes tokens from Redis
- **AND** tokens are immediately invalidated
- **AND** system returns 200 OK with success message

#### Scenario: Logout with expired token

- **WHEN** user sends logout request with expired token
- **THEN** system attempts to remove tokens from Redis
- **AND** system returns 200 OK (idempotent operation)

### Requirement: Password security

The system SHALL securely store and verify passwords.

#### Scenario: Password hashing

- **WHEN** new user is created or password is changed
- **THEN** password is hashed using BCrypt with random salt
- **AND** hashed password is stored in database
- **AND** plain text password is never stored

#### Scenario: Password verification

- **WHEN** user attempts to login
- **THEN** system compares provided password with stored BCrypt hash
- **AND** verification is performed using BCrypt algorithm
- **AND** result is boolean (match or no match)

### Requirement: Security configuration

The system SHALL have proper Spring Security configuration.

#### Scenario: Public endpoints

- **WHEN** request is made to public endpoints
- **THEN** authentication is not required:
  - `/api/auth/login`
  - `/api/auth/refresh`
  - `/swagger-ui/**`
  - `/v3/api-docs/**`

#### Scenario: Protected endpoints

- **WHEN** request is made to protected endpoints
- **THEN** valid JWT token is required in `Authorization` header
- **AND** token format is `Bearer <token>`
- **AND** requests without valid token are rejected with 401

#### Scenario: Role-based access control

- **WHEN** user has specific roles assigned
- **THEN** user can only access endpoints permitted for those roles
- **AND** access control is enforced at method level using `@PreAuthorize`
- **AND** unauthorized access returns 403 Forbidden
