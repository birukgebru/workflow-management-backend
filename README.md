# Enterprise Workflow Management System

An Enterprise Workflow Management System is a backend platform. The platrom designed to handle full lifecycle of busines workflows. This system also manages role-based operations and audit tracking. 

The system demonstrates enterprise backend engineering concepts. Implements JWT authentication and RBAC for security. In additon  to workflow management, it handles audit logs. Ensures transactional integrity and have REST API documentation.

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
- Audit logs and action traceability

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

## ARCHITECTURE

The application follows layered modular monolith architecture (auth, workflow, audit, shared, config). Each module is separated into controller, service, repository, dto, mapper, entity.

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

1. git clone https://github.com/birukgebru/workflow-management-backend.git

2. cd workflow-management-backend.git

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

### Swagger-ui
![Swagger-ui](docs/images/swagger-ui-1.png)

### Workflow Endpoints
![Workflow-endpoints](docs/images/swagger-ui-workflow-management.png)

### ApiResponse Structure and Tokens (AccessToken & RefreshToken)
![ApiResponse-and-Tokens](docs/images/swagger-ui-api-response-structure-and-tokens.png)

### Authorization
![Authorization](docs/images/swagger-ui-authorization.png)


## FUTURE IMPROVEMENTS

- Email notifications
- Workflow attachments
- Dashboard analytics
- Docker Compose deployment
- CI/CD pipeline
- Workflow configuration engine


