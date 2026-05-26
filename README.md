# Workflow Management System

Enterprise Workflow Management System is a backend-driven platform designed to manage approval workflows, role-based processing, audit tracking, and workflow lifecycle management.

The system demonstrates enterprise backend engineering concepts including JWT authentication, RBAC, workflow orchestration, audit logging, transactional integrity, and REST API documentation.

## FEATURES
Authentication
- JWT authentication
- Refresh token flow
- Stateless security
- Password hashing

Authorization
- RBAC
- Admin/Reviewer/Approver roles

Workflow Engine
- Submit workflow requests
- Review/Approve/Reject requests
- Workflow status tracking

Auditability
- Workflow history tracking
- Audit logs
- Action traceability

API
- RESTful API
- Swagger/OpenAPI documentation

## TECH STACK

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT Authentication
- Swagger/OpenAPI
- Maven

## Architecture

The application follows layered modular monolith architecture (auth, workflow, audit, shared, config) each separated into controller, service, repository, dto, mapper, entity.

![Architecture Diagram](docs/images/architecture-diagram-layered.png)

## PROJECT STRUCTURE

```text
src/main/java/com/workflowsystem/demo 
├───audit
│   ├───controller
│   ├───dto
│   ├───entitiy
│   ├───mapper
│   ├───repository
│   └───service
├───auth
│   ├───controller
│   ├───dto
│   ├───entity
│   ├───enums
│   ├───mapper
│   ├───repository
│   ├───security
│   └───service
├───config
├───shared
│   ├───controller
│   ├───exception
│   └───response
└───workflow
    ├───controller
    ├───dto
    ├───entity
    ├───enums
    ├───mapper
    ├───repository
    └───service
        └───impl
```

## AUTHENTICATION FLOW

1. User registers account
2. User logs in
3. JWT access token issued
4. Refresh token stored securely
5. Protected endpoints require Bearer token
6. Role-based access enforced

## CORE ENTITITES

- Users
- Roles
- Workflow Requests
- Workflow History
- Audit Logs
- Refresh Tokens

## SETUP INSTRUCTIONS

### Requirements
- Java 17
- PostgreSQL
- Maven

### Installations 

1. git clone https://github.com/damienbeaufils/spring-boot-clean-architecture-demo.git

2. cd spring-boot-clean-architecture-demo

### Run Application 
- mvn spring-boot:run

### Create Database

```sql
CREATE DATABASE workflow_db;
```

### Configure Application

Copy the example configuration file:

```bash
cp src/main/resources/application-example.yml src/main/resources/application-dev.yml
```

Update the following values:

- PostgreSQL username/password
- JWT secret key

### Run Application

```bash
mvn spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

## API DOCUMENTATION

```text
http://localhost:8080/swagger-ui/index.html
```

## FUTURE IMPROVEMENTS

- Email notifications
- Workflow attachments
- Dashboard analytics
- Docker Compose deployment
- CI/CD pipeline
- Workflow configuration engine


