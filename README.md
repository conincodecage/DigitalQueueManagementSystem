# Digital Queue Management System (QueueEase)

A beginner-friendly AIP mini-project for managing walk-in queues at campus offices. It is implemented with **HTML, CSS, Java Servlets, JSP, JDBC and MySQL** and is intended for **Apache Tomcat 9** (the `javax.servlet` API).

## What the project does

### Student/public side
- Generate a token by entering a name and selecting a campus service.
- Get a token number such as `STA-001`, `ACC-002` or `EXM-003`.
- Check a token's status: `WAITING`, `CALLED`, `SERVED` or `SKIPPED`.
- View the current token being served and the number of people waiting for each service.

### Staff side
- Sign in to a staff dashboard.
- Select a service desk.
- Call the next waiting token in number order.
- Mark a called token as served or skip it.
- View currently open tokens.

A staff counter cannot call a second token while another token is still marked `CALLED`; the active token must first be served or skipped.

## Technology mapping to the AIP syllabus

| Syllabus topic | Where it is used |
|---|---|
| HTML forms | Token generation, status lookup and staff login forms |
| GET and POST | GET for pages/dashboard; POST for token creation, login and queue actions |
| Servlet architecture / servlet lifecycle | Servlet classes handle each URL and call the data-access layer |
| `HttpServletRequest` / `HttpServletResponse` | Read form parameters and forward/redirect to pages |
| `HttpSession` | Keeps staff logged in and protects staff-only endpoints |
| JSP directives, expressions and implicit objects | JSP view pages use page directives, request/context objects and JSP expressions/scriptlets for display |
| JDBC and prepared statements | `QueueDao` reads/writes MySQL through prepared statements |
| CRUD/database operations | Create ticket, read status/queue, update ticket status |
| Web application server | Deploy and run on Apache Tomcat 9 |

The project does **not** require Spring, Hibernate, REST APIs or third-party UI frameworks. Those topics can be studied separately from this core project.

## Prerequisites

- JDK 8 or later (JDK 11 or 17 is also fine for Tomcat 9)
- Apache Maven 3.8+
- MySQL Server 8.x
- Apache Tomcat 9.x
- NetBeans is optional; the project is a standard Maven WAR project.

> Important: This version uses `javax.servlet.*`, so use **Tomcat 9**, not Tomcat 10/11. Tomcat 10+ uses `jakarta.servlet.*` and would require code/dependency changes.

## Setup

### 1. Create the database

Open MySQL Workbench or a MySQL command prompt and run the entire `DATABASE_SETUP.sql` file. It creates the `digital_queue` database and these tables:

- `token_counters`: issues sequential token numbers separately for each service each day.
- `queue_tickets`: stores the name, service, token number, status and timestamps.

### 2. Configure database credentials

The defaults in `src/main/java/com/aip/queue/config/Db.java` are:

- URL: `jdbc:mysql://localhost:3306/digital_queue...`
- User: `root`
- Password: empty string

If your local MySQL root password is not empty, set `DB_PASSWORD` in the environment where Tomcat is launched. You can also set `DB_USER` and `DB_URL` if your local setup differs. For example, on Windows Command Prompt, before starting Tomcat from the same window:

```bat
set DB_USER=root
set DB_PASSWORD=your_mysql_password
set DB_URL=jdbc:mysql://localhost:3306/digital_queue?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
```

Do not commit real passwords to a repository.

### 3. Build the WAR file

Open a terminal in the folder that contains `pom.xml` and run:

```bash
mvn clean package
```

The deployable file will be created at:

```text
target/digital-queue.war
```

Maven needs internet access the first time it downloads project dependencies.

### 4. Deploy to Tomcat 9

Copy `target/digital-queue.war` into Tomcat's `webapps` folder, start Tomcat, and open:

```text
http://localhost:8080/digital-queue/
```

If you use NetBeans, open the directory as a Maven project, configure a Tomcat 9 server, build the WAR, and deploy it to that server.

### 5. Open the staff dashboard

Use the classroom demonstration credentials:

```text
Username: staff
Password: Queue@123
```

You may override them with environment variables `QUEUE_ADMIN_USER` and `QUEUE_ADMIN_PASSWORD` before Tomcat starts. The defaults are for a classroom demo only, not a production deployment.

## Quick test

1. Open the home page and create a token for **Student Affairs**.
2. Create another token for the same service; the number should increment.
3. Open **Check token status** and enter the service plus the numeric part of the token (for `STA-001`, enter `1`).
4. Sign in to the staff dashboard.
5. Select Student Affairs and press **Call next waiting token**.
6. Check the public status again; the ticket should show `CALLED`.
7. Mark it served, then check again; it should show `SERVED`.
8. Test other services to confirm they use separate token sequences.

Tickets and counters are scoped to the database's current date. If the public page shows a database error, check MySQL is running, `DATABASE_SETUP.sql` has been executed, and `DB_USER`/`DB_PASSWORD` match your local setup.

## Important scope / limitations

- This is an academic prototype, not a production queue product.
- Staff login has a single configurable demo account and should be upgraded before real deployment.
- It has no SMS/email notifications, payment, multiple staff roles, or live push updates. Refresh the page to see recent queue changes.
- A database administrator should create a restricted MySQL account instead of using the root account for deployment.
