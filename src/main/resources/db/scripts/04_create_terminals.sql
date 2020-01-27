CREATE TABLE IF NOT EXISTS terminals
(
    terminal_id            INTEGER PRIMARY KEY AUTOINCREMENT,
    terminal_reg_id        character varying(10) UNIQUE                    NOT NULL,
    terminal_model         character varying(20)                           NOT NULL,
    terminal_serial_id     character varying(30) UNIQUE                    NOT NULL,
    terminal_inventory_id  character varying(20) UNIQUE                    NOT NULL,
    terminal_comment       character varying(500),
    terminal_is_active     BOOLEAN                                         NOT NULL,
    terminal_department_id INTEGER,
    user_id                INTEGER,
    terminal_create_date   DATETIME DEFAULT (DATETIME('now', 'localtime')) NOT NULL,
    terminal_update_date   DATETIME,
    FOREIGN KEY (terminal_department_id) REFERENCES departments (department_id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT
);