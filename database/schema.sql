-- ===============================================================
-- Hospital Management System - Database Schema & Sample Data
-- Designed and Maintained by: Poojitha (Database / DBMS Lead)
-- Stack: MySQL + JDBC + Java (OOPS)
-- ===============================================================

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- ---------------------------------------------------------------
-- Drop existing tables in reverse dependency order
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS bill;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS doctor;
DROP TABLE IF EXISTS patient;

-- ---------------------------------------------------------------
-- 1. Patient Table
-- ---------------------------------------------------------------
CREATE TABLE patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    age INT NOT NULL CHECK (age > 0 AND age < 150),
    gender VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255)
);

-- ---------------------------------------------------------------
-- 2. Doctor Table
-- ---------------------------------------------------------------
CREATE TABLE doctor (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    consultation_fee DECIMAL(10, 2) NOT NULL CHECK (consultation_fee >= 0)
);

-- ---------------------------------------------------------------
-- 3. Appointment Table
-- ---------------------------------------------------------------
CREATE TABLE appointment (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Scheduled' CHECK (status IN ('Scheduled', 'Completed', 'Cancelled')),
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) 
        REFERENCES patient(patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) 
        REFERENCES doctor(doctor_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- 4. Bill Table
-- ---------------------------------------------------------------
CREATE TABLE bill (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    payment_status VARCHAR(30) NOT NULL DEFAULT 'Pending' CHECK (payment_status IN ('Paid', 'Pending', 'Cancelled')),
    bill_date DATE NOT NULL,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) 
        REFERENCES appointment(appointment_id) ON DELETE CASCADE
);

-- ===============================================================
-- Sample Fictional Data
-- ===============================================================

-- Insert Sample Patients
INSERT INTO patient (full_name, age, gender, phone, address) VALUES
('Ravi Kumar', 34, 'Male', '9876543210', '12 Lake View St, Hyderabad'),
('Ananya Sharma', 28, 'Female', '9812345678', '45 MG Road, Bengaluru'),
('Karthik Reddy', 52, 'Male', '9123456780', '88 Banjara Hills, Hyderabad'),
('Sneha Patel', 23, 'Female', '9988776655', '104 Anna Salai, Chennai');

-- Insert Sample Doctors
INSERT INTO doctor (full_name, specialization, phone, consultation_fee) VALUES
('Dr. Ramesh Rao', 'Cardiology', '9000011111', 800.00),
('Dr. Priya Menon', 'Dermatology', '9000022222', 500.00),
('Dr. Vikram Singh', 'General Medicine', '9000033333', 400.00),
('Dr. Sunita Deshmukh', 'Pediatrics', '9000044444', 600.00);

-- Insert Sample Appointments
INSERT INTO appointment (patient_id, doctor_id, appointment_date, appointment_time, status) VALUES
(1, 1, '2026-10-25', '10:00:00', 'Scheduled'),
(2, 2, '2026-10-25', '11:30:00', 'Completed'),
(3, 1, '2026-10-26', '14:00:00', 'Scheduled'),
(4, 3, '2026-10-26', '16:00:00', 'Completed');

-- Insert Sample Bills
INSERT INTO bill (appointment_id, amount, payment_status, bill_date) VALUES
(2, 500.00, 'Paid', '2026-10-25'),
(4, 400.00, 'Paid', '2026-10-26');

-- ===============================================================
-- Test Queries to Demonstrate for Evaluation / Viva
-- ===============================================================

-- 1. Basic SELECT Queries
-- SELECT * FROM patient;
-- SELECT * FROM doctor;
-- SELECT * FROM appointment;
-- SELECT * FROM bill;

-- 2. INNER JOIN: Appointments with Patient and Doctor names
-- SELECT 
--     a.appointment_id,
--     p.full_name AS patient_name,
--     p.phone AS patient_phone,
--     d.full_name AS doctor_name,
--     d.specialization,
--     a.appointment_date,
--     a.appointment_time,
--     a.status
-- FROM appointment a
-- INNER JOIN patient p ON a.patient_id = p.patient_id
-- INNER JOIN doctor d ON a.doctor_id = d.doctor_id
-- ORDER BY a.appointment_date, a.appointment_time;

-- 3. AGGREGATE QUERIES:
-- Appointments count per doctor:
-- SELECT d.full_name AS doctor_name, COUNT(a.appointment_id) AS total_appointments
-- FROM doctor d
-- LEFT JOIN appointment a ON d.doctor_id = a.doctor_id
-- GROUP BY d.doctor_id, d.full_name;

-- Total revenue collected:
-- SELECT SUM(amount) AS total_revenue_collected FROM bill WHERE payment_status = 'Paid';

-- 4. Constraint Validation Test Cases (Expected to FAIL in MySQL):
-- Test invalid foreign key:
-- INSERT INTO appointment (patient_id, doctor_id, appointment_date, appointment_time, status)
-- VALUES (999, 1, '2026-10-30', '10:00:00', 'Scheduled'); 
-- ERROR 1452: Cannot add or update a child row: a foreign key constraint fails

-- Test negative consultation fee:
-- INSERT INTO doctor (full_name, specialization, phone, consultation_fee)
-- VALUES ('Dr. Test', 'General', '9999999999', -100.00);
-- ERROR 3819: Check constraint 'doctor_chk_1' is violated.
