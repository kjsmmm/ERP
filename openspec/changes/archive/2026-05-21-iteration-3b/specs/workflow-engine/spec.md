## ADDED Requirements

### Requirement: Flowable integration
The system SHALL integrate Flowable 7.x via Spring Boot Starter. Flowable database tables (ACT_*) SHALL be auto-created on startup. Process definition files SHALL be placed in resources/processes/.

#### Scenario: Application starts with Flowable
- **WHEN** application starts
- **THEN** Flowable engine initializes and ACT_* tables are created in the database

### Requirement: Start approval process
The system SHALL provide an API to start an approval process with a process definition key, business key, and initiator. The system SHALL return a process instance ID.

#### Scenario: Start a process
- **WHEN** user submits an approval request with process key and business key
- **THEN** system starts the process and returns process instance ID

### Requirement: Query pending tasks
The system SHALL provide an API to query pending approval tasks for the current user. Results SHALL include task ID, process instance info, business key, and create time.

#### Scenario: User queries own pending tasks
- **WHEN** authenticated user queries pending tasks
- **THEN** system returns all tasks assigned to or claimable by the user

### Requirement: Query completed tasks
The system SHALL provide an API to query completed approval tasks for the current user with approval result and comments.

#### Scenario: User queries task history
- **WHEN** authenticated user queries completed tasks
- **THEN** system returns all tasks the user has approved/rejected with comments and timestamps

### Requirement: Approve task
The system SHALL provide an API to approve a pending task with optional comments. After approval, the task SHALL move to the next step or complete the process.

#### Scenario: Approve at final step
- **WHEN** approver approves the last task in a process
- **THEN** process instance completes and business status is updated

### Requirement: Reject task
The system SHALL provide an API to reject a pending task with required comments. After rejection, the process SHALL end and the business entity SHALL revert to its previous state.

#### Scenario: Reject with comments
- **WHEN** approver rejects a task with comments
- **THEN** process ends and business entity reverts to previous state
