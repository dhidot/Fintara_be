-- V1__init_roles.sql

-- Membuat tabel Role
CREATE TABLE IF NOT EXISTS Roles (
    id INT PRIMARY KEY IDENTITY,
    name VARCHAR(255) NOT NULL
);

-- Menambahkan data roles
INSERT INTO Roles (name)
VALUES
    ('SUPER_ADMIN'),
    ('BACK_OFFICE'),
    ('BRANCH_MANAGER'),
    ('MARKETING'),
    ('CUSTOMER');
