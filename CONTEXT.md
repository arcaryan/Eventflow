v1.0

# Online Event Management System — Project Context

> **Purpose of this file:** This is the single source of truth for Codex/AI-assisted implementation of the project. Read this file before making any architectural, database, UI, or code change. Do not silently change the scope, technology choices, role permissions, workflows, naming conventions, or database model defined here.

---

## 1. Project Identity

**Project Name:** Online Event Management System  
**Project Type:** Java Web Application  
**Primary Goal:** Build a complete role-based event management platform in which Administrators manage the system, Event Organizers create and manage events/tickets/communications, and Attendees discover/register/purchase tickets and receive event updates.

The implementation should be suitable for a hackathon/academic evaluation and should clearly demonstrate:

- Problem understanding and solution design
- Core Java concepts
- JDBC database integration
- Servlet-based web integration
- Clean modular code
- Proper documentation and setup instructions
- A professional, understandable UI

---

## 2. Hackathon / Evaluation Constraints

This project is being evaluated as a **Java Web Based Project**.

### Marking areas visible in the hackathon brief

1. **Problem Understanding & Solution Design — 8 marks**
2. **Core Java Concepts — 10 marks**
3. **Database Integration (JDBC) — 8 marks**
4. **Servlets & Web Integration — 7 marks**

Therefore, the implementation must make those areas obvious in the codebase and presentation.

### Submission expectations

- Submit a presentation file with relevant visuals, diagrams, screenshots, and concise explanation.
- Submit a public or reviewer-accessible GitHub repository.
- Repository must have a clear structure and a README explaining setup, requirements, configuration, database setup, and how to run the project.
- Code should be modular, readable, properly commented where useful, and follow consistent coding conventions.
- Before submission, verify both the presentation file and GitHub project are accessible and complete.

---

## 3. Non-Negotiable Technology Stack

Unless the user explicitly asks for a change, use the following stack consistently.

### Backend

- **Java 17+**
- **Jakarta Servlets**
- **JSP + JSTL** for server-rendered views
- **JDBC** for database access
- **Maven** for dependency/build management
- **Apache Tomcat 10+** as servlet container

### Database

- **MySQL 8+**

### Frontend

- JSP
- HTML5
- CSS3
- Vanilla JavaScript
- Bootstrap 5 for professional responsive UI
- Chart.js for dashboard charts where needed

### Authentication

- Session-based authentication using `HttpSession`
- Passwords must be hashed, never stored as plain text
- BCrypt is allowed for password hashing

### Packaging

- Maven WAR project

Do **not** replace this architecture with Spring Boot, Hibernate/JPA, Node.js, React, Firebase, or another framework unless the user explicitly requests it. The point is to visibly satisfy the Servlet + JDBC evaluation rubric.

---

## 4. High-Level Product Summary

The system supports three user types:

1. **Admin**
2. **Event Organizer**
3. **Attendee**

Each role has a dedicated dashboard and strictly separated permissions.

### Product objective

Event organizers can create and manage events, configure tickets, review registrations, and communicate with attendees. Attendees can browse approved events, register, purchase tickets, and view updates. Administrators oversee users, approve/reject events, control system settings, and monitor system-wide statistics and activity.

---

## 5. Role Definitions and Permissions

### 5.1 Admin

Admin has system-wide access.

#### Admin can

- Login/logout
- View dashboard statistics
- Create/update/delete/deactivate user accounts
- Change user roles where appropriate
- View all users
- View organizer-submitted events
- Approve events
- Reject events with an optional reason
- View all registrations
- View ticket sales
- Manage system settings
- View system activity logs

#### Admin cannot

- Purchase attendee tickets as part of the normal flow
- Behave as an organizer unless a dedicated organizer account is used

---

### 5.2 Event Organizer

Organizer owns and manages their own events.

#### Organizer can

- Login/logout
- View organizer dashboard
- Create an event
- Edit own event
- Delete/cancel own event where allowed
- Submit event for approval
- View approval status
- Create ticket types
- Update ticket types
- Delete ticket types if no conflicting purchase exists
- View registrations for own events
- View ticket sales for own events
- Send updates/messages to registered attendees
- View event statistics
- View upcoming events and deadlines

#### Organizer cannot

- Approve their own events
- View or modify other organizers' private event-management data
- Manage global users or system settings

---

### 5.3 Attendee

Attendee interacts with approved/public events.

#### Attendee can

- Register/login/logout
- Browse approved upcoming events
- View event details
- Register for an event
- Select and purchase available tickets
- View purchase confirmations
- View purchased tickets
- View registered upcoming events
- View event updates/messages
- View registration history
- Update own profile and preferences

#### Attendee cannot

- Create events
- Approve/reject events
- Manage another user's profile or tickets

---

## 6. Core Functional Requirements

### 6.1 Authentication and Authorization

Required features:

- User registration for attendee and organizer accounts
- Login using email + password
- Logout
- Session handling
- Role-based route/page protection
- Unauthorized users redirected to an appropriate page
- Password hashing
- Basic server-side validation

Recommended user account statuses:

- `ACTIVE`
- `INACTIVE`
- `SUSPENDED`

Recommended roles:

- `ADMIN`
- `ORGANIZER`
- `ATTENDEE`

---

### 6.2 Admin — User Management

#### Input

- Name
- Email
- Role
- Account status

#### Output

- Success/error confirmation
- Updated user table

#### Required UI

- Searchable user table
- Filter by role/status
- Add user
- Edit user
- Deactivate/delete user
- Confirmation dialog for destructive action

---

### 6.3 Admin — Event Approval

Organizer-created events must not become publicly available until approved.

Recommended event statuses:

- `DRAFT`
- `PENDING_APPROVAL`
- `APPROVED`
- `REJECTED`
- `CANCELLED`
- `COMPLETED`

#### Admin can

- View pending events
- Open full event details
- Approve an event
- Reject an event
- Store rejection reason

#### Public rule

Only `APPROVED` events are visible to attendees for registration/ticket purchase.

---

### 6.4 Admin — System Settings

Support a simple system settings module. Keep it practical rather than overengineered.

Suggested settings:

- Platform name
- Support email
- Default currency
- Registration enabled/disabled
- Maximum tickets per attendee per event
- Event approval required flag

Store settings in the database using key/value records.

---

### 6.5 Admin — Event Statistics

Dashboard should display useful system-wide information such as:

- Total users
- Total organizers
- Total attendees
- Total events
- Pending events
- Approved events
- Total registrations
- Total tickets sold
- Total ticket revenue
- Recent activity

Use charts/tables where useful.

---

### 6.6 Admin — System Activity Monitoring

Implement activity logging for important actions.

Examples:

- User login
- User creation/update/deactivation
- Event creation
- Event submitted for approval
- Event approved/rejected
- Ticket created/updated
- Registration created/cancelled
- Ticket purchase completed
- Organizer message sent

Each log should ideally include:

- Actor user ID
- Action type
- Entity type
- Entity ID
- Short description
- Timestamp

The requirement mentions real-time updates. For this academic implementation, use a lightweight approach such as recent-activity refresh/polling rather than introducing WebSockets unless explicitly requested.

---

## 7. Event Organizer Functional Requirements

### 7.1 Event Creation

#### Input

- Title
- Description
- Date
- Start time
- End time
- Venue
- Capacity
- Category
- Optional banner/image URL or uploaded image path

#### Output

- Confirmation message
- Event stored as `DRAFT` initially

Validation:

- Title required
- Description required
- Event date cannot be in the past when created
- End time must be after start time
- Venue required
- Capacity must be positive

---

### 7.2 Event Management

Organizer dashboard must show own events in a table or cards.

Recommended columns:

- Event title
- Date
- Venue
- Status
- Registrations
- Tickets sold
- Actions

Available actions based on status:

- View
- Edit
- Submit for approval
- Cancel
- Delete draft
- Manage tickets
- View attendees
- Send update

Avoid allowing silent edits to critical details of an already approved event without deliberate handling. For this project, an edited approved event may be moved back to `PENDING_APPROVAL` if major information changes.

---

### 7.3 Ticket Management

An event can have one or more ticket types.

Examples:

- Free
- General Admission
- VIP
- Student

Ticket fields:

- ID
- Event ID
- Ticket name
- Description
- Price
- Quantity
- Sold quantity
- Sales start datetime
- Sales end datetime
- Status

Recommended ticket status:

- `ACTIVE`
- `INACTIVE`
- `SOLD_OUT`

Business rules:

- Price cannot be negative
- Quantity must be positive
- Sold quantity cannot exceed quantity
- Ticket cannot be purchased outside the sale window
- Ticket cannot be purchased for a non-approved event

---

### 7.4 Attendee Communication

Organizer can send a message/update to attendees registered for one of their events.

Message fields:

- Event ID
- Organizer ID
- Subject
- Message body
- Created timestamp

The message is stored in the system and shown in the attendee's Event Updates section.

Email/SMS integration is **not required** for the base version. It may be added later only if requested.

---

### 7.5 Organizer Statistics

Organizer dashboard statistics should be restricted to the organizer's own events.

Suggested metrics:

- Total own events
- Approved events
- Pending events
- Upcoming events
- Total registrations
- Tickets sold
- Revenue
- Registrations by event
- Ticket sales by event

---

## 8. Attendee Functional Requirements

### 8.1 Event Discovery

Attendees should be able to browse approved events.

Provide:

- Event cards/list
- Search by title
- Filter by category
- Filter by date/upcoming
- Event detail page

Event cards should show:

- Title
- Date/time
- Venue
- Category
- Organizer name
- Minimum ticket price or Free
- Registration/ticket availability

---

### 8.2 Event Registration

#### Input

- Event selection

#### Output

- Registration confirmation

Recommended registration statuses:

- `REGISTERED`
- `CANCELLED`
- `ATTENDED`

Business rules:

- One active registration per attendee per event
- Cannot register for rejected/cancelled/unapproved event
- Respect event capacity
- Store registration timestamp

---

### 8.3 Ticket Purchase

#### Input

- Selected ticket type
- Quantity
- Payment details / simulated payment confirmation

#### Output

- Purchase confirmation
- Ticket record/order record

For the hackathon version, implement **simulated payment**, not a real payment gateway, unless explicitly requested.

Recommended payment statuses:

- `PENDING`
- `PAID`
- `FAILED`
- `REFUNDED`

Recommended order fields:

- Order ID
- Attendee ID
- Event ID
- Ticket ID
- Quantity
- Unit price
- Total amount
- Payment status
- Order timestamp
- Confirmation/reference code

Business rules:

- Ticket stock must be checked server-side before purchase
- Total amount calculated server-side
- On successful purchase, sold quantity increases transactionally
- Prevent overselling

Use JDBC transactions for the purchase flow.

---

### 8.4 Event Updates

Attendee dashboard should show messages/updates for events the attendee is registered for.

Recommended presentation:

- Event name
- Message subject
- Message body preview
- Sent time
- Read/unread state if implemented

---

### 8.5 Registration History

Show past/current event registration records with:

- Event name
- Date
- Registration date
- Status
- Ticket/purchase status if applicable

---

### 8.6 Profile Management

Allow attendee to update:

- Name
- Phone
- Optional address/location
- Notification preference

Email should remain unique.

---

## 9. Required Dashboards

### 9.1 Admin Dashboard

Sections:

1. Summary statistic cards
2. Pending event approvals
3. Recent users
4. Event statistics charts
5. Ticket sales/revenue chart
6. System activity feed
7. Quick navigation to user management and settings

---

### 9.2 Organizer Dashboard

Sections:

1. Summary cards
2. Own event table
3. Upcoming events
4. Registrations chart
5. Ticket sales/revenue chart
6. Recent attendee registrations
7. Quick action to create event

---

### 9.3 Attendee Dashboard

Sections:

1. Upcoming registered events
2. Purchased tickets
3. Event updates
4. Registration history
5. Profile shortcut
6. Browse events shortcut

---

## 10. Database Design

Use normalized relational tables. The following schema is the intended baseline.

### 10.1 `users`

- `id` BIGINT PK AUTO_INCREMENT
- `name` VARCHAR(100) NOT NULL
- `email` VARCHAR(150) UNIQUE NOT NULL
- `password_hash` VARCHAR(255) NOT NULL
- `phone` VARCHAR(20) NULL
- `role` ENUM/VARCHAR(20) NOT NULL
- `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
- `created_at` TIMESTAMP
- `updated_at` TIMESTAMP

---

### 10.2 `events`

- `id` BIGINT PK AUTO_INCREMENT
- `organizer_id` BIGINT FK -> users.id
- `title` VARCHAR(200) NOT NULL
- `description` TEXT NOT NULL
- `category` VARCHAR(100)
- `event_date` DATE NOT NULL
- `start_time` TIME NOT NULL
- `end_time` TIME NOT NULL
- `venue` VARCHAR(255) NOT NULL
- `capacity` INT NOT NULL
- `banner_url` VARCHAR(500) NULL
- `status` VARCHAR(30) NOT NULL
- `rejection_reason` VARCHAR(500) NULL
- `created_at` TIMESTAMP
- `updated_at` TIMESTAMP

---

### 10.3 `tickets`

- `id` BIGINT PK AUTO_INCREMENT
- `event_id` BIGINT FK -> events.id
- `name` VARCHAR(100) NOT NULL
- `description` VARCHAR(500) NULL
- `price` DECIMAL(10,2) NOT NULL
- `quantity` INT NOT NULL
- `sold_quantity` INT NOT NULL DEFAULT 0
- `sales_start_at` DATETIME NULL
- `sales_end_at` DATETIME NULL
- `status` VARCHAR(20) NOT NULL
- `created_at` TIMESTAMP
- `updated_at` TIMESTAMP

---

### 10.4 `registrations`

- `id` BIGINT PK AUTO_INCREMENT
- `event_id` BIGINT FK -> events.id
- `attendee_id` BIGINT FK -> users.id
- `status` VARCHAR(20) NOT NULL
- `registered_at` TIMESTAMP
- `updated_at` TIMESTAMP

Use a unique constraint to prevent duplicate active registration where practical.

---

### 10.5 `ticket_orders`

- `id` BIGINT PK AUTO_INCREMENT
- `attendee_id` BIGINT FK -> users.id
- `event_id` BIGINT FK -> events.id
- `ticket_id` BIGINT FK -> tickets.id
- `quantity` INT NOT NULL
- `unit_price` DECIMAL(10,2) NOT NULL
- `total_amount` DECIMAL(10,2) NOT NULL
- `payment_status` VARCHAR(20) NOT NULL
- `reference_code` VARCHAR(100) UNIQUE NOT NULL
- `created_at` TIMESTAMP

---

### 10.6 `event_messages`

- `id` BIGINT PK AUTO_INCREMENT
- `event_id` BIGINT FK -> events.id
- `organizer_id` BIGINT FK -> users.id
- `subject` VARCHAR(200) NOT NULL
- `message` TEXT NOT NULL
- `created_at` TIMESTAMP

---

### 10.7 `system_settings`

- `id` BIGINT PK AUTO_INCREMENT
- `setting_key` VARCHAR(100) UNIQUE NOT NULL
- `setting_value` TEXT
- `updated_at` TIMESTAMP

---

### 10.8 `activity_logs`

- `id` BIGINT PK AUTO_INCREMENT
- `actor_user_id` BIGINT NULL
- `action_type` VARCHAR(100) NOT NULL
- `entity_type` VARCHAR(100) NULL
- `entity_id` BIGINT NULL
- `description` VARCHAR(500)
- `created_at` TIMESTAMP

---

## 11. JDBC and DAO Rules

JDBC must be visible and central to the implementation.

Use a DAO/repository layer rather than embedding SQL directly in JSPs or Servlets.

### Required principles

- Use `PreparedStatement`
- Never concatenate untrusted user input into SQL
- Use try-with-resources
- Centralize database configuration
- Use transactions for ticket purchase and any multi-step critical write
- Catch SQL exceptions and convert them into meaningful application-level errors
- Keep SQL out of JSP pages

Suggested connection utility:

`DBConnection.java`

Suggested packages:

- `dao`
- `model`
- `service`
- `controller`
- `filter`
- `util`

---

## 12. Java Concepts That Must Be Demonstrated

Because Core Java is heavily graded, use Java properly rather than writing everything as procedural servlet code.

The codebase should visibly demonstrate:

- Classes and objects
- Encapsulation
- Inheritance where meaningful
- Interfaces
- Polymorphism where meaningful
- Enums for roles/statuses
- Collections such as `List`, `Map`, `Set`
- Exception handling
- Utility classes
- Service abstraction
- DAO interfaces and implementations where practical

### Recommended examples

- `User` base model with role-specific behavior only where genuinely useful
- `UserDAO` interface + `UserDAOImpl`
- `EventDAO` interface + `EventDAOImpl`
- `TicketDAO` interface + implementation
- Service classes for business rules
- Enum types such as `UserRole`, `EventStatus`, `PaymentStatus`

Do not force inheritance into places where composition is clearer. The code should look intentional, not like a checklist stitched together by a sleep-deprived committee.

---

## 13. Suggested Project Structure

```text
online-event-management-system/
├── pom.xml
├── README.md
├── .gitignore
├── docs/
│   ├── database-schema.sql
│   ├── sample-data.sql
│   └── diagrams/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/eventmanagement/
│   │   │       ├── controller/
│   │   │       │   ├── auth/
│   │   │       │   ├── admin/
│   │   │       │   ├── organizer/
│   │   │       │   └── attendee/
│   │   │       ├── dao/
│   │   │       ├── dao/impl/
│   │   │       ├── model/
│   │   │       ├── service/
│   │   │       ├── filter/
│   │   │       ├── util/
│   │   │       └── exception/
│   │   ├── resources/
│   │   │   └── db.properties
│   │   └── webapp/
│   │       ├── assets/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
│   │       ├── WEB-INF/
│   │       │   ├── views/
│   │       │   │   ├── auth/
│   │       │   │   ├── admin/
│   │       │   │   ├── organizer/
│   │       │   │   ├── attendee/
│   │       │   │   └── common/
│   │       │   └── web.xml
│   │       └── index.jsp
│   └── test/
│       └── java/
└── CONTEXT.md
```

JSP files that should not be directly reachable should live under `WEB-INF/views` and be forwarded to by Servlets.

---

## 14. Servlet / Route Conventions

Use clear route naming.

### Public/Auth

- `GET /`
- `GET /login`
- `POST /login`
- `GET /register`
- `POST /register`
- `POST /logout`
- `GET /events`
- `GET /events/view?id=...`

### Admin

- `/admin/dashboard`
- `/admin/users`
- `/admin/users/create`
- `/admin/users/edit`
- `/admin/users/delete`
- `/admin/events/pending`
- `/admin/events/view`
- `/admin/events/approve`
- `/admin/events/reject`
- `/admin/settings`
- `/admin/activity`

### Organizer

- `/organizer/dashboard`
- `/organizer/events`
- `/organizer/events/create`
- `/organizer/events/edit`
- `/organizer/events/delete`
- `/organizer/events/submit`
- `/organizer/tickets`
- `/organizer/tickets/create`
- `/organizer/tickets/edit`
- `/organizer/attendees`
- `/organizer/messages`
- `/organizer/messages/send`

### Attendee

- `/attendee/dashboard`
- `/attendee/events`
- `/attendee/register-event`
- `/attendee/tickets`
- `/attendee/purchase`
- `/attendee/updates`
- `/attendee/history`
- `/attendee/profile`

POST must be used for state-changing actions.

---

## 15. Filter / Security Requirements

Use servlet filters for authorization.

Recommended filters:

- `AuthenticationFilter`
- `AdminAuthorizationFilter`
- `OrganizerAuthorizationFilter`
- `AttendeeAuthorizationFilter`

Rules:

- Never trust role information from request parameters
- Read logged-in user identity/role from session
- Validate ownership before organizer actions
- Validate ownership before attendee profile/ticket actions
- Escape output appropriately in JSPs
- Use CSRF protection if practical; if not, at minimum keep all mutation routes POST-only and document the limitation
- Do not expose DB credentials in source control

---

## 16. UI/UX Guidelines

The UI should look like a cohesive product, not unrelated pages assembled from whichever Bootstrap example happened to be open.

### Global design

- Responsive desktop-first dashboard layout
- Left sidebar + top navbar for authenticated dashboards
- Consistent spacing, card styles, buttons, forms, badges, and tables
- Clear role-specific navigation
- Bootstrap icons or Font Awesome icons are acceptable
- Use status badges consistently
- Use toast/alert feedback for actions
- Confirmation modal for delete/reject/cancel actions
- Empty states for no events/tickets/messages
- Form validation messages shown near fields

### Suggested visual hierarchy

- Primary color: blue/indigo
- Neutral white/light gray backgrounds
- Red only for destructive/rejected states
- Green for approved/success states
- Amber for pending states

Do not obsess over fancy animation. Clarity and completeness matter more for this project.

---

## 17. Validation Rules

All important input must be validated server-side even if JavaScript validation exists.

### User

- Name required
- Valid email required
- Email unique
- Password minimum 8 characters
- Valid role

### Event

- Title required
- Description required
- Valid future date when creating
- Start/end time required
- End time after start time
- Venue required
- Capacity > 0

### Ticket

- Name required
- Price >= 0
- Quantity > 0
- Sale end after sale start

### Purchase

- Quantity > 0
- Enough available ticket stock
- Event approved and active
- Ticket active
- Purchase within sale window

---

## 18. Business Rules and Invariants

These rules must remain consistent throughout the project.

1. An event belongs to exactly one organizer.
2. Only the organizer who owns an event may manage it.
3. Only admins approve/reject events.
4. Attendees can only interact with approved events.
5. Ticket inventory must never go below zero.
6. Ticket sold count must never exceed ticket quantity.
7. Purchase totals are calculated server-side.
8. Critical ticket-purchase writes use a JDBC transaction.
9. Duplicate event registration by the same attendee should be prevented.
10. Event messages are only visible to attendees registered for that event.
11. Organizer statistics only include that organizer's own events.
12. Admin statistics may include the entire system.
13. Passwords are never returned/displayed/stored in plain text.
14. JSP pages never directly execute SQL.
15. DAOs perform database access; services enforce business logic; Servlets handle request/response flow.

---

## 19. Error Handling

Create user-friendly handling for:

- Invalid login
- Duplicate email
- Invalid event form
- Unauthorized role access
- Resource not found
- Event not owned by current organizer
- Event not approved
- Sold-out tickets
- Database errors
- Unexpected server errors

Recommended custom exceptions:

- `ValidationException`
- `AuthorizationException`
- `ResourceNotFoundException`
- `BusinessRuleException`
- `DatabaseException`

Do not display raw SQL stack traces to end users.

---

## 20. Seed Data

Provide sample data for demonstration.

Minimum suggested seed users:

- 1 Admin
- 2 Organizers
- 4 Attendees

Minimum suggested event data:

- 2 approved events
- 1 pending event
- 1 draft event
- Multiple ticket types
- Several registrations
- Several ticket orders
- Several event updates/messages

Seed credentials must be clearly documented as demo credentials in README and must not be reused as production secrets.

---

## 21. Presentation / Demo Flow

The final project should support a clean live demo in this order:

1. Login as Organizer
2. Create an event
3. Add ticket types
4. Submit event for approval
5. Logout
6. Login as Admin
7. Review pending event
8. Approve event
9. View admin statistics/activity
10. Logout
11. Login as Attendee
12. Browse approved event
13. Register for event
14. Purchase a ticket using simulated payment
15. View purchased ticket and upcoming event
16. Logout
17. Login as Organizer
18. View new registration/ticket sale
19. Send an event update to attendees
20. Login as Attendee and show received update

This end-to-end flow should work before optional extras are added.

---

## 22. Implementation Phases

Codex should follow this order unless the user explicitly redirects the work.

### Phase 1 — Foundation

- Create Maven WAR project
- Add dependencies
- Configure Tomcat-compatible Jakarta Servlet setup
- Configure MySQL connection
- Create database schema
- Create models/enums/utilities

### Phase 2 — Authentication

- User DAO/service
- Registration
- Login/logout
- Session handling
- Role filters

### Phase 3 — Admin Core

- Admin dashboard
- User management
- Event approval
- Settings
- Activity logs

### Phase 4 — Organizer Core

- Event CRUD
- Submit for approval
- Ticket CRUD
- Attendee list
- Messaging
- Organizer statistics

### Phase 5 — Attendee Core

- Event browsing
- Registration
- Ticket purchase transaction
- Purchased tickets
- Updates
- History
- Profile

### Phase 6 — Dashboard Polish

- Charts
- Search/filter
- Status badges
- Responsive layout
- Error/empty states

### Phase 7 — Finalization

- Seed data
- README
- Database SQL files
- Screenshots
- Test core flows
- Clean repository
- Prepare presentation content

Do not jump to advanced extras before Phases 1–5 work end to end.

---

## 23. Testing Expectations

At minimum, manually verify:

### Authentication

- Correct login
- Wrong password
- Role redirect
- Logout
- Protected route without session

### Admin

- User create/update/deactivate
- Event approve/reject
- Settings update

### Organizer

- Event create/edit
- Submit for approval
- Cannot edit another organizer's event
- Ticket create/update
- Send attendee message

### Attendee

- Browse approved events
- Cannot access unapproved event via manipulated ID
- Register once
- Duplicate registration prevented
- Purchase available ticket
- Sold-out condition handled
- View update only for registered event

### Database

- Foreign keys valid
- Transaction rollback on failed purchase
- No ticket overselling

---

## 24. Optional Enhancements

Only implement these after the required project is complete.

Possible extras:

- QR code on purchased ticket
- Printable ticket
- Event image upload
- Pagination
- CSV export for attendees
- Email notifications
- Attendance check-in
- Feedback/rating system
- Dark mode
- Calendar view

Optional features must never delay or destabilize required rubric features.

---

## 25. Explicit Non-Goals for the Base Version

Do not introduce the following unless requested:

- Real payment gateway
- Microservices
- Spring Boot
- Hibernate/JPA
- React/Vue/Angular SPA
- WebSockets
- Kafka/RabbitMQ
- Redis
- Docker/Kubernetes
- Cloud deployment architecture
- Complex event recommendation engine
- AI features

This is an academic Java Servlet/JDBC project. Adding an enterprise constellation of services would mostly create more ways for the demo to catch fire at exactly the wrong moment.

---

## 26. Coding Standards

- Java classes: `PascalCase`
- Methods/variables: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- DB tables/columns: `snake_case`
- Servlet classes end with `Servlet`
- DAO interfaces end with `DAO`
- DAO implementations end with `DAOImpl`
- Service classes end with `Service`

General rules:

- Keep methods focused
- Avoid giant Servlets
- Avoid business logic in JSP
- Avoid SQL in controllers/views
- Reuse common JSP fragments for navbar/sidebar/alerts
- Comment why, not what
- Remove dead code
- No hard-coded secrets
- No duplicated SQL when reusable DAO methods can handle it

---

## 27. README Requirements

The final README should include:

1. Project overview
2. Features by role
3. Tech stack
4. Folder structure
5. Prerequisites
6. MySQL database setup
7. `db.properties` configuration example
8. Maven build instructions
9. Tomcat deployment instructions
10. Demo credentials
11. Screenshots
12. Database schema overview
13. Main workflows
14. Known limitations
15. Future enhancements

---

## 28. Codex Operating Rules

Whenever Codex is asked to implement or modify this project, follow these rules:

1. Read this `CONTEXT.md` first.
2. Inspect existing code before creating parallel implementations.
3. Preserve the Java Servlet + JSP + JDBC + MySQL architecture.
4. Reuse existing naming and folder conventions.
5. Do not invent new user roles.
6. Do not change event/ticket/registration status semantics without an explicit request.
7. Do not bypass the DAO/service architecture.
8. Do not put database queries in JSP files.
9. Do not weaken role-based authorization.
10. Do not make unapproved events publicly registrable.
11. Do not replace simulated payment with a real gateway unless asked.
12. Keep organizer data ownership checks server-side.
13. Keep all important validation server-side.
14. Update SQL/schema files whenever persistence models change.
15. Update README/setup instructions whenever configuration changes.
16. Prefer completing a working end-to-end flow over adding speculative features.
17. When requirements conflict, prefer this file unless the user's newest explicit instruction overrides it.
18. If a requested change affects multiple modules, update all affected layers consistently: model → DAO → service → servlet → JSP → SQL → docs/tests.
19. Before finalizing a feature, verify navigation, authorization, validation, error handling, and database persistence.
20. Do not silently delete existing features while refactoring.

---

## 29. Definition of Done

The project is considered complete when:

- All three roles can authenticate and see correct dashboards.
- Admin can manage users and approve/reject events.
- Organizer can create/manage events and tickets and communicate with attendees.
- Attendee can browse approved events, register, purchase tickets, receive updates, and manage profile/history.
- JDBC is used cleanly through DAO classes.
- Ticket purchase uses a transaction and prevents overselling.
- Role/ownership authorization works server-side.
- Dashboard statistics are backed by real database queries.
- UI is consistent and responsive.
- SQL schema and sample data are included.
- README contains complete run/setup instructions.
- Repository is clean and presentation-ready.

---

## 30. Current Source Requirement Summary

### Admin

- User Management
- Event Approvals
- System Settings
- Event Statistics
- System Activity Monitoring

### Event Organizer

- Event Creation
- Event Management
- Ticket Management
- Attendee Communication
- Event Statistics
- Upcoming Events

### Attendee

- Event Registration
- Ticket Purchase
- Receive Event Updates
- Upcoming Events
- Ticket Purchases
- Event Updates
- Registration History
- Profile Management

These requirements are mandatory and must stay represented in the final implementation.

---

# Final Instruction to Codex

Build the smallest clean, complete, professional implementation that fully satisfies the mandatory requirements and grading rubric. Keep the architecture understandable enough to explain in a hackathon presentation. Favor correctness, role security, JDBC visibility, clean Java structure, and a reliable end-to-end demo over unnecessary complexity.
