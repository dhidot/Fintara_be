MERGE INTO plafonds AS target
USING (VALUES
    ('Bronze', 10000000.00, 0.0500, 6, 24),
    ('Silver', 50000000.00, 0.0450, 12, 36),
    ('Gold', 150000000.00, 0.0400, 12, 48),
    ('Platinum', 500000000.00, 0.0350, 24, 60)
) AS source (name, max_amount, interest_rate, min_tenor, max_tenor)
ON target.name = source.name
WHEN NOT MATCHED THEN
    INSERT (id, name, max_amount, interest_rate, min_tenor, max_tenor)
    VALUES (NEWID(), source.name, source.max_amount, source.interest_rate, source.min_tenor, source.max_tenor);
