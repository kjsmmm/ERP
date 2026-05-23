## ADDED Requirements

### Requirement: Unified response format

The system SHALL use a unified response format for all API endpoints.

#### Scenario: Successful response format

- **WHEN** API endpoint returns success
- **THEN** response body follows `Result<T>` format:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": { ... }
  }
  ```

#### Scenario: Error response format

- **WHEN** API endpoint returns error
- **THEN** response body follows `Result<T>` format:
  ```json
  {
    "code": 400,
    "message": "error description",
    "data": null
  }
  ```

#### Scenario: Pagination response format

- **WHEN** API endpoint returns paginated data
- **THEN** response body follows `PageResult<T>` format:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "records": [...],
      "total": 100,
      "current": 1,
      "size": 10
    }
  }
  ```

### Requirement: Global exception handling

The system SHALL have global exception handling that catches and formats all exceptions.

#### Scenario: Business exception handling

- **WHEN** `BusinessException` is thrown
- **THEN** global exception handler catches it
- **AND** returns appropriate error code and message
- **AND** logs the exception with context

#### Scenario: Validation exception handling

- **WHEN** request validation fails (e.g., `@Valid` annotation)
- **THEN** global exception handler catches `MethodArgumentNotValidException`
- **AND** returns 400 status code with validation error details
- **AND** error message includes field-level validation errors

#### Scenario: Access denied handling

- **WHEN** user lacks required permission
- **THEN** global exception handler catches `AccessDeniedException`
- **AND** returns 403 status code with "Access denied" message

#### Scenario: Unknown exception handling

- **WHEN** unexpected exception occurs
- **THEN** global exception handler catches generic `Exception`
- **AND** returns 500 status code with generic error message
- **AND** logs full exception stack trace for debugging

### Requirement: Logging framework

The system SHALL use SLF4J + Logback for logging.

#### Scenario: Console logging

- **WHEN** application runs in development mode
- **THEN** logs are output to console with:
  - Timestamp
  - Log level
  - Thread name
  - Logger name
  - Message
  - Exception stack trace (if any)

#### Scenario: File logging

- **WHEN** application runs in production mode
- **THEN** logs are written to `logs/erp.log`
- **AND** log files rotate daily
- **AND** old log files are retained for 30 days
- **AND** log file size is limited to 100MB per file

#### Scenario: Log level configuration

- **WHEN** developer configures logging in `application.yml`
- **THEN** log levels can be set per package:
  ```yaml
  logging:
    level:
      com.erp: DEBUG
      org.springframework: INFO
  ```

### Requirement: CORS configuration

The system SHALL have proper Cross-Origin Resource Sharing (CORS) configuration.

#### Scenario: Development CORS

- **WHEN** frontend runs on `localhost:3000`
- **AND** backend runs on `localhost:8080`
- **THEN** backend allows requests from `localhost:3000`
- **AND** all HTTP methods are allowed
- **AND** credentials (cookies, auth headers) are allowed

#### Scenario: Production CORS

- **WHEN** application runs in production
- **THEN** CORS is configured with specific allowed origins
- **AND** only required HTTP methods are allowed
- **AND** credentials are restricted to allowed origins

### Requirement: API documentation

The system SHALL have auto-generated API documentation using SpringDoc (OpenAPI 3).

#### Scenario: Swagger UI accessible

- **WHEN** developer accesses `/swagger-ui.html`
- **THEN** Swagger UI is displayed
- **AND** all API endpoints are listed
- **AND** endpoints are grouped by module (auth, system, customer, etc.)

#### Scenario: API documentation includes details

- **WHEN** developer views API documentation
- **THEN** each endpoint shows:
  - HTTP method and path
  - Description
  - Request parameters
  - Request body schema (if applicable)
  - Response schema
  - Example values

#### Scenario: Authentication in Swagger UI

- **WHEN** developer uses Swagger UI to test authenticated endpoints
- **THEN** Swagger UI has "Authorize" button
- **AND** developer can enter JWT token
- **AND** authenticated requests include token in header
