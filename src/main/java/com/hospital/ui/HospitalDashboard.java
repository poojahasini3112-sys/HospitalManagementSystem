package com.hospital.ui;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.BillDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Hospital Management System - Main Desktop UI (Java Swing)
 * Designed for Moksha & Poojitha's project demonstration.
 * Integrates directly with PatientDAO, DoctorDAO, AppointmentDAO, and BillDAO.
 */
public class HospitalDashboard extends JFrame {

    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final BillDAO billDAO = new BillDAO();

    // Patient Tab Components
    private DefaultTableModel patientTableModel;
    private JTable patientTable;
    private JTextField txtPatientId, txtPatientName, txtPatientAge, txtPatientPhone, txtPatientAddress, txtPatientSearch;
    private JComboBox<String> cbPatientGender;

    // Doctor Tab Components
    private DefaultTableModel doctorTableModel;
    private JTable doctorTable;
    private JTextField txtDocName, txtDocSpec, txtDocPhone, txtDocFee;

    // Appointment Tab Components
    private DefaultTableModel appointmentTableModel;
    private JTable appointmentTable;
    private JComboBox<DoctorItem> cbApptDoctors;
    private JTextField txtApptPatientId, txtApptDate, txtApptTime;
    private JComboBox<String> cbApptStatus;

    // Billing Tab Components
    private DefaultTableModel billTableModel;
    private JTable billTable;
    private JTextField txtBillApptId, txtBillAmount, txtBillDate;
    private JComboBox<String> cbBillStatus;
    private JLabel lblTotalRevenue;

    public HospitalDashboard() {
        setTitle("MediTrack - Hospital Management System | Java Swing + MySQL");
        setSize(1050, 680);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadAllData();
    }

    private void initUI() {
        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("👥 Patients", createPatientsPanel());
        tabbedPane.addTab("🩺 Doctors", createDoctorsPanel());
        tabbedPane.addTab("📅 Appointments", createAppointmentsPanel());
        tabbedPane.addTab("💳 Billing & Revenue", createBillingPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar at bottom
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        JLabel lblStatus = new JLabel(" Connected to MySQL (hospital_db) | Team: Moksha (UI) & Poojitha (DBMS)");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.add(lblStatus);
        add(statusBar, BorderLayout.SOUTH);
    }

    // =========================================================================
    // TAB 1: PATIENTS PANEL
    // =========================================================================
    private JPanel createPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtPatientId = new JTextField(15);
        txtPatientId.setEditable(false);
        txtPatientName = new JTextField(15);
        txtPatientAge = new JTextField(15);
        cbPatientGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        txtPatientPhone = new JTextField(15);
        txtPatientAddress = new JTextField(15);

        addFormField(formPanel, "Patient ID (Auto):", txtPatientId, gbc, 0);
        addFormField(formPanel, "Full Name *:", txtPatientName, gbc, 1);
        addFormField(formPanel, "Age *:", txtPatientAge, gbc, 2);
        addFormField(formPanel, "Gender *:", cbPatientGender, gbc, 3);
        addFormField(formPanel, "Phone *:", txtPatientPhone, gbc, 4);
        addFormField(formPanel, "Address:", txtPatientAddress, gbc, 5);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton btnAdd = new JButton("Register");
        JButton btnUpdate = new JButton("Update");
        JButton btnClear = new JButton("Clear");

        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnUpdate.setBackground(new Color(0, 123, 255));
        btnUpdate.setForeground(Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // Right Table Panel
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtPatientSearch = new JTextField(18);
        JButton btnSearch = new JButton("Search by Name");
        JButton btnRefresh = new JButton("Refresh All");

        searchBar.add(new JLabel("Search:"));
        searchBar.add(txtPatientSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnRefresh);
        rightPanel.add(searchBar, BorderLayout.NORTH);

        patientTableModel = new DefaultTableModel(new String[]{"ID", "Full Name", "Age", "Gender", "Phone", "Address"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        patientTable = new JTable(patientTableModel);
        patientTable.setRowHeight(24);
        rightPanel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        // Table Selection Listener (fill form when clicked)
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtPatientId.setText(patientTableModel.getValueAt(selectedRow, 0).toString());
                txtPatientName.setText(patientTableModel.getValueAt(selectedRow, 1).toString());
                txtPatientAge.setText(patientTableModel.getValueAt(selectedRow, 2).toString());
                cbPatientGender.setSelectedItem(patientTableModel.getValueAt(selectedRow, 3).toString());
                txtPatientPhone.setText(patientTableModel.getValueAt(selectedRow, 4).toString());
                Object addr = patientTableModel.getValueAt(selectedRow, 5);
                txtPatientAddress.setText(addr != null ? addr.toString() : "");
            }
        });

        // Action Handlers
        btnAdd.addActionListener(e -> handleAddPatient());
        btnUpdate.addActionListener(e -> handleUpdatePatient());
        btnClear.addActionListener(e -> clearPatientForm());
        btnSearch.addActionListener(e -> handleSearchPatient());
        btnRefresh.addActionListener(e -> loadPatients());

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // TAB 2: DOCTORS PANEL
    // =========================================================================
    private JPanel createDoctorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Doctor Registration"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtDocName = new JTextField(15);
        txtDocSpec = new JTextField(15);
        txtDocPhone = new JTextField(15);
        txtDocFee = new JTextField(15);

        addFormField(formPanel, "Full Name *:", txtDocName, gbc, 0);
        addFormField(formPanel, "Specialization *:", txtDocSpec, gbc, 1);
        addFormField(formPanel, "Phone *:", txtDocPhone, gbc, 2);
        addFormField(formPanel, "Consultation Fee (₹) *:", txtDocFee, gbc, 3);

        JButton btnAddDoc = new JButton("Add Doctor");
        btnAddDoc.setBackground(new Color(40, 167, 69));
        btnAddDoc.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(btnAddDoc, gbc);

        // Doctor Table
        doctorTableModel = new DefaultTableModel(new String[]{"ID", "Doctor Name", "Specialization", "Phone", "Fee (₹)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        doctorTable = new JTable(doctorTableModel);
        doctorTable.setRowHeight(24);

        btnAddDoc.addActionListener(e -> handleAddDoctor());

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // TAB 3: APPOINTMENTS PANEL (Demonstrates INNER JOIN)
    // =========================================================================
    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book New Appointment"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtApptPatientId = new JTextField(15);
        cbApptDoctors = new JComboBox<>();
        txtApptDate = new JTextField(LocalDate.now().toString(), 15);
        txtApptTime = new JTextField("10:00:00", 15);
        cbApptStatus = new JComboBox<>(new String[]{"Scheduled", "Completed", "Cancelled"});

        addFormField(formPanel, "Patient ID *:", txtApptPatientId, gbc, 0);
        addFormField(formPanel, "Select Doctor *:", cbApptDoctors, gbc, 1);
        addFormField(formPanel, "Date (YYYY-MM-DD) *:", txtApptDate, gbc, 2);
        addFormField(formPanel, "Time (HH:MM:SS) *:", txtApptTime, gbc, 3);
        addFormField(formPanel, "Status:", cbApptStatus, gbc, 4);

        JButton btnBook = new JButton("Book Appointment");
        btnBook.setBackground(new Color(0, 123, 255));
        btnBook.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(btnBook, gbc);

        // Appointments Table (Displays JOIN results)
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnComplete = new JButton("Mark Completed");
        JButton btnCancel = new JButton("Cancel Appointment");
        JButton btnRefreshAppts = new JButton("Refresh List");

        toolbar.add(btnComplete);
        toolbar.add(btnCancel);
        toolbar.add(btnRefreshAppts);
        rightPanel.add(toolbar, BorderLayout.NORTH);

        appointmentTableModel = new DefaultTableModel(new String[]{"Appt ID", "Patient Name", "Doctor Name", "Specialization", "Date", "Time", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        appointmentTable = new JTable(appointmentTableModel);
        appointmentTable.setRowHeight(24);
        rightPanel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        btnBook.addActionListener(e -> handleBookAppointment());
        btnComplete.addActionListener(e -> handleUpdateApptStatus("Completed"));
        btnCancel.addActionListener(e -> handleUpdateApptStatus("Cancelled"));
        btnRefreshAppts.addActionListener(e -> loadAppointments());

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // TAB 4: BILLING PANEL (Demonstrates Aggregates & Calculations)
    // =========================================================================
    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Generate Consultation Bill"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtBillApptId = new JTextField(15);
        txtBillAmount = new JTextField(15);
        cbBillStatus = new JComboBox<>(new String[]{"Paid", "Pending", "Cancelled"});
        txtBillDate = new JTextField(LocalDate.now().toString(), 15);

        addFormField(formPanel, "Appointment ID *:", txtBillApptId, gbc, 0);
        addFormField(formPanel, "Amount (₹) *:", txtBillAmount, gbc, 1);
        addFormField(formPanel, "Payment Status *:", cbBillStatus, gbc, 2);
        addFormField(formPanel, "Bill Date (YYYY-MM-DD):", txtBillDate, gbc, 3);

        JButton btnGenerateBill = new JButton("Generate Bill");
        btnGenerateBill.setBackground(new Color(40, 167, 69));
        btnGenerateBill.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(btnGenerateBill, gbc);

        // Right side: Table and KPI summary
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        JPanel kpiPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotalRevenue = new JLabel("Total Revenue Collected: ₹0.00");
        lblTotalRevenue.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalRevenue.setForeground(new Color(0, 128, 0));
        kpiPanel.add(lblTotalRevenue);
        rightPanel.add(kpiPanel, BorderLayout.NORTH);

        billTableModel = new DefaultTableModel(new String[]{"Bill ID", "Appt ID", "Amount (₹)", "Status", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        billTable = new JTable(billTableModel);
        billTable.setRowHeight(24);
        rightPanel.add(new JScrollPane(billTable), BorderLayout.CENTER);

        btnGenerateBill.addActionListener(e -> handleGenerateBill());

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // EVENT HANDLERS
    // =========================================================================
    private void handleAddPatient() {
        try {
            String name = txtPatientName.getText().trim();
            String ageStr = txtPatientAge.getText().trim();
            String gender = cbPatientGender.getSelectedItem().toString();
            String phone = txtPatientPhone.getText().trim();
            String address = txtPatientAddress.getText().trim();

            if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields (Name, Age, Phone).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int age = Integer.parseInt(ageStr);
            Patient p = new Patient(name, age, gender, phone, address);
            int newId = patientDAO.addPatient(p);

            JOptionPane.showMessageDialog(this, "Patient registered successfully with ID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
            clearPatientForm();
            loadPatients();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdatePatient() {
        try {
            String idStr = txtPatientId.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a patient from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = Integer.parseInt(idStr);
            String name = txtPatientName.getText().trim();
            int age = Integer.parseInt(txtPatientAge.getText().trim());
            String gender = cbPatientGender.getSelectedItem().toString();
            String phone = txtPatientPhone.getText().trim();
            String address = txtPatientAddress.getText().trim();

            Patient p = new Patient(id, name, age, gender, phone, address);
            boolean success = patientDAO.updatePatient(p);
            if (success) {
                JOptionPane.showMessageDialog(this, "Patient updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPatients();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating patient: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearchPatient() {
        String query = txtPatientSearch.getText().trim();
        try {
            List<Patient> list = patientDAO.searchPatientsByName(query);
            patientTableModel.setRowCount(0);
            for (Patient p : list) {
                patientTableModel.addRow(new Object[]{p.getPatientId(), p.getFullName(), p.getAge(), p.getGender(), p.getPhone(), p.getAddress()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddDoctor() {
        try {
            String name = txtDocName.getText().trim();
            String spec = txtDocSpec.getText().trim();
            String phone = txtDocPhone.getText().trim();
            BigDecimal fee = new BigDecimal(txtDocFee.getText().trim());

            if (name.isEmpty() || spec.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all doctor details.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Doctor d = new Doctor(name, spec, phone, fee);
            int newId = doctorDAO.addDoctor(d);
            JOptionPane.showMessageDialog(this, "Doctor added successfully with ID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);

            txtDocName.setText("");
            txtDocSpec.setText("");
            txtDocPhone.setText("");
            txtDocFee.setText("");
            loadDoctors();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding doctor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBookAppointment() {
        try {
            String patientIdStr = txtApptPatientId.getText().trim();
            if (patientIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Patient ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int patientId = Integer.parseInt(patientIdStr);
            DoctorItem doc = (DoctorItem) cbApptDoctors.getSelectedItem();
            if (doc == null) {
                JOptionPane.showMessageDialog(this, "Please select a doctor.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate date = LocalDate.parse(txtApptDate.getText().trim());
            LocalTime time = LocalTime.parse(txtApptTime.getText().trim());
            String status = cbApptStatus.getSelectedItem().toString();

            Appointment appt = new Appointment(patientId, doc.id, date, time, status);
            int apptId = appointmentDAO.bookAppointment(appt);

            JOptionPane.showMessageDialog(this, "Appointment #" + apptId + " booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAppointments();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Foreign Key Check Failed! Patient ID or Doctor ID does not exist in MySQL.\n" + ex.getMessage(), "Database Constraint Protected", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format (Use YYYY-MM-DD and HH:MM:SS): " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateApptStatus(String status) {
        int row = appointmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an appointment from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int apptId = (int) appointmentTableModel.getValueAt(row, 0);
        try {
            appointmentDAO.updateStatus(apptId, status);
            loadAppointments();
            JOptionPane.showMessageDialog(this, "Appointment #" + apptId + " updated to " + status, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleGenerateBill() {
        try {
            int apptId = Integer.parseInt(txtBillApptId.getText().trim());
            BigDecimal amount = new BigDecimal(txtBillAmount.getText().trim());
            String status = cbBillStatus.getSelectedItem().toString();
            LocalDate date = LocalDate.parse(txtBillDate.getText().trim());

            Bill bill = new Bill(apptId, amount, status, date);
            int billId = billDAO.createBill(bill);

            JOptionPane.showMessageDialog(this, "Bill #" + billId + " generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBillingData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: Ensure Appointment ID exists and hasn't already been billed.\n" + ex.getMessage(), "Constraint Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearPatientForm() {
        txtPatientId.setText("");
        txtPatientName.setText("");
        txtPatientAge.setText("");
        txtPatientPhone.setText("");
        txtPatientAddress.setText("");
        patientTable.clearSelection();
    }

    // =========================================================================
    // DATA LOADERS
    // =========================================================================
    public void loadAllData() {
        loadPatients();
        loadDoctors();
        loadAppointments();
        loadBillingData();
    }

    private void loadPatients() {
        try {
            List<Patient> list = patientDAO.getAllPatients();
            patientTableModel.setRowCount(0);
            for (Patient p : list) {
                patientTableModel.addRow(new Object[]{p.getPatientId(), p.getFullName(), p.getAge(), p.getGender(), p.getPhone(), p.getAddress()});
            }
        } catch (SQLException e) {
            System.err.println("Error loading patients: " + e.getMessage());
        }
    }

    private void loadDoctors() {
        try {
            List<Doctor> list = doctorDAO.getAllDoctors();
            doctorTableModel.setRowCount(0);
            cbApptDoctors.removeAllItems();
            for (Doctor d : list) {
                doctorTableModel.addRow(new Object[]{d.getDoctorId(), d.getFullName(), d.getSpecialization(), d.getPhone(), d.getConsultationFee()});
                cbApptDoctors.addItem(new DoctorItem(d.getDoctorId(), d.getFullName(), d.getSpecialization()));
            }
        } catch (SQLException e) {
            System.err.println("Error loading doctors: " + e.getMessage());
        }
    }

    private void loadAppointments() {
        try {
            List<AppointmentDetails> list = appointmentDAO.getAllAppointmentDetails();
            appointmentTableModel.setRowCount(0);
            for (AppointmentDetails ad : list) {
                appointmentTableModel.addRow(new Object[]{
                        ad.getAppointmentId(),
                        ad.getPatientName(),
                        ad.getDoctorName(),
                        ad.getDoctorSpecialization(),
                        ad.getAppointmentDate(),
                        ad.getAppointmentTime(),
                        ad.getStatus()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
        }
    }

    private void loadBillingData() {
        try {
            BigDecimal totalRev = billDAO.getTotalCollectedRevenue();
            lblTotalRevenue.setText(String.format("Total Revenue Collected: ₹%.2f", totalRev));
        } catch (SQLException e) {
            System.err.println("Error loading revenue: " + e.getMessage());
        }
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    // Helper item for Doctor combo box
    private static class DoctorItem {
        int id;
        String name;
        String spec;

        DoctorItem(int id, String name, String spec) {
            this.id = id;
            this.name = name;
            this.spec = spec;
        }

        @Override
        public String toString() {
            return String.format("Dr. %s (%s)", name, spec);
        }
    }

    public static void main(String[] args) {
        // Set System Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            HospitalDashboard dashboard = new HospitalDashboard();
            dashboard.setVisible(true);
        });
    }
}
