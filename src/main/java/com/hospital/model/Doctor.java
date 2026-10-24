package com.hospital.model;

import java.math.BigDecimal;

/**
 * Model class representing a Doctor.
 */
public class Doctor {
    private int doctorId;
    private String fullName;
    private String specialization;
    private String phone;
    private BigDecimal consultationFee;

    public Doctor() {}

    public Doctor(String fullName, String specialization, String phone, BigDecimal consultationFee) {
        this.fullName = fullName;
        this.specialization = specialization;
        this.phone = phone;
        this.consultationFee = consultationFee;
    }

    public Doctor(int doctorId, String fullName, String specialization, String phone, BigDecimal consultationFee) {
        this.doctorId = doctorId;
        this.fullName = fullName;
        this.specialization = specialization;
        this.phone = phone;
        this.consultationFee = consultationFee;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    @Override
    public String toString() {
        return String.format("Doctor [ID=%d, Name='%s', Specialization='%s', Phone='%s', Fee=%.2f]",
                doctorId, fullName, specialization, phone, consultationFee);
    }
}
