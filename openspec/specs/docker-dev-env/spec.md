## ADDED Requirements

### Requirement: Docker Compose configuration

The system SHALL have a Docker Compose configuration for development environment.

#### Scenario: Docker Compose file exists

- **WHEN** developer examines `docker/` directory
- **THEN** `docker-compose.yml` exists with service definitions for:
  - MySQL 8.0
  - Redis 7
  - Adminer (database management tool)
- **AND** all services use persistent volumes for data retention

#### Scenario: Services start successfully

- **WHEN** developer runs `docker-compose up -d` in `docker/` directory
- **THEN** all three services start without errors
- **AND** MySQL is accessible on port 3306
- **AND** Redis is accessible on port 6379
- **AND** Adminer is accessible on port 8080

### Requirement: MySQL configuration

The system SHALL have properly configured MySQL 8.0 instance.

#### Scenario: MySQL initializes with schema

- **WHEN** MySQL container starts for the first time
- **THEN** database `erp` is created automatically
- **AND** init script from `docker/mysql/init.sql` is executed
- **AND** system management tables are created (sys_user, sys_role, etc.)

#### Scenario: MySQL data persists

- **WHEN** developer stops and restarts Docker containers
- **THEN** MySQL data is retained (not lost)
- **AND** all tables and data remain intact

### Requirement: Redis configuration

The system SHALL have properly configured Redis 7 instance.

#### Scenario: Redis is accessible

- **WHEN** backend application starts
- **THEN** it can connect to Redis on `localhost:6379`
- **AND** Redis can store and retrieve JWT tokens
- **AND** Redis can store session data

### Requirement: Adminer for database management

The system SHALL include Adminer for easy database management during development.

#### Scenario: Adminer provides web interface

- **WHEN** developer accesses `http://localhost:8080` in browser
- **THEN** Adminer login page is displayed
- **AND** developer can connect to MySQL using credentials:
  - System: MySQL
  - Server: mysql (Docker service name)
  - Username: root
  - Password: (from environment variable)
  - Database: erp
- **AND** developer can browse tables, run queries, and manage data

### Requirement: Environment variables management

The system SHALL use environment variables for sensitive configuration.

#### Scenario: Environment variables are defined

- **WHEN** developer examines `docker/docker-compose.yml`
- **THEN** sensitive values use environment variables:
  - `MYSQL_ROOT_PASSWORD`
  - `MYSQL_DATABASE`
- **AND** `.env.example` file exists with template values
- **AND** actual `.env` file is in `.gitignore`

#### Scenario: Application reads environment variables

- **WHEN** backend application starts
- **THEN** it reads database credentials from environment variables
- **AND** it reads Redis connection from environment variables
- **AND** it reads JWT secret from environment variables
