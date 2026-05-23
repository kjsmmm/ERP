# workshop-management Specification

## Purpose
TBD - created by archiving change iteration-4a. Update Purpose after archive.
## Requirements
### Requirement: Workshop CRUD
The system SHALL provide CRUD operations for workshops with fields: workshopCode (unique), workshopName, address, manager, phone, description, status (1=enabled, 0=disabled).

#### Scenario: Create workshop
- **WHEN** user creates a workshop with valid data
- **THEN** workshop is saved and returned with generated ID

#### Scenario: Duplicate workshop code
- **WHEN** user creates a workshop with an existing workshopCode
- **THEN** system rejects with error "车间编码已存在"

#### Scenario: Delete workshop with teams
- **WHEN** user deletes a workshop that has associated teams
- **THEN** system rejects with error "车间下存在班组，不能删除"

### Requirement: Workshop list query
The system SHALL provide a paginated workshop list with keyword search on workshopCode and workshopName.

#### Scenario: Search workshops
- **WHEN** user queries with keyword "切割"
- **THEN** system returns workshops whose code or name contains "切割"

