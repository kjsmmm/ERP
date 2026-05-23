## ADDED Requirements

### Requirement: Warehouse management
The system SHALL provide CRUD operations for warehouses. Each warehouse SHALL have a unique code, name, address, and status.

#### Scenario: Create warehouse
- **WHEN** user creates a warehouse with valid code and name
- **THEN** system saves the warehouse with status active

#### Scenario: Prevent duplicate warehouse code
- **WHEN** user creates a warehouse with an existing code
- **THEN** system rejects with error message

### Requirement: Inventory query
The system SHALL display inventory levels per product per warehouse, showing on_hand_qty, reserved_qty, and calculated available_qty.

#### Scenario: Query inventory by product
- **WHEN** user queries inventory for a specific product
- **THEN** system returns inventory records across all warehouses with available quantity calculated

#### Scenario: Query inventory by warehouse
- **WHEN** user queries inventory for a specific warehouse
- **THEN** system returns all product inventory records in that warehouse

### Requirement: Stock inbound
The system SHALL support stock inbound operations. Each operation SHALL increase on_hand_qty and create a stock record.

#### Scenario: Inbound to existing inventory
- **WHEN** user performs inbound of 100 units of product A to warehouse 1
- **THEN** system increases on_hand_qty by 100 and creates a stock record with type INBOUND

#### Scenario: Inbound creates new inventory record
- **WHEN** user performs inbound for a product that has no inventory record in the target warehouse
- **THEN** system creates a new inventory record with the inbound quantity

### Requirement: Stock outbound
The system SHALL support stock outbound operations. Each operation SHALL decrease on_hand_qty and release corresponding reserved_qty, then create a stock record. System SHALL reject outbound if on_hand_qty is insufficient.

#### Scenario: Outbound with sufficient stock
- **WHEN** user performs outbound of 50 units and on_hand_qty >= 50
- **THEN** system decreases on_hand_qty by 50, decreases reserved_qty by min(50, reserved_qty), and creates a stock record with type OUTBOUND

#### Scenario: Reject outbound with insufficient stock
- **WHEN** user performs outbound of 100 units but on_hand_qty is only 80
- **THEN** system rejects with error message showing available quantity

### Requirement: Inventory reservation
The system SHALL support inventory reservation (increase reserved_qty) and release (decrease reserved_qty). Reservation SHALL be rejected if available_qty (on_hand - reserved) is insufficient.

#### Scenario: Reserve inventory for order
- **WHEN** order is confirmed and product has sufficient available_qty
- **THEN** system increases reserved_qty by order quantity

#### Scenario: Release reservation on order cancel
- **WHEN** confirmed order is cancelled
- **THEN** system decreases reserved_qty by the reserved amount

#### Scenario: Reject reservation exceeding available
- **WHEN** system attempts to reserve 100 units but available_qty is only 80
- **THEN** reservation is rejected with error

### Requirement: Stock record audit trail
The system SHALL record every inventory movement with product, warehouse, quantity, type, reference order, operator, and timestamp.

#### Scenario: Stock record created on inbound
- **WHEN** inbound operation completes
- **THEN** stock record is created with type INBOUND, quantity, product, warehouse, and operator
