MERGE INTO users AS target
USING (VALUES
    ('admin@example.com', 'Admin User', 'hashed_password_1', 'SUPER_ADMIN', 'EMPLOYEE'),
    ('backoffice@example.com', 'Back Office', 'hashed_password_2', 'BACK_OFFICE', 'EMPLOYEE'),
    ('branchmanager@example.com', 'Branch Manager', 'hashed_password_3', 'BRANCH_MANAGER', 'EMPLOYEE'),
    ('marketing@example.com', 'Marketing Team', 'hashed_password_4', 'MARKETING', 'EMPLOYEE'),
    ('customer@example.com', 'Customer', 'hashed_password_5', 'CUSTOMER', 'CUSTOMER')
) AS source (email, name, password, role_name, user_type)
ON target.email = source.email
WHEN NOT MATCHED THEN
    INSERT (id, email, name, password, role_id, user_type)
    VALUES (NEWID(), source.email, source.name, source.password,
           (SELECT id FROM roles WHERE name = source.role_name), source.user_type);
