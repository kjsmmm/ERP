# customer-pricing Specification

## Purpose
TBD - created by archiving change iteration-3b. Update Purpose after archive.
## Requirements
### Requirement: Customer product price management
The system SHALL support managing customer-specific product prices. Each price record SHALL link a customer, a product, and a unit price.

#### Scenario: Set customer-specific price
- **WHEN** user sets price of product A for customer X as 95.00
- **THEN** system saves the price record

#### Scenario: Update customer price
- **WHEN** user updates the price of an existing customer-product combination
- **THEN** system updates the price record

#### Scenario: Delete customer price
- **WHEN** user deletes a customer price record
- **THEN** system removes the record

### Requirement: Price priority in order creation
The system SHALL use customer-specific price when available, otherwise fall back to product standard price.

#### Scenario: Auto-fill customer price
- **WHEN** user adds a product to an order for a customer that has a specific price
- **THEN** system fills unit_price with the customer-specific price

#### Scenario: Fall back to standard price
- **WHEN** user adds a product to an order for a customer that has no specific price
- **THEN** system fills unit_price with product.standard_price

### Requirement: Query customer prices
The system SHALL provide an API to query all product prices for a specific customer, or all customer prices for a specific product.

#### Scenario: Query prices for a customer
- **WHEN** user requests prices for customer X
- **THEN** system returns all product prices specific to customer X with product details

