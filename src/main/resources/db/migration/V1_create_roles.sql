MERGE INTO roles AS target
USING (VALUES
    ('SUPER_ADMIN'),
    ('BACK_OFFICE'),
    ('BRANCH_MANAGER'),
    ('MARKETING'),
    ('CUSTOMER')
) AS source (name)
ON target.name = source.name
WHEN NOT MATCHED THEN
    INSERT (id, name) VALUES (NEWID(), source.name);