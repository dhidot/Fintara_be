MERGE INTO features AS target
USING (VALUES
    ('BRANCHES_ACCESS'),
    ('CUSTOMER_ACCESS'),
    ('CUSTOMER_PROFILE'),
    ('FEATURES_ACCESS'),
    ('EMPLOYEE_ACCESS'),
    ('PEGAWAI_PROFILE'),
    ('ROLE_ACCESS'),
    ('ROLE_FEATURE_ACCESS'),
    ('USER_ACCESS')
) AS source (name)
ON target.name = source.name
WHEN NOT MATCHED THEN
    INSERT (id, name) VALUES (NEWID(), source.name);
