CREATE TABLE IF NOT EXISTS departments
(
    department_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    department_name character varying(30) UNIQUE NOT NULL
)