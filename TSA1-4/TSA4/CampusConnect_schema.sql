
CREATE DATABASE IF NOT EXISTS campusconnect_db;
USE campusconnect_db;

CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS service_requests (
    ticket_id VARCHAR(20) PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    category ENUM('ENROLLMENT', 'DOCUMENT', 'PAYMENT', 'ADVISING') NOT NULL,
    description VARCHAR(255) NOT NULL,
    priority INT NOT NULL CHECK (priority BETWEEN 1 AND 5),
    estimated_days INT NOT NULL CHECK (estimated_days >= 1),
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);