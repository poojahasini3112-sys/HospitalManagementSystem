# Hospital Management System (HMS)
**Academic Project for OOPS using Java + Database Management Systems (DBMS)**

## 👥 Team Responsibilities
- **Poojitha**: Database Architecture, MySQL Schema, SQL Queries, Constraints, and JDBC/DAO Layer.
- **Moksha**: Frontend UI, Java Swing screens, forms, tables, and validation.

---

## 🛠️ Technology Stack
- **Language**: Java 17+ (Object-Oriented Programming)
- **Database**: MySQL 8.0+
- **Connectivity**: JDBC with `com.mysql.cj.jdbc.Driver`
- **Build Tool**: Apache Maven
- **Version Control**: Git & GitHub

---

## 📁 Project Directory Structure
```
HospitalManagementSystem/
├── pom.xml                                      # Maven dependencies
├── README.md                                    # Setup & architecture guide
├── database/
│   └── schema.sql                               # Complete DB script with constraints & sample data
└── src/main/java/com/hospital/
    ├── Main.java                                # Database test & verification suite
    ├── model/                                   # Domain Entities (OOPS Encapsulation)
    │   ├── Patient.java
    │   ├── Doctor.java
    │   ├── Appointment.java
    │   ├── Bill.java
    │   └── AppointmentDetails.java              # DTO for INNER JOIN queries
    ├── dao/                                     # Data Access Objects (SQL queries & PreparedStatements)
    │   ├── PatientDAO.java
    │   ├── DoctorDAO.java
    │   ├── AppointmentDAO.java
    │   └── BillDAO.java
    └── util/
        └── DatabaseConnection.java              # JDBC Connection manager
```

---

## 🚀 Setup & Execution Guide

### 1. Database Setup in MySQL
1. Open MySQL Workbench or your MySQL command line client.
2. Run the script located at `database/schema.sql`:
   ```sql
   source path/to/HospitalManagementSystem/database/schema.sql;
   ```
   Or open `database/schema.sql` in MySQL Workbench and click the lightning bolt ⚡ to execute.
3. This creates:
   - Database: `hospital_db`
   - Tables: `patient`, `doctor`, `appointment`, `bill`
   - Constraints: Primary Keys, Foreign Keys (`ON DELETE CASCADE`), `CHECK` constraints (non-negative fees and valid ages).
   - Fictional sample data.

### 2. Configure Database Credentials
Open `src/main/java/com/hospital/util/DatabaseConnection.java` and verify your MySQL password:
```java
private static String password = "your_mysql_password"; // Default is 'root'
```

### 3. Build & Run Test Suite
Using Maven:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.hospital.Main"
```

---

## 🤝 Interface Contract for Moksha (UI Lead)
Moksha can call these DAO methods directly from Swing event handlers:

### Patient Operations (`PatientDAO`):
- `int addPatient(Patient p)`: Inserts patient and returns new generated `patient_id`.
- `List<Patient> getAllPatients()`: Returns all patients for table display.
- `Patient getPatientById(int id)`: Searches patient by ID.
- `List<Patient> searchPatientsByName(String name)`: Searches patients by name.
- `boolean updatePatient(Patient p)`: Updates an existing patient.

### Doctor Operations (`DoctorDAO`):
- `List<Doctor> getAllDoctors()`: Populates doctor dropdowns and lists.
- `Doctor getDoctorById(int id)`: Fetches doctor details.
- `List<Doctor> getDoctorsBySpecialization(String spec)`: Filters doctors by specialty.

### Appointment Operations (`AppointmentDAO`):
- `int bookAppointment(Appointment a)`: Books an appointment with foreign keys.
- `List<AppointmentDetails> getAllAppointmentDetails()`: Returns combined data (Patient Name, Doctor Name, Date, Time, Status) via `INNER JOIN` for JTables.
- `boolean updateStatus(int appointmentId, String newStatus)`: Updates status ('Scheduled', 'Completed', 'Cancelled').

### Billing Operations (`BillDAO`):
- `int createBill(Bill b)`: Generates bill for an appointment.
- `Bill getBillByAppointmentId(int appointmentId)`: Retrieves billing status.
- `boolean updatePaymentStatus(int billId, String status)`: Marks bill as 'Paid' or 'Pending'.
