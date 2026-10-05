# EventFlow — Online Event Management System

EventFlow is a role-based Java web application for managing events, ticket inventory, registrations, attendee updates, and administrative approvals.

It intentionally uses the project rubric's visible architecture:

- Java 17+
- Jakarta Servlets 6
- JSP + JSTL
- JDBC
- MySQL 8+
- Maven WAR
- Apache Tomcat 10+
- Bootstrap 5, custom CSS, Vanilla JavaScript, and Chart.js-ready dashboard styling

## Roles

| Role | Main capabilities |
|---|---|
| Admin | Manage users, approve/reject events, manage settings, inspect activity |
| Organizer | Create events, manage tickets, inspect attendees, broadcast updates |
| Attendee | Discover events, register, purchase simulated tickets, view updates/history/profile |

## Project structure

The application is one Maven WAR project. The request flow is:

```text
JSP → Servlet → Service → DAO → JDBC/MySQL
```

See [FOLDER_STRUCTURE.md](FOLDER_STRUCTURE.md) for the folder responsibilities and feature workflow.

## Prerequisites

- JDK 17 or newer
- Maven 3.9+
- MySQL 8+
- Apache Tomcat 10.1+

## Database setup

1. Create the schema:

   ```bash
   mysql -u root -p < docs/database-schema.sql
   ```

2. Copy `src/main/resources/db.properties.example` to `src/main/resources/db.properties`.

3. Update the local database username and password. `db.properties` is ignored by Git.

4. Load the demonstration data:

   ```bash
   mysql -u root -p event_management < docs/sample-data.sql
   ```

## Build and run

```bash
mvn clean package
```

Copy `target/event-management.war` to Tomcat's `webapps` directory, start Tomcat, and open:

```text
http://localhost:8080/event-management/
```

## Automatic Windows setup

For a Windows machine with no Java, Maven, MySQL, or Tomcat installed:

1. Extract the ZIP anywhere.
2. Double-click `start-eventflow.cmd`.
3. Allow the script to access the internet when Windows asks.

The launcher finds its own project folder, downloads missing tools, creates a private local MySQL data directory, loads the schema and demo data, builds the WAR, starts Tomcat, and opens the application. Tools and runtime files are stored under `%LOCALAPPDATA%\EventFlow`, not inside the project ZIP. The setup script automatically chooses another local port if 8080 or 3306 is already in use.

If Windows blocks scripts, use the included `start-eventflow.cmd`; it invokes PowerShell with a process-only execution-policy bypass and does not change the machine policy.

## Demo credentials

All seed accounts use the password `password` for demonstration only.

```text
Admin:     admin@eventflow.local
Organizer: organizer@eventflow.local
Attendee:  jordan@eventflow.local
```

Never use these credentials outside a local demo environment.

## Core demo flow

1. Sign in as the organizer.
2. Create an event and add ticket types.
3. Submit the event for approval.
4. Sign in as the admin and approve it.
5. Sign in as the attendee, browse the approved event, register, and purchase a simulated ticket.
6. Return to the organizer workspace to inspect the registration and send an attendee update.
7. Return to the attendee workspace to view the ticket and update.

## Security and data rules

- Passwords are BCrypt-hashed.
- Session identity and role are read from `HttpSession`, never trusted from request parameters.
- Role filters protect admin, organizer, and attendee routes.
- Organizer ownership is checked server-side before mutations.
- Only approved events are publicly registrable or purchasable.
- SQL uses prepared statements inside DAO implementations.
- Ticket purchase stock updates and order creation run inside a JDBC transaction.

## Known setup note

The implementation can be inspected without Java installed, but Maven packaging and Tomcat verification require the prerequisites above. Track verification progress in [PROJECT_PROGRESS.md](PROJECT_PROGRESS.md).

## Documentation

- [CONTEXT.md](CONTEXT.md) — source requirements and constraints
- [FOLDER_STRUCTURE.md](FOLDER_STRUCTURE.md) — project organization guide
- [PROJECT_PROGRESS.md](PROJECT_PROGRESS.md) — phase/task/subtask tracker
- [docs/database-schema.sql](docs/database-schema.sql) — schema
- [docs/sample-data.sql](docs/sample-data.sql) — demo data
