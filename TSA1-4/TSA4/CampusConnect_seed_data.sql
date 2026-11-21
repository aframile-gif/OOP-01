
USE campusconnect_db;

INSERT IGNORE INTO students (student_id, name) VALUES
('S01', 'Aliyah Ramile'),
('S02', 'Mark Cruz');

INSERT IGNORE INTO service_requests (ticket_id, student_id, category, description, priority, estimated_days, status) VALUES
('TKT-101', 'S01', 'ENROLLMENT', 'Add OOP Subject', 3, 3, 'OPEN'),
('TKT-102', 'S02', 'ADVISING', 'Academic Load Review', 5, 3, 'OPEN'),
('TKT-103', 'S01', 'PAYMENT', 'Tuition Receipt Issue', 5, 1, 'OPEN');