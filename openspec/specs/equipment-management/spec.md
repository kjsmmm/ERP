# equipment-management Specification

## Purpose
TBD - created by archiving change iteration-4a. Update Purpose after archive.
## Requirements
### Requirement: Equipment type CRUD
The system SHALL provide CRUD operations for equipment types with fields: typeCode (unique), typeName, description.

#### Scenario: Create equipment type
- **WHEN** user creates an equipment type with valid data
- **THEN** equipment type is saved

#### Scenario: Delete equipment type in use
- **WHEN** user deletes an equipment type that is referenced by equipment or process steps
- **THEN** system rejects with error "设备类型已被引用，不能删除"

### Requirement: Equipment CRUD
The system SHALL provide CRUD operations for equipment with fields: equipmentCode (unique), equipmentName, equipmentTypeId (FK), workshopId (FK), status (1=正常, 2=维修中, 0=停用), purchaseDate, lastMaintenanceDate, nextMaintenanceDate, remark.

#### Scenario: Create equipment
- **WHEN** user creates equipment with valid workshopId and equipmentTypeId
- **THEN** equipment is saved

#### Scenario: Equipment list with filters
- **WHEN** user queries equipment filtered by workshopId and status
- **THEN** system returns matching equipment with type name and workshop name filled

