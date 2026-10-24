package com.hospital.dao;

import com.hospital.model.Bill;
import com.hospital.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Bill table operations.
 * Implemented by: Poojitha (Database / DBMS Lead)
 */
public class BillDAO {

    /**
     * Generates a new bill for an appointment.
     */
    public int createBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bill (appointment_id, amount, payment_status, bill_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bill.getAppointmentId());
            ps.setBigDecimal(2, bill.getAmount());
            ps.setString(3, bill.getPaymentStatus());
            ps.setDate(4, Date.valueOf(bill.getBillDate()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    bill.setBillId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves a bill by its bill ID.
     */
    public Bill getBillById(int billId) throws SQLException {
        String sql = "SELECT bill_id, appointment_id, amount, payment_status, bill_date FROM bill WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBill(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the bill associated with an appointment.
     */
    public Bill getBillByAppointmentId(int appointmentId) throws SQLException {
        String sql = "SELECT bill_id, appointment_id, amount, payment_status, bill_date FROM bill WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBill(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates payment status ('Paid', 'Pending', 'Cancelled').
     */
    public boolean updatePaymentStatus(int billId, String status) throws SQLException {
        String sql = "UPDATE bill SET payment_status = ? WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, billId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Demonstrates SUM aggregate function for reports/viva.
     * Computes total revenue collected from all 'Paid' bills.
     */
    public BigDecimal getTotalCollectedRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_revenue FROM bill WHERE payment_status = 'Paid'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("total_revenue");
            }
        }
        return BigDecimal.ZERO;
    }

    private Bill mapRowToBill(ResultSet rs) throws SQLException {
        return new Bill(
                rs.getInt("bill_id"),
                rs.getInt("appointment_id"),
                rs.getBigDecimal("amount"),
                rs.getString("payment_status"),
                rs.getDate("bill_date").toLocalDate()
        );
    }
}
