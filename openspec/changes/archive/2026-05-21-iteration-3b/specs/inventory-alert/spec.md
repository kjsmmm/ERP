## ADDED Requirements

### Requirement: Safety stock configuration
The system SHALL support configuring safety stock levels per product per warehouse in the inventory record.

#### Scenario: Set safety stock
- **WHEN** user sets safety stock of product A in warehouse 1 to 50
- **THEN** system saves the safety stock value in the inventory record

### Requirement: Inventory alert query
The system SHALL provide a query for products where on_hand_qty is below safety stock. Results SHALL include product info, warehouse, current quantity, safety stock, and deficit.

#### Scenario: Query low-stock products
- **WHEN** user queries inventory alerts
- **THEN** system returns products where on_hand_qty < safety_stock with current quantity and deficit amount

#### Scenario: No alerts when stock is sufficient
- **WHEN** all products have on_hand_qty >= safety_stock
- **THEN** system returns empty alert list
