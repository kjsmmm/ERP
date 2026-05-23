## ADDED Requirements

### Requirement: Create sales order
The system SHALL allow users to create sales orders with customer, order items (product, quantity, unit price), expected delivery date, and remarks. Order number SHALL be auto-generated with format SO-YYYYMMDD-NNN. Initial status SHALL be DRAFT.

#### Scenario: Create order with valid data
- **WHEN** user submits order with valid customer, at least one order item, and delivery date
- **THEN** system creates order with status DRAFT, auto-generates order number, and returns order ID

### Requirement: Query order list
The system SHALL provide paginated order list with filters for customer name, order number, status, and date range. Results SHALL be sorted by creation time descending.

#### Scenario: Filter orders by status
- **WHEN** user queries with status filter = CONFIRMED
- **THEN** system returns only orders with CONFIRMED status

### Requirement: View order detail
The system SHALL display order detail including basic info, customer info, order items, and total amount.

#### Scenario: View existing order
- **WHEN** user requests detail for an existing order
- **THEN** system returns order info with customer name, item list with product details

### Requirement: Edit order
The system SHALL allow editing orders in DRAFT or CONFIRMED status. In iteration 3a, edits are applied directly without approval.

#### Scenario: Edit draft order
- **WHEN** user modifies items or delivery date of a DRAFT order
- **THEN** system updates the order directly

#### Scenario: Edit confirmed order
- **WHEN** user modifies a CONFIRMED order
- **THEN** system updates the order directly (approval will be added in iteration 3b)

### Requirement: Delete order
The system SHALL allow deleting orders only in DRAFT status.

#### Scenario: Delete draft order
- **WHEN** user deletes a DRAFT order
- **THEN** system removes the order and releases any reserved inventory

#### Scenario: Reject delete for non-draft order
- **WHEN** user attempts to delete a CONFIRMED order
- **THEN** system rejects with error message

### Requirement: Order state machine
The system SHALL enforce valid state transitions: DRAFT→CONFIRMED→IN_PRODUCTION→COMPLETED→CLOSED, with CANCELLED and PAUSED branches. Invalid transitions SHALL be rejected.

#### Scenario: Confirm order with sufficient inventory
- **WHEN** user confirms a DRAFT order and all items have sufficient available inventory
- **THEN** order status changes to CONFIRMED and inventory is reserved for all items

#### Scenario: Reject confirm with insufficient inventory
- **WHEN** user confirms a DRAFT order but some items have insufficient available inventory
- **THEN** system rejects with error listing insufficient products

#### Scenario: Cancel confirmed order
- **WHEN** user cancels a CONFIRMED order
- **THEN** order status changes to CANCELLED and reserved inventory is released

### Requirement: Order amount calculation
The system SHALL calculate item subtotal (quantity * unit price) and order total amount automatically.

#### Scenario: Auto-calculate on item change
- **WHEN** user changes quantity or unit price of an order item
- **THEN** system recalculates subtotal for that item and total amount for the order
