# process-route Specification

## Purpose
TBD - created by archiving change iteration-4a. Update Purpose after archive.
## Requirements
### Requirement: Process route CRUD
The system SHALL provide CRUD operations for process routes with fields: productId (FK), routeCode, routeName, version, isDefault (boolean), status (1=启用, 0=停用). A product SHALL have at most one default route.

#### Scenario: Create process route
- **WHEN** user creates a process route for a product
- **THEN** route is saved with version 1

#### Scenario: Set default route
- **WHEN** user sets a route as default for a product
- **THEN** system unsets any existing default route for that product

### Requirement: Process step CRUD
The system SHALL provide CRUD for process steps with fields: routeId (FK), stepNo, stepName, standardTime (minutes), equipmentType (text), description. Steps SHALL be ordered by stepNo within a route.

#### Scenario: Add steps to route
- **WHEN** user adds steps to a process route
- **THEN** steps are saved and ordered by stepNo

### Requirement: Process route in product detail
The system SHALL expose the process route and its steps in the product detail API.

#### Scenario: View product with process route
- **WHEN** user views a product detail
- **THEN** system returns the default process route with all steps ordered by stepNo

