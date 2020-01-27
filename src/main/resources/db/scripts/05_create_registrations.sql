CREATE TABLE IF NOT EXISTS registrations
(
    record_id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id            INTEGER                                         NOT NULL,
    terminal_id        INTEGER                                         NOT NULL,
    admin_gave_id      INTEGER                                         NOT NULL,
    admin_received_id  INTEGER,
    record_start_date  DATETIME DEFAULT (DATETIME('now', 'localtime')) NOT NULL,
    record_finish_date DATETIME,
    FOREIGN KEY (admin_gave_id) REFERENCES users (user_id) ON DELETE RESTRICT,
    FOREIGN KEY (admin_received_id) REFERENCES users (user_id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT,
    FOREIGN KEY (terminal_id) REFERENCES terminals (terminal_id) ON DELETE RESTRICT
);