# Employee Management System - Sample API Requests & Responses

Base URL: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## TITLES

### Create Title
**POST** `/api/v1/titles`
```json
// Request
{
  "titleId": "T001",
  "title": "Senior Engineer"
}

// Response 201 Created
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 201,
  "message": "Title created successfully",
  "data": {
    "titleId": "T001",
    "title": "Senior Engineer"
  }
}
```

### Get All Titles
**GET** `/api/v1/titles?page=0&size=10&sort=titleId,asc`
```json
// Response 200 OK
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 200,
  "message": "Titles fetched successfully",
  "data": {
    "content": [
      { "titleId": "T001", "title": "Senior Engineer" }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

---

## DEPARTMENTS

### Create Department
**POST** `/api/v1/departments`
```json
// Request
{
  "deptNo": "D005",
  "deptName": "Development"
}

// Response 201 Created
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 201,
  "message": "Department created successfully",
  "data": {
    "deptNo": "D005",
    "deptName": "Development"
  }
}
```

### Get Employees by Department
**GET** `/api/v1/departments/D005/employees?page=0&size=10`
```json
// Response 200 OK
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 200,
  "message": "Employees fetched successfully",
  "data": {
    "content": [
      {
        "empNo": 10001,
        "titleId": "T004",
        "titleName": "Senior Engineer",
        "firstName": "Georgi",
        "lastName": "Facello",
        "sex": "MALE",
        "birthDate": "1953-09-02",
        "hireDate": "1986-06-26",
        "salary": 60117,
        "departments": ["Development"]
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

---

## EMPLOYEES

### Create Employee
**POST** `/api/v1/employees`
```json
// Request
{
  "empNo": 10001,
  "titleId": "T004",
  "birthDate": "1990-05-15",
  "firstName": "John",
  "lastName": "Doe",
  "sex": "MALE",
  "hireDate": "2020-01-10"
}

// Response 201 Created
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 201,
  "message": "Employee created successfully",
  "data": {
    "empNo": 10001,
    "titleId": "T004",
    "titleName": "Senior Engineer",
    "firstName": "John",
    "lastName": "Doe",
    "sex": "MALE",
    "birthDate": "1990-05-15",
    "hireDate": "2020-01-10",
    "salary": null,
    "departments": []
  }
}
```

### Update Employee
**PUT** `/api/v1/employees/10001`
```json
// Request
{
  "empNo": 10001,
  "titleId": "T006",
  "birthDate": "1990-05-15",
  "firstName": "John",
  "lastName": "Doe",
  "sex": "MALE",
  "hireDate": "2020-01-10"
}

// Response 200 OK
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 200,
  "message": "Employee updated successfully",
  "data": { ... }
}
```

### Search Employees
**GET** `/api/v1/employees/search?firstName=John&department=Development&page=0&size=5&sort=lastName,asc`
```json
// Response 200 OK
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 200,
  "message": "Employees search completed",
  "data": {
    "content": [ ... ],
    "pageNumber": 0,
    "pageSize": 5,
    "totalElements": 2,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

### Delete Employee
**DELETE** `/api/v1/employees/10001`
```json
// Response 200 OK
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 200,
  "message": "Employee deleted successfully",
  "data": null
}
```

---

## SALARIES

### Update Salary
**PUT** `/api/v1/salaries/10001`
```json
// Request
{
  "salary": 95000
}

// Response 200 OK
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 200,
  "message": "Salary updated successfully",
  "data": {
    "empNo": 10001,
    "firstName": "John",
    "lastName": "Doe",
    "salary": 95000
  }
}
```

### Get Salary
**GET** `/api/v1/salaries/10001`
```json
// Response 200 OK
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 200,
  "message": "Salary fetched successfully",
  "data": {
    "empNo": 10001,
    "firstName": "John",
    "lastName": "Doe",
    "salary": 95000
  }
}
```

---

## MANAGERS

### Assign Manager
**POST** `/api/v1/managers/assign`
```json
// Request
{
  "deptNo": "D005",
  "empNo": 10001
}

// Response 201 Created
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 201,
  "message": "Manager assigned successfully",
  "data": {
    "deptNo": "D005",
    "deptName": "Development",
    "empNo": 10001,
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

### Remove Manager
**DELETE** `/api/v1/managers/remove`
```json
// Request Body
{
  "deptNo": "D005",
  "empNo": 10001
}

// Response 200 OK
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 200,
  "message": "Manager removed successfully",
  "data": null
}
```

### Get All Managers
**GET** `/api/v1/managers`
```json
// Response 200 OK
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 200,
  "message": "Managers fetched successfully",
  "data": [
    {
      "deptNo": "D005",
      "deptName": "Development",
      "empNo": 10001,
      "firstName": "John",
      "lastName": "Doe"
    }
  ]
}
```

---

## ERROR RESPONSES

### 404 Not Found
```json
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 404,
  "message": "Employee not found with empNo: '99999'",
  "data": null
}
```

### 409 Conflict (Duplicate)
```json
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 409,
  "message": "Employee already exists with empNo: '10001'",
  "data": null
}
```

### 400 Bad Request (Validation)
```json
{
  "timestamp": "2026-05-20T10:00:00",
  "status": 400,
  "message": "Validation failed",
  "data": {
    "firstName": "First name is required",
    "empNo": "Employee number must be positive",
    "titleId": "Title ID is required"
  }
}
```
