package com.hospital.model;

/**
 * Model class representing a Patient.
 * Demonstrates Encapsulation (private fields, getters/setters).
 */
public class Patient {
    private int patientId;
    private String fullName;
    private int age;
    private String gender;
    private String phone;
    private String address;

    // Default Constructor
    public Patient() {}

    // Constructor without ID (for new registrations before DB auto-increments ID)
    public Patient(String fullName, int age, String gender, String phone, String address) {
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
    }

    // Full Constructor (when retrieved from DB)
    public Patient(int patientId, String fullName, int age, String gender, String phone, String address) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("Patient [ID=%d, Name='%s', Age=%d, Gender='%s', Phone='%s', Address='%s']",
                patientId, fullName, age, gender, phone, address);
    }
}
