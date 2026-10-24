package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Doctor table operations.
 * Implemented by: Poojitha (Database / DBMS Lead)
 */
public class DoctorDAO {

    /**
     * Inserts a new doctor and returns generated ID.
     */
    public int addDoctor(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctor (full_name, specialization, phone, consultation_fee) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, doctor.getFullName());
            ps.setString(2, doctor.getSpecialization());
            ps.setString(3, doctor.getPhone());
            ps.setBigDecimal(4, doctor.getConsultationFee());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    doctor.setDoctorId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves all doctors.
     */
    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT doctor_id, full_name, specialization, phone, consultation_fee FROM doctor ORDER BY full_name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToDoctor(rs));
            }
        }
        return list;
    }

    /**
     * Finds a doctor by ID.
     */
    public Doctor getDoctorById(int doctorId) throws SQLException {
        String sql = "SELECT doctor_id, full_name, specialization, phone, consultation_fee FROM doctor WHERE doctor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDoctor(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves doctors filtering by specialization.
     */
    public List<Doctor> getDoctorsBySpecialization(String spec) throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT doctor_id, full_name, specialization, phone, consultation_fee FROM doctor WHERE specialization = ? ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, spec);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToDoctor(rs));
                }
            }
        }
        return list;
    }

    private Doctor mapRowToDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getInt("doctor_id"),
                rs.getString("full_name"),
                rs.getString("specialization"),
                rs.getString("phone"),
                rs.getBigDecimal("consultation_fee")
        );
    }
}
