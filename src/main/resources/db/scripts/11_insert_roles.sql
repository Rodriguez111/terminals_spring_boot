INSERT INTO roles
(role)
SELECT 'root'
WHERE NOT EXISTS (SELECT role FROM roles WHERE role = 'root')
UNION ALL SELECT 'administrator'
WHERE NOT EXISTS (SELECT role FROM roles WHERE role = 'administrator')
UNION ALL SELECT 'user'
WHERE NOT EXISTS (SELECT role FROM roles WHERE role = 'user');