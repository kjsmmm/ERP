## MODIFIED Requirements

### Requirement: View order detail
The system SHALL display order detail including basic info, customer info, order items, total amount, and delivery status (delivered quantity per item, fully delivered flag).

#### Scenario: View order with delivery status
- **WHEN** user views order detail
- **THEN** system returns order info with each item showing ordered quantity, delivered quantity, and remaining quantity

#### Scenario: View fully delivered order
- **WHEN** all items in an order have been fully delivered
- **THEN** system shows delivery status as "全部发货"

#### Scenario: View partially delivered order
- **WHEN** some items in an order have been partially delivered
- **THEN** system shows delivery status as "部分发货" with remaining quantities
