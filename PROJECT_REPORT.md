# Hospital Management System (HMS)
## Academic Project Report
**Course:** Object-Oriented Programming using Java (OOPS) + Database Management Systems (DBMS)  
**Authors / Team Members:**
- **Poojitha** — Database Architecture, MySQL Schema, Constraints, Queries & DAO Implementation
- **Moksha** — Frontend User Interface, Java Swing Screens, Forms & Event Handling

---

## 1. Abstract
The **Hospital Management System** is a desktop application developed to automate hospital administrative workflows including patient registration, doctor schedule management, appointment booking, and consultation billing. The system bridges core Object-Oriented Programming (OOPS) paradigms in Java with relational database principles in MySQL using Java Database Connectivity (JDBC). Data integrity is strictly maintained through schema-level constraints, while application modularity is achieved through a 3-tier architecture (UI $\rightarrow$ DAO $\rightarrow$ Database).

---

## 2. Objectives
- Eliminate error-prone manual paper registers for patient records and doctor appointments.
- Implement object-oriented encapsulation and separation of concerns using Data Access Objects (DAO).
- Enforce relational database integrity using Primary Keys, Foreign Keys (`ON DELETE CASCADE`), and `CHECK` constraints.
- Demonstrate multi-table `INNER JOIN` queries linking patients, doctors, and appointment slots.
- Prevent SQL injection vulnerabilities using parameterized `PreparedStatement` queries.

---

## 3. Technology Stack & Tools
| Technology | Role | Details |
| :--- | :--- | :--- |
| **Java (JDK 26 / 17+)** | Application Core | Object-Oriented Models, business rules, and validation logic. |
| **Java Swing** | Desktop Frontend | User-friendly interactive GUI with `JTabbedPane`, `JTable`, and `JOptionPane`. |
| **MySQL 8.0** | Relational Database | Relational storage for patients, doctors, appointments, and bills. |
| **JDBC** | Middleware Driver | Type-4 JDBC Driver (`mysql-connector-j-8.3.0.jar`) with `PreparedStatement`. |
| **Maven & Git** | Build & Versioning | Standard project directory structure and feature-branch collaboration. |

---

## 4. System Design & Architecture

### 4.1 3-Tier Layered Architecture
```
┌────────────────────────────────────────────────────────┐
│               PRESENTATION LAYER (UI)                  │
│       Java Swing: HospitalDashboard (Tabs & Tables)    │
└──────────────────────────┬─────────────────────────────┘
                           │ (Data Transfer Objects & Models)
┌──────────────────────────▼─────────────────────────────┐
│                 DATA ACCESS LAYER (DAO)                │
│    PatientDAO | DoctorDAO | AppointmentDAO | BillDAO   │
│         PreparedStatement SQL Execution & Mapping      │
└──────────────────────────┬─────────────────────────────┘
                           │ (JDBC Connection via DatabaseConnection.java)
┌──────────────────────────▼─────────────────────────────┐
│                PERSISTENCE LAYER (DBMS)                │
│               MySQL Database: hospital_db              │
│       Tables: patient, doctor, appointment, bill       │
└────────────────────────────────────────────────────────┘
```

### 4.2 Entity-Relationship (ER) Design
- **Patient (1) ———< (Many) Appointment**: One patient can book multiple appointments over time.
- **Doctor (1) ———< (Many) Appointment**: One doctor can consult multiple patients across scheduled slots.
- **Appointment (1) ——— (1) Bill**: Each completed consultation appointment has an associated consultation fee invoice.

### 4.3 Database Schema Design & Constraints
1. **`patient` Table**:
   - `patient_id`: INT AUTO_INCREMENT PRIMARY KEY
   - `full_name`: VARCHAR(100) NOT NULL
   - `age`: INT NOT NULL CHECK (age > 0 AND age < 150)
   - `gender`: VARCHAR(20) NOT NULL
   - `phone`: VARCHAR(20) NOT NULL
   - `address`: VARCHAR(255)
2. **`doctor` Table**:
   - `doctor_id`: INT AUTO_INCREMENT PRIMARY KEY
   - `full_name`: VARCHAR(100) NOT NULL
   - `specialization`: VARCHAR(100) NOT NULL
   - `phone`: VARCHAR(20) NOT NULL
   - `consultation_fee`: DECIMAL(10,2) NOT NULL CHECK (consultation_fee >= 0)
3. **`appointment` Table**:
   - `appointment_id`: INT AUTO_INCREMENT PRIMARY KEY
   - `patient_id`: INT NOT NULL (Foreign Key $\rightarrow$ `patient(patient_id)`)
   - `doctor_id`: INT NOT NULL (Foreign Key $\rightarrow$ `doctor(doctor_id)`)
   - `appointment_date`: DATE NOT NULL
   - `appointment_time`: TIME NOT NULL
   - `status`: VARCHAR(30) NOT NULL CHECK (status IN ('Scheduled', 'Completed', 'Cancelled'))
4. **`bill` Table**:
   - `bill_id`: INT AUTO_INCREMENT PRIMARY KEY
   - `appointment_id`: INT NOT NULL (Foreign Key $\rightarrow$ `appointment(appointment_id)`)
   - `amount`: DECIMAL(10,2) NOT NULL CHECK (amount >= 0)
   - `payment_status`: VARCHAR(30) NOT NULL CHECK (payment_status IN ('Paid', 'Pending', 'Cancelled'))
   - `bill_date`: DATE NOT NULL

---

## 5. OOPS Concepts Demonstrated
- **Classes and Objects**: Separate domain classes for entities (`Patient`, `Doctor`, `Appointment`, `Bill`, `AppointmentDetails`).
- **Encapsulation**: Private entity fields accessed only via public getter and setter methods.
- **Separation of Concerns**: UI code does not write raw SQL; it only communicates via DAO interfaces.
- **Exception Handling**: Graceful interception of `SQLException` and `NumberFormatException` using user-friendly dialog popups.

---

## 6. DBMS Concepts Demonstrated
- **DDL & Constraints**: Primary keys, Foreign keys with `ON DELETE CASCADE`, domain `CHECK` constraints, `NOT NULL`.
- **DML Operations**: Parameterized `INSERT`, `UPDATE`, `DELETE`, and `SELECT`.
- **Multi-Table JOIN**:
  ```sql
  SELECT a.appointment_id, p.full_name AS patient_name, d.full_name AS doctor_name, 
         d.specialization, a.appointment_date, a.appointment_time, a.status
  FROM appointment a
  INNER JOIN patient p ON a.patient_id = p.patient_id
  INNER JOIN doctor d ON a.doctor_id = d.doctor_id;
  ```
- **Aggregate Functions**:
  - `COUNT(a.appointment_id)` grouped by doctor to analyze workload.
  - `SUM(amount)` on paid bills to compute total revenue.

---

## 7. Testing & Verification Results
| Test Scenario | Action | Expected Result | Actual Result |
| :--- | :--- | :--- | :--- |
| **Patient Registration** | Submit valid details in form | Record saved with auto-increment ID | PASSED ✅ (Patient #5 created) |
| **Invalid Form Submission** | Submit blank name or non-number age | Validation alert displayed | PASSED ✅ |
| **Foreign Key Check** | Book appointment with invalid patient ID | MySQL Foreign Key constraint error | PASSED ✅ (Caught by MySQL engine) |
| **JOIN Query Display** | Open Appointments tab | Shows joined patient and doctor names | PASSED ✅ |
| **Aggregate Revenue** | Query paid bills | Computes exact total revenue | PASSED ✅ (₹900.00 computed) |

---

## 8. Conclusion & Future Scope
The application successfully fulfills all requirements for both OOPS and DBMS subjects. The layered architecture provides a maintainable foundation.
- **Future Enhancements**: Add role-based authentication (Admin vs Receptionist), export patient records to PDF, and support pharmacy inventory management.
