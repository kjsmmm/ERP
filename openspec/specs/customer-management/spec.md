## ADDED Requirements

### Requirement: Customer CRUD

The system SHALL provide full CRUD operations for customer management.

#### Scenario: Create customer

- **WHEN** user submits customer creation request
- **THEN** system validates required fields (customer_name, customer_type)
- **AND** system auto-generates customer_code (format: CUS-YYYYMMDD-NNN)
- **AND** system checks customer_name uniqueness
- **AND** customer record is created in `customer` table
- **AND** customer ID is returned in response
- **AND** created_by and created_at are auto-filled

#### Scenario: Query customer list

- **WHEN** user requests customer list with pagination
- **THEN** system returns paginated customer list
- **AND** supports filtering by customer_name, customer_code, industry, level, type, status
- **AND** supports sorting by created_at, customer_name
- **AND** response includes customer basic info without related contacts

#### Scenario: Update customer

- **WHEN** user submits customer update request
- **THEN** system validates required fields
- **AND** system checks customer_name uniqueness (if changed, excluding self)
- **AND** customer record is updated in database
- **AND** updated_by and updated_at are auto-filled

#### Scenario: Delete customer

- **WHEN** user submits customer deletion request
- **THEN** system performs logical deletion (sets `deleted` flag)
- **AND** associated contacts and follow records are retained
- **AND** deleted customer cannot be queried in normal list

#### Scenario: Enable/disable customer

- **WHEN** user changes customer status
- **THEN** customer status is updated in database
- **AND** disabled customers are shown with disabled status indicator

### Requirement: Customer detail view

The system SHALL provide a comprehensive customer detail view.

#### Scenario: View customer detail

- **WHEN** user opens customer detail page
- **THEN** system displays customer basic info in first tab
- **AND** system displays contact list in second tab
- **AND** system displays follow records in third tab
- **AND** all tabs load data on demand (lazy loading)

### Requirement: Customer contact management

The system SHALL manage contacts associated with customers.

#### Scenario: Add contact

- **WHEN** user adds contact to customer
- **THEN** system validates required fields (contact_name, phone)
- **AND** contact record is created linked to customer
- **AND** if is_primary is set, other contacts' is_primary is cleared (one primary per customer)

#### Scenario: Edit contact

- **WHEN** user edits a contact
- **THEN** system validates required fields
- **AND** contact record is updated
- **AND** is_primary logic is enforced

#### Scenario: Delete contact

- **WHEN** user deletes a contact
- **THEN** contact is logically deleted
- **AND** associated follow records retain the contact reference

#### Scenario: List contacts

- **WHEN** user views customer contacts tab
- **THEN** system returns all contacts for the customer
- **AND** primary contact is highlighted
- **AND** contacts are sorted by is_primary DESC, created_at ASC

### Requirement: Customer follow records

The system SHALL track customer interaction follow records.

#### Scenario: Add follow record

- **WHEN** user adds follow record
- **THEN** system validates required fields (follow_type, content, follow_time)
- **AND** follow record is created linked to customer
- **AND** optionally linked to a specific contact
- **AND** operator_id is set to current logged-in user

#### Scenario: List follow records

- **WHEN** user views customer follow records tab
- **THEN** system returns paginated follow records for the customer
- **AND** records are sorted by follow_time DESC (newest first)
- **AND** each record shows follow type icon, content, operator name, time

#### Scenario: Follow record types

- **WHEN** user creates follow record
- **THEN** system supports follow types:
  - 电话 (Phone call)
  - 拜访 (Visit)
  - 邮件 (Email)
  - 微信 (WeChat)
- **AND** each type has a distinct icon in the UI

### Requirement: Customer search and filter

The system SHALL provide efficient customer search and filtering.

#### Scenario: Quick search

- **WHEN** user types in search box
- **THEN** system searches by customer_name and customer_code
- **AND** search is triggered on Enter or search button click
- **AND** results are paginated

#### Scenario: Advanced filter

- **WHEN** user applies filters
- **THEN** system supports filter by:
  - customer_type (国内/国外)
  - customer_level (A/B/C/D)
  - industry
  - status (启用/停用)
- **AND** multiple filters can be combined
- **AND** filters can be reset

### Requirement: Customer data model

The system SHALL store customer data with proper relational structure.

#### Scenario: Customer table structure

- **WHEN** customer record is created
- **THEN** table `customer` contains:
  - id (BIGINT PK)
  - customer_code (VARCHAR(50) UNIQUE)
  - customer_name (VARCHAR(100))
  - customer_type (TINYINT: 1=国内, 2=国外)
  - industry (VARCHAR(50))
  - customer_level (TINYINT: 1=A, 2=B, 3=C, 4=D)
  - source (VARCHAR(50))
  - tax_number (VARCHAR(30))
  - bank_name (VARCHAR(100))
  - bank_account (VARCHAR(50))
  - payment_terms (VARCHAR(50))
  - credit_limit (DECIMAL)
  - address (VARCHAR(200))
  - status (TINYINT: 0=停用, 1=启用)
  - created_by, created_at, updated_by, updated_at, deleted, factory_id, remark

#### Scenario: Contact table structure

- **WHEN** contact record is created
- **THEN** table `customer_contact` contains:
  - id (BIGINT PK)
  - customer_id (BIGINT FK → customer.id)
  - contact_name (VARCHAR(50))
  - position (VARCHAR(50))
  - phone (VARCHAR(20))
  - email (VARCHAR(100))
  - is_primary (TINYINT: 0=否, 1=是)
  - created_by, created_at, updated_by, updated_at, deleted, factory_id, remark

#### Scenario: Follow table structure

- **WHEN** follow record is created
- **THEN** table `customer_follow` contains:
  - id (BIGINT PK)
  - customer_id (BIGINT FK → customer.id)
  - contact_id (BIGINT FK → customer_contact.id, nullable)
  - follow_type (TINYINT: 1=电话, 2=拜访, 3=邮件, 4=微信)
  - content (TEXT)
  - follow_time (DATETIME)
  - next_follow_time (DATETIME, nullable)
  - operator_id (BIGINT FK → sys_user.id)
  - created_by, created_at, updated_by, updated_at, deleted, factory_id, remark
