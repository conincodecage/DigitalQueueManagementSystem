# DIGITAL QUEUE MANAGEMENT SYSTEM
## Advanced Internet Programming — Mini Project Report

**Project title:** Digital Queue Management System (QueueEase)  
**Technology:** HTML, CSS, Java Servlets, JSP, JDBC, MySQL and Apache Tomcat 9  
**Project type:** Syllabus-aligned Java web application

## 1. Abstract

The Digital Queue Management System is a web application for organizing walk-in queues at campus offices such as Student Affairs, Accounts, the Examination Cell and IT Support. A visitor enters their name, selects a service and receives a digital token. Visitors can check whether a token is waiting, called, served or skipped. Staff can sign in, select a counter, call the next waiting token, and update the token's status. The application demonstrates the use of HTML forms, HTTP GET and POST, Servlets, JSP, HttpSession, JDBC and relational database operations.

## 2. Problem statement

In a manual queue, visitors may need to stand near a counter to learn when their turn is approaching. Staff may also have difficulty keeping the waiting order clear. This project demonstrates a simple digital alternative: a visitor receives a numbered token and can check its status, while staff members operate the queue from a dashboard.

## 3. Objectives

1. Generate a unique daily token number for each service.
2. Store token information in a MySQL database.
3. Let visitors check token status online.
4. Let staff call the next waiting token and mark it served or skipped.
5. Apply Java Servlet, JSP, JDBC, HTTP request methods and session management concepts from the AIP syllabus.

## 4. Scope

The application supports four sample services: Student Affairs, Accounts Office, Examination Cell and IT Support Desk. Token sequences are separate for each service and restart on a new database date. It is a small educational prototype and does not include notification services, a mobile application or production-grade staff identity management.

## 5. Main modules

### 5.1 Public home and queue board

Displays token-generation form and current queue summaries. The form sends a POST request to a Servlet. The Servlet validates inputs and asks the JDBC data-access class to create a ticket.

### 5.2 Token generation

The service's daily counter is incremented in a database transaction. The generated token record is stored with `WAITING` status. The visitor receives a token slip, for example `STA-001`.

### 5.3 Token status lookup

The visitor chooses a service and enters the numeric part of their token. A Servlet reads the parameters, queries MySQL using a prepared statement, and forwards the resulting ticket to a JSP page.

### 5.4 Staff login and session

The staff login Servlet checks the configured demonstration credentials. A successful login creates an HTTP session and stores the `staffUser` attribute. Staff-only Servlets check this session attribute before changing the queue. The session expires after 20 minutes of inactivity.

### 5.5 Queue dashboard

The staff member chooses a service and calls the next waiting token. A ticket changes from `WAITING` to `CALLED`. The current token must be marked `SERVED` or `SKIPPED` before the next one can be called.

## 6. System architecture

```text
Visitor / Staff Browser
          |
          | HTTP GET / POST
          v
Java Servlets (Controller)
          |
          | call data-access methods
          v
QueueDao (JDBC + PreparedStatement)
          |
          v
MySQL Database

Servlet sets request attributes and forwards to JSP views
          |
          v
JSP page produces HTML response
          |
          v
Browser
```

The Servlet processes the request and business flow, `QueueDao` performs database operations, and JSP pages render data returned by the Servlet.

## 7. Database design

### Table: `token_counters`

| Column | Purpose |
|---|---|
| `service_type` | Service name; part of the primary key |
| `queue_date` | Date for which tokens are numbered |
| `last_number` | Last number issued for that service and date |

### Table: `queue_tickets`

| Column | Purpose |
|---|---|
| `id` | Internal auto-increment key |
| `service_type` | Selected campus service |
| `token_number` | User-facing number within that service/day |
| `customer_name` | Visitor's entered name |
| `status` | `WAITING`, `CALLED`, `SERVED` or `SKIPPED` |
| `queue_date` | Date for the queue |
| `created_at` | Time the token was issued |
| `called_at` | Time staff called the token |
| `served_at` | Time the token was marked served |

A unique constraint on `(service_type, queue_date, token_number)` prevents duplicate token numbers for a service on the same date.

## 8. Technologies used

- **HTML:** form fields, buttons, tables and page layout.
- **CSS:** page styling and responsive design.
- **Java Servlets:** read request parameters, validate input, access the DAO, forward requests and redirect after actions.
- **JSP:** generate dynamic HTML using request attributes and JSP expressions/scriptlets.
- **HttpSession:** maintain staff sign-in between requests.
- **JDBC:** connect Java to MySQL and execute SQL statements.
- **PreparedStatement:** safely pass form values as SQL parameters.
- **MySQL:** persist counters and ticket records.
- **Apache Tomcat 9:** run the Java web application.

## 9. Mapping to the AIP syllabus

| AIP syllabus item | Project demonstration |
|---|---|
| Client-server architecture | Browser sends HTTP requests to Tomcat; server returns HTML |
| Servlet architecture and lifecycle | Annotated Servlet classes process application routes |
| `HttpServletRequest` and `HttpServletResponse` | Read submitted parameters, forward and redirect |
| GET and POST | GET for displaying pages; POST for state-changing operations |
| HttpSession | Staff login state and restricted staff actions |
| JSP directives, scripting and implicit objects | JSP page directive, expressions, scriptlets, `request` and `session` |
| JDBC connection and prepared statements | `Db` and `QueueDao` classes |
| CRUD operations | Create tickets, read queues/status, update ticket status |
| Web server / Tomcat | Application deployment and runtime |

The project focuses on topics explicitly covered in Units 1 and 2. Spring and Hibernate are not required for this version.

## 10. Sample test cases

| Test | Action | Expected result |
|---|---|---|
| T1 | Submit token form with a valid name and service | Token slip appears with `WAITING` status |
| T2 | Submit two tokens for one service on the same date | The second token number is one greater than the first |
| T3 | Choose another service and create a token | That service has its own token sequence |
| T4 | Check a valid service/token pair | Correct token and status are shown |
| T5 | Check an unknown token number | A not-found message appears |
| T6 | Open staff dashboard without signing in | Browser is redirected to staff login |
| T7 | Sign in with configured classroom credentials | Staff dashboard opens |
| T8 | Call next while there is a waiting token and no active token | Lowest waiting token changes to `CALLED` |
| T9 | Try to call next while a token is active | System tells staff to serve or skip the active token first |
| T10 | Mark a called token as served | Status changes to `SERVED` |
| T11 | Enter a blank name | Form validation prevents token creation |

## 11. Limitations and future enhancement

The prototype uses a single configurable staff login, and users refresh the page to see updates. Future extensions could include hashed staff passwords in a user table, role-based access, live updates, a printed QR-code ticket, estimated waiting time, SMS notifications and deployment behind HTTPS. These are not necessary for the core syllabus project.

## 12. Conclusion

The Digital Queue Management System demonstrates how Java web technologies can solve a simple real-life service problem. It connects browser forms to Servlets, renders results using JSP, stores queue information with JDBC and MySQL, and uses HttpSession to protect staff operations. The project is intentionally limited in scope so the core implementation can be understood and explained during a practical demonstration or viva.
