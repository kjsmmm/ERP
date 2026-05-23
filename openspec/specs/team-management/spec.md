# team-management Specification

## Purpose
TBD - created by archiving change iteration-4a. Update Purpose after archive.
## Requirements
### Requirement: Team CRUD
The system SHALL provide CRUD operations for teams with fields: teamCode (unique), teamName, workshopId (FK to workshop), leaderId (FK to sys_user), memberCount, status.

#### Scenario: Create team
- **WHEN** user creates a team with valid workshopId and leaderId
- **THEN** team is saved with the specified workshop and leader

#### Scenario: Invalid workshopId
- **WHEN** user creates a team with non-existent workshopId
- **THEN** system rejects with error "车间不存在"

### Requirement: Team list by workshop
The system SHALL provide team list filtered by workshopId.

#### Scenario: Query teams by workshop
- **WHEN** user queries teams with workshopId = 1
- **THEN** system returns all teams belonging to workshop 1 with leader name filled

