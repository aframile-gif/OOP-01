
CREATE DATABASE IF NOT EXISTS bookbridge_db;
USE bookbridge_db;

CREATE TABLE IF NOT EXISTS members (
    member_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    membership_type ENUM('STUDENT', 'FACULTY', 'GUEST') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS books (
    isbn VARCHAR(30) PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    status ENUM('AVAILABLE', 'CHECKED_OUT', 'RESERVED') NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS loans (
    loan_id VARCHAR(20) PRIMARY KEY,
    member_id VARCHAR(20) NOT NULL,
    isbn VARCHAR(30) NOT NULL,
    requested_days INT NOT NULL CHECK (requested_days BETWEEN 1 AND 30),
    late_days INT NOT NULL DEFAULT 0 CHECK (late_days >= 0),
    due_date DATE NOT NULL,
    status ENUM('APPROVED', 'RETURNED', 'OVERDUE') NOT NULL,
    estimated_fine DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(member_id),
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

CREATE TABLE IF NOT EXISTS reservations (
    reservation_id VARCHAR(20) PRIMARY KEY,
    member_id VARCHAR(20) NOT NULL,
    isbn VARCHAR(30) NOT NULL,
    status ENUM('PENDING', 'FULFILLED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(member_id),
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);