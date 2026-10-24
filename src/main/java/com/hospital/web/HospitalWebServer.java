package com.hospital.web;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.BillDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.*;
import com.hospital.util.DatabaseConnection;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Hospital Web Application Server
 * Built with: Pure Java (Backend) + MySQL (Database) + JDBC (Connectivity)
 * Serves the MediTrack Web Portal and REST APIs connected live to MySQL hospital_db.
 */
public class HospitalWebServer {

    private static final int PORT = 8080;
    private static final PatientDAO patientDAO = new PatientDAO();
    private static final DoctorDAO doctorDAO = new DoctorDAO();
    private static final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private static final BillDAO billDAO = new BillDAO();

    public static void main(String[] args) throws IOException {
        System.out.println("==========================================================");
        System.out.println("   MEDITRACK WEB SERVER (Java + JDBC + MySQL)             ");
        System.out.println("==========================================================");

        // 1. Verify MySQL Connection via JDBC
        if (!DatabaseConnection.testConnection()) {
            System.err.println("❌ Could not connect to MySQL database via JDBC.");
            System.err.println("Please ensure MySQL is running and password is correct in DatabaseConnection.java.");
            return;
        }
        System.out.println("✅ JDBC Connected to MySQL 'hospital_db' successfully!");

        // 2. Start Built-in Java HTTP Web Server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Webpage Static Handler
        server.createContext("/", new StaticFileHandler());

        // REST API Endpoints (Connected via JDBC to MySQL)
        server.createContext("/api/doctors", new DoctorsApiHandler());
        server.createContext("/api/patients", new PatientsApiHandler());
        server.createContext("/api/appointments", new AppointmentsApiHandler());
        server.createContext("/api/billing", new BillingApiHandler());

        server.setExecutor(null); // default executor
        server.start();

        System.out.println("🚀 MediTrack Web Application is LIVE!");
        System.out.println("👉 Open your browser at: http://localhost:" + PORT);
        System.out.println("==========================================================");
    }

    // =========================================================================
    // 1. STATIC FILE HANDLER (Serves web/index.html)
    // =========================================================================
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            File file = new File("web" + path);
            if (!file.exists()) {
                file = new File("index.html"); // Fallback to root index.html
            }

            if (file.exists()) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                String contentType = "text/html; charset=UTF-8";
                if (path.endsWith(".css")) contentType = "text/css";
                else if (path.endsWith(".js")) contentType = "application/javascript";
                else if (path.endsWith(".png")) contentType = "image/png";

                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } else {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    // =========================================================================
    // 2. DOCTORS API (JDBC SELECT)
    // =========================================================================
    static class DoctorsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    List<Doctor> doctors = doctorDAO.getAllDoctors();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < doctors.size(); i++) {
                        Doctor d = doctors.get(i);
                        json.append(String.format("{\"id\":%d,\"name\":\"%s\",\"spec\":\"%s\",\"phone\":\"%s\",\"fee\":%.2f}",
                                d.getDoctorId(), escape(d.getFullName()), escape(d.getSpecialization()), escape(d.getPhone()), d.getConsultationFee()));
                        if (i < doctors.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendJsonResponse(exchange, 200, json.toString());
                } catch (SQLException e) {
                    sendJsonResponse(exchange, 500, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
                }
            }
        }
    }

    // =========================================================================
    // 3. PATIENTS API (JDBC SELECT & INSERT)
    // =========================================================================
    static class PatientsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    List<Patient> patients = patientDAO.getAllPatients();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < patients.size(); i++) {
                        Patient p = patients.get(i);
                        json.append(String.format("{\"id\":%d,\"name\":\"%s\",\"age\":%d,\"gender\":\"%s\",\"phone\":\"%s\",\"address\":\"%s\"}",
                                p.getPatientId(), escape(p.getFullName()), p.getAge(), escape(p.getGender()), escape(p.getPhone()), escape(p.getAddress())));
                        if (i < patients.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendJsonResponse(exchange, 200, json.toString());
                } catch (SQLException e) {
                    sendJsonResponse(exchange, 500, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
                }
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                try {
                    // Simple parse JSON parameters
                    String name = getJsonVal(body, "name");
                    int age = Integer.parseInt(getJsonVal(body, "age"));
                    String gender = getJsonVal(body, "gender");
                    String phone = getJsonVal(body, "phone");
                    String address = getJsonVal(body, "address");

                    Patient p = new Patient(name, age, gender, phone, address);
                    int newId = patientDAO.addPatient(p);
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"patient_id\":" + newId + "}");
                } catch (Exception e) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
                }
            }
        }
    }

    // =========================================================================
    // 4. APPOINTMENTS API (JDBC INNER JOIN & INSERT)
    // =========================================================================
    static class AppointmentsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    List<AppointmentDetails> list = appointmentDAO.getAllAppointmentDetails();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < list.size(); i++) {
                        AppointmentDetails ad = list.get(i);
                        json.append(String.format("{\"id\":%d,\"patient\":\"%s\",\"doctor\":\"%s\",\"spec\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"status\":\"%s\"}",
                                ad.getAppointmentId(), escape(ad.getPatientName()), escape(ad.getDoctorName()),
                                escape(ad.getDoctorSpecialization()), ad.getAppointmentDate(), ad.getAppointmentTime(), ad.getStatus()));
                        if (i < list.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendJsonResponse(exchange, 200, json.toString());
                } catch (SQLException e) {
                    sendJsonResponse(exchange, 500, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
                }
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                try {
                    int patientId = Integer.parseInt(getJsonVal(body, "patient_id"));
                    int doctorId = Integer.parseInt(getJsonVal(body, "doctor_id"));
                    LocalDate date = LocalDate.parse(getJsonVal(body, "date"));
                    LocalTime time = LocalTime.parse(getJsonVal(body, "time"));
                    String status = getJsonVal(body, "status");
                    if (status.isEmpty()) status = "Scheduled";

                    Appointment appt = new Appointment(patientId, doctorId, date, time, status);
                    int apptId = appointmentDAO.bookAppointment(appt);
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"appointment_id\":" + apptId + "}");
                } catch (SQLException e) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"Database Foreign Key Error: " + escape(e.getMessage()) + "\"}");
                } catch (Exception e) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
                }
            }
        }
    }

    // =========================================================================
    // 5. BILLING API (JDBC SUM & INSERT)
    // =========================================================================
    static class BillingApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    BigDecimal totalRev = billDAO.getTotalCollectedRevenue();
                    sendJsonResponse(exchange, 200, "{\"total_revenue\":" + totalRev + "}");
                } catch (SQLException e) {
                    sendJsonResponse(exchange, 500, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
                }
            }
        }
    }

    // Helper functions
    private static void enableCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void sendJsonResponse(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static String getJsonVal(String json, String key) {
        String pattern = "\"" + key + "\":";
        int idx = json.indexOf(pattern);
        if (idx == -1) return "";
        int start = idx + pattern.length();
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) start++;
        int end = start;
        while (end < json.length() && json.charAt(end) != '"' && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        return json.substring(start, end).trim();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }
}
