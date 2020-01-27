INSERT INTO users
(user_login, user_password, user_name, user_surname, user_role_id,
 user_is_active, user_create_date)
SELECT 'root', 'root', 'rootName', 'rootSurname',
       (SELECT role_id FROM roles WHERE role = 'root'),
       true, datetime(CURRENT_TIMESTAMP, 'localtime')
WHERE NOT EXISTS (SELECT user_login FROM users WHERE user_login = 'root');