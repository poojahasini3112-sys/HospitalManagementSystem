package com.hospital;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.BillDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.Appointment;
import com.hospital.model.AppointmentDetails;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.util.DatabaseConnection;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Main application runner for Poojitha's Database & DAO verification.
 * Runs complete test suites for JDBC connection, SQL constraints, and DAO operations.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("   HOSPITAL MANAGEMENT SYSTEM - BACKEND & DATABASE    ");
        System.out.println("   OOPS (Java) + DBMS (MySQL) Integration Test Suite   ");
        System.out.println("======================================================\n");

        // 1. Verify Database Connection
        System.out.println("[Step 1] Verifying MySQL Connection...");
        if (!DatabaseConnection.testConnection()) {
            System.err.println("\n⚠️ Could not connect to MySQL server.");
            System.err.println("Please check:");
            System.err.println(" 1. MySQL service is running.");
            System.err.println(" 2. Database 'hospital_db' is created using database/schema.sql.");
            System.err.println(" 3. Check username/password in DatabaseConnection.java (currently: root / root).\n");
            return;
        }
        System.out.println("✅ Successfully connected to MySQL 'hospital_db'!\n");

        try {
            PatientDAO patientDAO = new PatientDAO();
            DoctorDAO doctorDAO = new DoctorDAO();
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            BillDAO billDAO = new BillDAO();

            // 2. Test Doctor Records
            System.out.println("[Step 2] Fetching Doctor Records...");
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            System.out.printf("Found %d doctors in the database:\n", doctors.size());
            for (Doctor d : doctors) {
                System.out.printf("  • Dr. %s (%s) - Fee: ₹%.2f\n", d.getFullName(), d.getSpecialization(), d.getConsultationFee());
            }
            System.out.println();

            // 3. Test Patient Records & Registration
            System.out.println("[Step 3] Testing Patient Registration & Listing...");
            Patient testPatient = new Patient("Test Patient " + System.currentTimeMillis() % 1000, 29, "Other", "9870000000", "Sample Address");
            int newPatientId = patientDAO.addPatient(testPatient);
            System.out.println("✅ Registered new patient with ID: " + newPatientId);

            List<Patient> patients = patientDAO.getAllPatients();
            System.out.printf("Total patients in database: %d\n\n", patients.size());

            // 4. Test INNER JOIN Query (Appointments)
            System.out.println("[Step 4] Testing INNER JOIN Query (Appointments + Patient + Doctor)...");
            List<AppointmentDetails> appointments = appointmentDAO.getAllAppointmentDetails();
            System.out.printf("Found %d appointments with joined patient & doctor details:\n", appointments.size());
            for (AppointmentDetails ad : appointments) {
                System.out.printf("  • Appt #%d: Patient '%s' with '%s' (%s) on %s at %s [%s]\n",
                        ad.getAppointmentId(), ad.getPatientName(), ad.getDoctorName(),
                        ad.getDoctorSpecialization(), ad.getAppointmentDate(), ad.getAppointmentTime(), ad.getStatus());
            }
            System.out.println();

            // 5. Test Aggregate Query (Appointments count per doctor)
            System.out.println("[Step 5] Testing Aggregate Query: Appointments Per Doctor...");
            Map<String, Integer> counts = appointmentDAO.getAppointmentCountPerDoctor();
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                System.out.printf("  • %s: %d appointment(s)\n", entry.getKey(), entry.getValue());
            }
            System.out.println();

            // 6. Test Revenue Aggregate Query
            System.out.println("[Step 6] Testing Aggregate Query: Total Revenue Collected...");
            System.out.println("  • Total Collected Revenue: ₹" + billDAO.getTotalCollectedRevenue() + "\n");

            // 7. Test Foreign Key Constraint Integrity (Expected Failure Demonstration)
            System.out.println("[Step 7] Testing Foreign Key Constraint Protection (Negative Test Case)...");
            Appointment invalidAppt = new Appointment(99999, 1, LocalDate.now(), LocalTime.of(10, 0), "Scheduled");
            try {
                appointmentDAO.bookAppointment(invalidAppt);
                System.err.println("❌ Error: Foreign key constraint did not trigger!");
            } catch (SQLException e) {
                System.out.println("✅ Foreign Key constraint successfully caught invalid patient ID (99999)!");
                System.out.println("   Database error prevented corrupted data: " + e.getMessage());
            }

            System.out.println("\n🎉 All database operations and tests completed successfully!");

        } catch (SQLException e) {
            System.err.println("SQL Exception occurred during test run: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
