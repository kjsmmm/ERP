# order-change-approval Specification

## Purpose
TBD - created by archiving change iteration-3b. Update Purpose after archive.
## Requirements
### Requirement: Order change triggers approval
When a CONFIRMED order is edited, the system SHALL create an approval request instead of applying changes directly. Order status SHALL change to PENDING_CHANGE.

#### Scenario: Edit confirmed order starts approval
- **WHEN** user edits a CONFIRMED order
- **THEN** order status changes to PENDING_CHANGE and approval process starts

### Requirement: Approval process definition
The order change approval process SHALL follow: applicant submits → direct supervisor approves → process completes.

#### Scenario: Supervisor approves change
- **WHEN** supervisor approves the order change
- **THEN** order changes are applied and status returns to CONFIRMED

#### Scenario: Supervisor rejects change
- **WHEN** supervisor rejects the order change
- **THEN** order changes are discarded and status returns to CONFIRMED

### Requirement: Order status reflects approval state
The system SHALL expose the current approval state (pending/approved/rejected) on the order detail page.

#### Scenario: View order with pending approval
- **WHEN** user views an order in PENDING_CHANGE status
- **THEN** system shows approval status and current approver

