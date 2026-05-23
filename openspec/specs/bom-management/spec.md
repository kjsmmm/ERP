# bom-management Specification

## Purpose
TBD - created by archiving change iteration-2. Update Purpose after archive.
## Requirements
### Requirement: BOM item management

The system SHALL manage BOM (Bill of Materials) items for products.

#### Scenario: Get BOM for a product

- **WHEN** user requests BOM for a product
- **THEN** system returns all direct child items from `bom_item` table
- **AND** each item includes material product info (name, code, unit, spec)
- **AND** items are sorted by sort_order

#### Scenario: Update BOM for a product

- **WHEN** user submits updated BOM item list for a product
- **THEN** system validates that all material_id references exist in product table
- **AND** system validates that quantity is greater than 0
- **AND** system validates that waste_rate is between 0 and 100
- **AND** system deletes all existing bom_items for the product
- **AND** system inserts all new bom_items in a single transaction
- **AND** updated_by and updated_at are auto-filled on new items

#### Scenario: BOM only for 半成品 and 成品

- **WHEN** user attempts to manage BOM for a product
- **THEN** system only allows BOM management when product_type is 2 (半成品) or 3 (成品)
- **AND** system rejects BOM management for product_type 1 (原材料) with appropriate message

### Requirement: BOM recursive expansion

The system SHALL support recursive expansion of BOM tree for cost calculation and production planning.

#### Scenario: Expand BOM tree recursively

- **WHEN** user requests full BOM expansion for a product
- **THEN** system recursively expands all sub-levels using MySQL recursive CTE
- **AND** system returns a flat list with level indicator showing nesting depth
- **AND** semi-finished products (半成品) in the tree are expanded to show their own materials
- **AND** raw materials (原材料) appear as leaf nodes
- **AND** each expanded item shows effective quantity (parent quantity × child quantity × (1 + waste_rate/100))

#### Scenario: BOM expansion depth limit

- **WHEN** system performs recursive BOM expansion
- **THEN** system enforces maximum recursion depth of 10 levels
- **AND** if depth exceeds 10, system returns error indicating possible circular reference

### Requirement: BOM circular reference detection

The system SHALL prevent circular references in BOM structure.

#### Scenario: Detect circular reference on save

- **WHEN** user saves a BOM that would create a circular reference
- **THEN** system traverses the BOM tree from the product being edited
- **AND** system tracks all visited product IDs
- **AND** if any material_id in the chain points back to an already-visited product, system rejects the save
- **AND** system returns error message indicating the circular dependency path

#### Scenario: Prevent self-reference

- **WHEN** user adds a BOM item where material_id equals the product_id
- **THEN** system rejects with error "产品不能包含自身作为子项"

### Requirement: BOM deletion constraints

The system SHALL enforce referential integrity for BOM data.

#### Scenario: Delete product referenced in BOM

- **WHEN** user attempts to delete a product that is referenced as material_id in any bom_item
- **THEN** system rejects deletion
- **AND** system returns list of product names that reference this material
- **AND** user must remove BOM references before deletion is possible

### Requirement: BOM data model

The system SHALL store BOM data with proper relational structure.

#### Scenario: BOM item table structure

- **WHEN** bom_item record is created
- **THEN** table `bom_item` contains:
  - id (BIGINT PK)
  - product_id (BIGINT FK → product.id, the parent product)
  - material_id (BIGINT FK → product.id, the child material/component)
  - quantity (DECIMAL(12,4))
  - waste_rate (DECIMAL(5,2) DEFAULT 0)
  - sort_order (INT DEFAULT 0)
  - created_by, created_at, updated_by, updated_at, deleted, factory_id, remark
- **AND** unique constraint on (product_id, material_id) to prevent duplicate entries

### Requirement: BOM API endpoints

The system SHALL provide RESTful API endpoints for BOM management.

#### Scenario: BOM API operations

- **WHEN** frontend interacts with BOM
- **THEN** system provides:
  - GET /product/{id}/bom — get direct BOM items
  - PUT /product/{id}/bom — update BOM (full replacement)
  - GET /product/{id}/bom/expand — recursive full expansion
- **AND** all endpoints require authentication
- **AND** GET endpoints require `product:view` permission
- **AND** PUT endpoint requires `product:edit` permission

