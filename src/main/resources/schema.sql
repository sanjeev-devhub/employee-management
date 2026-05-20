-- ============================================================
-- Employee Management System - Database Schema
-- ============================================================

CREATE DATABASE employee_db;
\c employee_db;

-- ============================================================
-- TABLE: titles
-- ============================================================
CREATE TABLE IF NOT EXISTS titles (
    title_id    VARCHAR(10)  PRIMARY KEY,
    title       VARCHAR(100) UNIQUE NOT NULL
);

-- ============================================================
-- TABLE: departments
-- ============================================================
CREATE TABLE IF NOT EXISTS departments (
    dept_no     VARCHAR(10)  PRIMARY KEY,
    dept_name   VARCHAR(100) UNIQUE NOT NULL
);

-- ============================================================
-- TABLE: employees
-- ============================================================
CREATE TABLE IF NOT EXISTS employees (
    emp_no       INT          PRIMARY KEY,
    emp_title_id VARCHAR(10)  NOT NULL,
    birth_date   DATE         NOT NULL,
    first_name   VARCHAR(50)  NOT NULL,
    last_name    VARCHAR(50)  NOT NULL,
    sex          CHAR(1)      CHECK (sex IN ('M', 'F')),
    hire_date    DATE         NOT NULL,
    CONSTRAINT fk_emp_title FOREIGN KEY (emp_title_id) REFERENCES titles(title_id)
);

-- ============================================================
-- TABLE: salaries
-- ============================================================
CREATE TABLE IF NOT EXISTS salaries (
    emp_no  INT PRIMARY KEY,
    salary  INT NOT NULL CHECK (salary > 0),
    CONSTRAINT fk_salary_emp FOREIGN KEY (emp_no) REFERENCES employees(emp_no) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: dept_emp (Employee-Department Join Table)
-- ============================================================
CREATE TABLE IF NOT EXISTS dept_emp (
    emp_no  INT         NOT NULL,
    dept_no VARCHAR(10) NOT NULL,
    PRIMARY KEY (emp_no, dept_no),
    CONSTRAINT fk_dept_emp_emp  FOREIGN KEY (emp_no)  REFERENCES employees(emp_no)   ON DELETE CASCADE,
    CONSTRAINT fk_dept_emp_dept FOREIGN KEY (dept_no) REFERENCES departments(dept_no) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: dept_manager (Department-Manager Join Table)
-- ============================================================
CREATE TABLE IF NOT EXISTS dept_manager (
    dept_no VARCHAR(10) NOT NULL,
    emp_no  INT         NOT NULL,
    PRIMARY KEY (dept_no, emp_no),
    CONSTRAINT fk_dept_mgr_dept FOREIGN KEY (dept_no) REFERENCES departments(dept_no) ON DELETE CASCADE,
    CONSTRAINT fk_dept_mgr_emp  FOREIGN KEY (emp_no)  REFERENCES employees(emp_no)    ON DELETE CASCADE
);

-- ============================================================
-- Indexes for performance
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_emp_first_name  ON employees(first_name);
CREATE INDEX IF NOT EXISTS idx_emp_last_name   ON employees(last_name);
CREATE INDEX IF NOT EXISTS idx_emp_hire_date   ON employees(hire_date);
CREATE INDEX IF NOT EXISTS idx_dept_emp_dept   ON dept_emp(dept_no);
CREATE INDEX IF NOT EXISTS idx_dept_mgr_dept   ON dept_manager(dept_no);

-- ============================================================
-- Sample Data
-- ============================================================

INSERT INTO titles (title_id, title) VALUES
    ('T001', 'Staff'),
    ('T002', 'Senior Staff'),
    ('T003', 'Engineer'),
    ('T004', 'Senior Engineer'),
    ('T005', 'Assistant Engineer'),
    ('T006', 'Technique Leader'),
    ('T007', 'Manager')
ON CONFLICT DO NOTHING;

INSERT INTO departments (dept_no, dept_name) VALUES
    ('D001', 'Marketing'),
    ('D002', 'Finance'),
    ('D003', 'Human Resources'),
    ('D004', 'Production'),
    ('D005', 'Development'),
    ('D006', 'Quality Management'),
    ('D007', 'Sales'),
    ('D008', 'Research'),
    ('D009', 'Customer Service')
ON CONFLICT DO NOTHING;

INSERT INTO employees (emp_no, emp_title_id, birth_date, first_name, last_name, sex, hire_date) VALUES
    (10001, 'T004', '1953-09-02', 'Georgi',    'Facello',   'M', '1986-06-26'),
    (10002, 'T002', '1964-06-02', 'Bezalel',   'Simmel',    'F', '1985-11-21'),
    (10003, 'T003', '1959-12-03', 'Parto',     'Bamford',   'M', '1986-08-28'),
    (10004, 'T003', '1954-05-01', 'Chirstian', 'Koblick',   'M', '1986-12-01'),
    (10005, 'T002', '1955-01-21', 'Kyoichi',   'Maliniak',  'M', '1989-09-12'),
    (10006, 'T004', '1953-04-20', 'Anneke',    'Preusig',   'F', '1989-06-02'),
    (10007, 'T007', '1957-05-23', 'Tzvetan',   'Zielinski', 'F', '1989-02-10'),
    (10008, 'T005', '1958-02-19', 'Saniya',    'Kalloufi',  'M', '1994-09-15'),
    (10009, 'T006', '1952-04-19', 'Sumant',    'Peac',      'F', '1985-02-18'),
    (10010, 'T004', '1963-06-01', 'Duangkaew', 'Piveteau',  'F', '1989-08-24')
ON CONFLICT DO NOTHING;

INSERT INTO salaries (emp_no, salary) VALUES
    (10001, 60117),
    (10002, 65828),
    (10003, 40006),
    (10004, 40054),
    (10005, 78228),
    (10006, 40000),
    (10007, 56724),
    (10008, 46671),
    (10009, 60929),
    (10010, 72488)
ON CONFLICT DO NOTHING;

INSERT INTO dept_emp (emp_no, dept_no) VALUES
    (10001, 'D005'),
    (10002, 'D007'),
    (10003, 'D004'),
    (10004, 'D004'),
    (10005, 'D003'),
    (10006, 'D005'),
    (10007, 'D008'),
    (10008, 'D005'),
    (10009, 'D006'),
    (10010, 'D006')
ON CONFLICT DO NOTHING;

INSERT INTO dept_manager (dept_no, emp_no) VALUES
    ('D005', 10007),
    ('D007', 10002)
ON CONFLICT DO NOTHING;
