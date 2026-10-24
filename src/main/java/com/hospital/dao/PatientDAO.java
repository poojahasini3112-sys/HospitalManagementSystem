package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Patient table operations.
 * Implemented by: Poojitha (Database / DBMS Lead)
 * Uses PreparedStatements exclusively to prevent SQL Injection.
 */
public class PatientDAO {

    /**
     * Inserts a new patient and returns the generated patient_id.
     */
    public int addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patient (full_name, age, gender, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, patient.getFullName());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getPhone());
            ps.setString(5, patient.getAddress());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    patient.setPatientId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves all patient records ordered by patient_id.
     */
    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT patient_id, full_name, age, gender, phone, address FROM patient ORDER BY patient_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToPatient(rs));
            }
        }
        return list;
    }

    /**
     * Finds a single patient by their primary key.
     */
    public Patient getPatientById(int patientId) throws SQLException {
        String sql = "SELECT patient_id, full_name, age, gender, phone, address FROM patient WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPatient(rs);
                }
            }
        }
        return null;
    }

    /**
     * Searches patients by full_name substring.
     */
    public List<Patient> searchPatientsByName(String nameQuery) throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT patient_id, full_name, age, gender, phone, address FROM patient WHERE full_name LIKE ? ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nameQuery + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPatient(rs));
                }
            }
        }
        return list;
    }

    /**
     * Updates an existing patient record.
     */
    public boolean updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE patient SET full_name = ?, age = ?, gender = ?, phone = ?, address = ? WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, patient.getFullName());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getPhone());
            ps.setString(5, patient.getAddress());
            ps.setInt(6, patient.getPatientId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a patient by ID.
     */
    public boolean deletePatient(int patientId) throws SQLException {
        String sql = "DELETE FROM patient WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            return ps.executeUpdate() > 0;
        }
    }

    private Patient mapRowToPatient(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getInt("patient_id"),
                rs.getString("full_name"),
                rs.getInt("age"),
                rs.getString("gender"),
                rs.getString("phone"),
                rs.getString("address")
        );
    }
}
