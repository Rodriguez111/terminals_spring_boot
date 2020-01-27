CREATE TABLE IF NOT EXISTS roles
(
    role_id INTEGER PRIMARY KEY AUTOINCREMENT,
    role    character varying(25) UNIQUE NOT NULL
);