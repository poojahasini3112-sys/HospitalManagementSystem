package com.hospital.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class representing a Consultation Bill.
 */
public class Bill {
    private int billId;
    private int appointmentId;
    private BigDecimal amount;
    private String paymentStatus; // 'Paid', 'Pending', 'Cancelled'
    private LocalDate billDate;

    public Bill() {}

    public Bill(int appointmentId, BigDecimal amount, String paymentStatus, LocalDate billDate) {
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.billDate = billDate;
    }

    public Bill(int billId, int appointmentId, BigDecimal amount, String paymentStatus, LocalDate billDate) {
        this.billId = billId;
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.billDate = billDate;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    @Override
    public String toString() {
        return String.format("Bill [ID=%d, AppointmentID=%d, Amount=%.2f, Status='%s', Date=%s]",
                billId, appointmentId, amount, paymentStatus, billDate);
    }
}
