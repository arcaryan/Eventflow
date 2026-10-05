# Folder Structure Guide

This project should use one simple Maven WAR application. Unlike a React + Express project, the frontend and backend do not need separate `apps/web` and `apps/api` applications.

The frontend JSP files, Java backend, database scripts, and static assets can live in one project because Tomcat runs the complete application.

## Recommended structure

```text
event-management-system/
├── pom.xml
├── README.md
├── CONTEXT.md
├── FOLDER_STRUCTURE.md
├── .gitignore
│
├── docs/
│   ├── database-schema.sql
│   ├── sample-data.sql
│   └── diagrams/
│
└── src/
    ├── main/
    │   ├── java/com/eventmanagement/
    │   │   ├── controller/
    │   │   │   ├── auth/
    │   │   │   ├── admin/
    │   │   │   ├── organizer/
    │   │   │   └── attendee/
    │   │   │
    │   │   ├── model/
    │   │   ├── dao/
    │   │   │   └── impl/
    │   │   ├── service/
    │   │   ├── filter/
    │   │   ├── exception/
    │   │   └── util/
    │   │
    │   ├── resources/
    │   │   └── db.properties.example
    │   │
    │   └── webapp/
    │       ├── index.jsp
    │       ├── assets/
    │       │   ├── css/
    │       │   ├── js/
    │       │   └── images/
    │       └── WEB-INF/
    │           ├── web.xml
    │           └── views/
    │               ├── common/
    │               ├── auth/
    │               ├── admin/
    │               ├── organizer/
    │               └── attendee/
    │
    └── test/java/com/eventmanagement/
```

## What belongs in each folder

### `controller/`

Servlets that receive HTTP requests, call services, and forward to JSP pages.

Examples:

```text
LoginServlet.java
AdminDashboardServlet.java
CreateEventServlet.java
PurchaseTicketServlet.java
```

Controllers should not contain large SQL queries or complicated business rules.

### `model/`

Plain Java objects representing application data and enums.

Examples:

```text
User.java
Event.java
Ticket.java
Registration.java
TicketOrder.java
EventMessage.java
UserRole.java
EventStatus.java
PaymentStatus.java
```

### `dao/`

Database operations and DAO interfaces.

Examples:

```text
UserDAO.java
EventDAO.java
TicketDAO.java
```

### `dao/impl/`

JDBC implementations of the DAO interfaces.

Examples:

```text
UserDAOImpl.java
EventDAOImpl.java
TicketDAOImpl.java
```

All SQL should remain here. Use `PreparedStatement` and try-with-resources.

### `service/`

Business rules and workflows that may involve multiple DAOs.

Examples:

```text
AuthService.java
EventService.java
TicketService.java
RegistrationService.java
PurchaseService.java
```

For example, `PurchaseService` should validate stock, calculate the total, create the order, update sold quantity, and manage the JDBC transaction.

### `filter/`

Authentication and authorization filters.

Examples:

```text
AuthenticationFilter.java
AdminAuthorizationFilter.java
OrganizerAuthorizationFilter.java
AttendeeAuthorizationFilter.java
```

### `exception/`

Application-specific exceptions.

Examples:

```text
ValidationException.java
AuthorizationException.java
ResourceNotFoundException.java
BusinessRuleException.java
DatabaseException.java
```

### `util/`

Small shared utilities and configuration helpers.

Examples:

```text
DBConnection.java
PasswordUtil.java
ValidationUtil.java
DateTimeUtil.java
```

Do not put business logic into utility classes.

### `resources/`

Configuration files loaded by the Java application.

Keep real database credentials in a local `db.properties` file that is ignored by Git. Commit only `db.properties.example`.

### `webapp/assets/`

Static browser files:

```text
css/       Custom styles
js/        Small page interactions and validation
images/    Local image assets
```

Bootstrap and Chart.js can be loaded from a CDN or stored locally, depending on the project setup.

### `WEB-INF/views/`

JSP pages that should be opened through Servlets rather than directly by a browser.

Use this layout:

```text
views/
├── common/
│   ├── header.jsp
│   ├── navbar.jsp
│   ├── sidebar.jsp
│   ├── alerts.jsp
│   └── footer.jsp
├── auth/
├── admin/
├── organizer/
└── attendee/
```

JSP files should display data and submit forms. They should not connect to the database or contain business rules.

## Request flow

Every feature should generally follow this path:

```text
Browser
  → Servlet/controller
  → Service
  → DAO implementation
  → MySQL
  → Servlet sets request attributes
  → JSP renders the response
```

For example, creating an event:

```text
create-event.jsp
  → CreateEventServlet
  → EventService
  → EventDAOImpl
  → events table
```

## Where to add a new feature

For a normal feature, add files in this order:

1. Add or update the model in `model/`.
2. Add DAO methods in `dao/` and `dao/impl/`.
3. Put validation and business rules in `service/`.
4. Add a Servlet in the relevant `controller/` subfolder.
5. Add the JSP page in the relevant `WEB-INF/views/` subfolder.
6. Add CSS or JavaScript only when the page needs it.
7. Update SQL files if the database structure changes.
8. Update `README.md` if setup or usage changes.

Example for ticket purchase:

```text
model/TicketOrder.java
dao/TicketOrderDAO.java
dao/impl/TicketOrderDAOImpl.java
service/PurchaseService.java
controller/attendee/PurchaseTicketServlet.java
WEB-INF/views/attendee/purchase.jsp
```

## Simple naming rules

| Item | Convention | Example |
|---|---|---|
| Java class | PascalCase | `EventService` |
| Servlet | Ends with `Servlet` | `LoginServlet` |
| DAO interface | Ends with `DAO` | `EventDAO` |
| DAO implementation | Ends with `DAOImpl` | `EventDAOImpl` |
| Service | Ends with `Service` | `PurchaseService` |
| JSP file | lowercase-kebab-case or clear lowercase name | `create-event.jsp` |
| Database table/column | snake_case | `ticket_orders`, `created_at` |
| Java variable/method | camelCase | `findById` |
| Constant | UPPER_SNAKE_CASE | `MAX_TICKETS` |

## What not to do

- Do not create separate React and Express applications for this project.
- Do not put SQL in JSP files.
- Do not put SQL directly in Servlets.
- Do not put business rules in JSP pages.
- Do not allow JSP files under `WEB-INF/views/` to be directly opened.
- Do not create one giant Servlet for an entire role.
- Do not create a separate package for every small class.
- Do not add a new framework unless the project requirements are intentionally changed.

## Practical starting point

You do not need to create every folder immediately. Start with:

```text
src/main/java/com/eventmanagement/
├── controller/auth/
├── dao/
├── dao/impl/
├── model/
├── service/
├── filter/
└── util/

src/main/webapp/WEB-INF/views/
├── common/
└── auth/
```

Add `admin/`, `organizer/`, and `attendee/` folders when those features are implemented. This keeps the project understandable while still leaving room for the complete required functionality.

## React and Express comparison

| React + Express habit | Servlet/JSP equivalent |
|---|---|
| React component | JSP page or JSP fragment |
| React Router route | Servlet URL mapping |
| Express controller | Servlet |
| Express service | Java service class |
| Express model/repository | Java model + DAO |
| Middleware | Servlet filter |
| `.env` configuration | `db.properties` |
| API response JSON | Request attributes forwarded to JSP |
| `npm run build` | `mvn package` |
| Node server | Tomcat |

The main idea is to keep each layer small: Servlets handle web requests, services handle decisions, DAOs handle JDBC, and JSPs handle presentation.
