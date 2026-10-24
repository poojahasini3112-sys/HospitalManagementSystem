package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentDetails;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for Appointment table operations.
 * Implemented by: Poojitha (Database / DBMS Lead)
 * Includes INNER JOIN queries linking Patient, Doctor and Appointment.
 */
public class AppointmentDAO {

    /**
     * Books a new appointment after foreign keys are checked by MySQL.
     */
    public int bookAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointment (patient_id, doctor_id, appointment_date, appointment_time, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, appointment.getPatientId());
            ps.setInt(2, appointment.getDoctorId());
            ps.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            ps.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            ps.setString(5, appointment.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    appointment.setAppointmentId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    /**
     * Demonstrates INNER JOIN query across 3 relational tables:
     * Appointment, Patient, and Doctor.
     */
    public List<AppointmentDetails> getAllAppointmentDetails() throws SQLException {
        List<AppointmentDetails> list = new ArrayList<>();
        String sql = "SELECT a.appointment_id, " +
                     "       p.patient_id, p.full_name AS patient_name, p.phone AS patient_phone, " +
                     "       d.doctor_id, d.full_name AS doctor_name, d.specialization AS doctor_specialization, " +
                     "       a.appointment_date, a.appointment_time, a.status " +
                     "FROM appointment a " +
                     "INNER JOIN patient p ON a.patient_id = p.patient_id " +
                     "INNER JOIN doctor d ON a.doctor_id = d.doctor_id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AppointmentDetails details = new AppointmentDetails(
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_name"),
                        rs.getString("patient_phone"),
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getString("doctor_specialization"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("status")
                );
                list.add(details);
            }
        }
        return list;
    }

    /**
     * Updates appointment status ('Scheduled', 'Completed', 'Cancelled').
     */
    public boolean updateStatus(int appointmentId, String newStatus) throws SQLException {
        String sql = "UPDATE appointment SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Demonstrates GROUP BY and COUNT aggregate function.
     * Returns a map of Doctor Name -> Total number of appointments.
     */
    public Map<String, Integer> getAppointmentCountPerDoctor() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT d.full_name, COUNT(a.appointment_id) AS total " +
                     "FROM doctor d " +
                     "LEFT JOIN appointment a ON d.doctor_id = a.doctor_id " +
                     "GROUP BY d.doctor_id, d.full_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("full_name"), rs.getInt("total"));
            }
        }
        return stats;
    }
}
