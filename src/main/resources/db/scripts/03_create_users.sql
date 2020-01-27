CREATE TABLE IF NOT EXISTS users
(
    user_id            INTEGER PRIMARY KEY AUTOINCREMENT,
    user_login         character varying(20) UNIQUE                    NOT NULL,
    user_password      character varying(20),
    user_name          character varying(25)                           NOT NULL,
    user_surname       character varying(25)                           NOT NULL,
    user_role_id       INTEGER                                         NOT NULL,
    user_department_id INTEGER,
    terminal_id        INTEGER,
    user_is_active     BOOLEAN                                         NOT NULL,
    user_create_date   DATETIME DEFAULT (DATETIME('now', 'localtime')) NOT NULL,
    user_update_date   DATETIME,
    FOREIGN KEY (user_role_id) REFERENCES roles (role_id) ON DELETE RESTRICT,
    FOREIGN KEY (user_department_id) REFERENCES departments (department_id) ON DELETE RESTRICT,
    FOREIGN KEY (terminal_id) REFERENCES terminals (terminal_id) ON DELETE RESTRICT
);