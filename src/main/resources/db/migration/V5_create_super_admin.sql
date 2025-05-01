-- Insert SuperAdmin User
INSERT INTO users (name, email, password, role_id, user_type, is_first_login)
SELECT
    'Super Admin',
    'superadmin@example.com',
    '$2a$10$U/Pwl0uFkmPTnuhDqHWG8u8m7A7BZWqfjq7E8d1PvMNStP/NqSkC6', -- BCrypt dari "superadmin123"
    r.id,
    'PEGAWAI',
    false
FROM roles r
WHERE r.name = 'SUPER_ADMIN'
ON CONFLICT (email) DO NOTHING;

-- Insert Pegawai Details untuk SuperAdmin
INSERT INTO pegawai_details (nip, status_pegawai, user_id, branch_id)
SELECT
    '20242751',
    'ACTIVE',
    u.id,
    b.id
FROM users u
JOIN branches b ON b.name = 'Pusat'
WHERE u.email = 'superadmin@example.com'
ON CONFLICT (user_id) DO NOTHING;
