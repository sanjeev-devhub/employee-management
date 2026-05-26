# Employee Management System

Enterprise-grade Spring Boot REST API for managing employees, departments, titles, salaries, and managers.

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.5**
- **Spring Data JPA + Hibernate**
- **PostgreSQL**
- **MapStruct**
- **Lombok**
- **SpringDoc OpenAPI (Swagger)**
- **JUnit 5 + Mockito**

---

## Quick Start (IntelliJ IDEA)

### Prerequisites
- Java 21 (JDK)
- Maven 3.9+
- PostgreSQL 15+
- IntelliJ IDEA (2023.1+)

### 1. Clone / Extract
Extract the zip and open the `employee-management` folder in IntelliJ IDEA:
`File → Open → select the employee-management folder`

### 2. Set up PostgreSQL
Create the database:
```sql
CREATE DATABASE employee_db;
```
Or run the full schema with sample data:
```bash
psql -U postgres -f src/main/resources/schema.sql
```

### 3. Configure database credentials
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/employee_db
    username: postgres      # change if different
    password: postgres      # change if different
```

### 4. Enable Annotation Processing in IntelliJ
`Settings → Build, Execution, Deployment → Compiler → Annotation Processors`
→ Check **Enable annotation processing**

### 5. Run the application
- Open `EmployeeManagementApplication.java`
- Click the green ▶ Run button
- Or via terminal: `./mvnw spring-boot:run`

### 6. Access Swagger UI
Open your browser: `http://localhost:8080/swagger-ui.html`

---

## Running with Docker Compose

```bash
# Build and start all services
docker-compose up --build

# Stop
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

---

## Running Tests

```bash
# All tests
./mvnw test

# Unit tests only
./mvnw test -Dtest="*Test"

# Integration tests only
./mvnw test -Dtest="*IntegrationTest"
```

---

## Project Structure

```
src/main/java/com/company/employeemanagement/
├── config/
│   ├── OpenApiConfig.java          # Swagger configuration
│   └── JacksonConfig.java          # JSON serialization config
├── controller/
│   ├── EmployeeController.java
│   ├── DepartmentController.java
│   ├── TitleController.java
│   ├── SalaryController.java
│   └── ManagerController.java
├── dto/
│   ├── request/
│   │   ├── EmployeeRequest.java
│   │   ├── DepartmentRequest.java
│   │   ├── TitleRequest.java
│   │   ├── SalaryRequest.java
│   │   └── ManagerAssignRequest.java
│   └── response/
│       ├── ApiResponse.java
│       ├── PageResponse.java
│       ├── EmployeeResponse.java
│       ├── DepartmentResponse.java
│       ├── TitleResponse.java
│       ├── SalaryResponse.java
│       └── ManagerResponse.java
├── entity/
│   ├── Employee.java
│   ├── Department.java
│   ├── Title.java
│   ├── Salary.java
│   ├── DeptEmpId.java
│   └── DeptManagerId.java
├── enums/
│   └── Gender.java
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   └── GlobalExceptionHandler.java
├── mapper/
│   ├── EmployeeMapper.java
│   ├── DepartmentMapper.java
│   └── TitleMapper.java
├── repository/
│   ├── EmployeeRepository.java
│   ├── DepartmentRepository.java
│   ├── TitleRepository.java
│   └── SalaryRepository.java
├── service/
│   ├── EmployeeService.java
│   ├── DepartmentService.java
│   ├── TitleService.java
│   ├── SalaryService.java
│   ├── ManagerService.java
│   └── impl/
│       ├── EmployeeServiceImpl.java
│       ├── DepartmentServiceImpl.java
│       ├── TitleServiceImpl.java
│       ├── SalaryServiceImpl.java
│       └── ManagerServiceImpl.java
└── specification/
    └── EmployeeSpecification.java
```

---

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/employees` | Create employee |
| PUT | `/api/v1/employees/{id}` | Update employee |
| DELETE | `/api/v1/employees/{id}` | Delete employee |
| GET | `/api/v1/employees/{id}` | Get employee by ID |
| GET | `/api/v1/employees` | Get all employees (paginated) |
| GET | `/api/v1/employees/search` | Search employees |
| POST | `/api/v1/departments` | Create department |
| PUT | `/api/v1/departments/{id}` | Update department |
| DELETE | `/api/v1/departments/{id}` | Delete department |
| GET | `/api/v1/departments/{id}` | Get department by ID |
| GET | `/api/v1/departments` | Get all departments |
| GET | `/api/v1/departments/{deptNo}/employees` | Get employees by dept |
| POST | `/api/v1/titles` | Create title |
| PUT | `/api/v1/titles/{id}` | Update title |
| DELETE | `/api/v1/titles/{id}` | Delete title |
| GET | `/api/v1/titles/{id}` | Get title by ID |
| GET | `/api/v1/titles` | Get all titles |
| PUT | `/api/v1/salaries/{empNo}` | Update salary |
| GET | `/api/v1/salaries/{empNo}` | Get salary |
| POST | `/api/v1/managers/assign` | Assign manager |
| DELETE | `/api/v1/managers/remove` | Remove manager |
| GET | `/api/v1/managers` | Get all managers |
| GET | `/api/v1/managers/departments/{deptNo}` | Get managers by dept |

---

## Search Parameters

`GET /api/v1/employees/search`

| Param | Type | Description |
|-------|------|-------------|
| `firstName` | String | Filter by first name (partial match) |
| `lastName` | String | Filter by last name (partial match) |
| `department` | String | Filter by department name (partial match) |
| `title` | String | Filter by title name (partial match) |
| `page` | int | Page number (default: 0) |
| `size` | int | Page size (default: 10) |
| `sort` | String | Sort field and direction e.g. `firstName,asc` |

---

## Common Issues

### Annotation Processing (MapStruct)
If you see `Cannot find symbol` errors for mappers, ensure annotation processing is enabled:
`Settings → Build → Compiler → Annotation Processors → Enable annotation processing`

Then rebuild: `Build → Rebuild Project`

### PostgreSQL Connection
If the app fails to connect, verify PostgreSQL is running on port 5432 and the `employee_db` database exists.

### Port Conflict
If port 8080 is in use, change it in `application.yml`:
```yaml
server:
  port: 8081
```
