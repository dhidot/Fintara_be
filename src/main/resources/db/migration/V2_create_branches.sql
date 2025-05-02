-- V2__init_branches.sql

-- Membuat tabel Branch
CREATE TABLE IF NOT EXISTS Branches (
    id INT PRIMARY KEY IDENTITY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE
);

-- Menambahkan data cabang
INSERT INTO Branches (name, address, latitude, longitude)
VALUES
    ('Pusat', 'Alamat Pusat', -6.2088, 106.8456),
    ('Jakarta Selatan', 'Alamat Jakarta Selatan', -6.2615, 106.8101),
    ('Surabaya', 'Alamat Surabaya', -7.2575, 112.7521),
    ('Bandung', 'Alamat Bandung', -6.9147, 107.6098),
    ('Medan', 'Alamat Medan', 3.5952, 98.6722);
