package com.studentcourse.dao;

import com.studentcourse.model.Registration;
import com.studentcourse.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {

    /**
     * Add new registration
     */
    public boolean addRegistration(Registration registration) {
        String sql = "INSERT INTO registrations (student_id, course_id, registration_date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, registration.getStudentId());
            pst.setInt(2, registration.getCourseId());
            pst.setDate(3, Date.valueOf(registration.getRegistrationDate()));
            pst.setString(4, registration.getStatus());
            
            int result = pst.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all registrations with student and course names
     */
    public List<Registration> getAllRegistrations() {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT r.registration_id, r.student_id, r.course_id, r.registration_date, r.status, " +
                     "s.student_name, c.course_name FROM registrations r " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN courses c ON r.course_id = c.course_id ORDER BY r.registration_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Registration reg = new Registration(
                    rs.getInt("registration_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getDate("registration_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("student_name"),
                    rs.getString("course_name")
                );
                registrations.add(reg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registrations;
    }

    /**
     * Get registration by ID
     */
    public Registration getRegistrationById(int registrationId) {
        String sql = "SELECT r.registration_id, r.student_id, r.course_id, r.registration_date, r.status, " +
                     "s.student_name, c.course_name FROM registrations r " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN courses c ON r.course_id = c.course_id WHERE r.registration_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, registrationId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return new Registration(
                    rs.getInt("registration_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getDate("registration_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("student_name"),
                    rs.getString("course_name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update registration status
     */
    public boolean updateRegistrationStatus(int registrationId, String status) {
        String sql = "UPDATE registrations SET status = ? WHERE registration_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, status);
            pst.setInt(2, registrationId);
            
            int result = pst.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete registration
     */
    public boolean deleteRegistration(int registrationId) {
        String sql = "DELETE FROM registrations WHERE registration_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, registrationId);
            int result = pst.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if student is already registered for a course with Active status
     */
    public boolean isDuplicateActiveRegistration(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND course_id = ? AND status = 'Active'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, studentId);
            pst.setInt(2, courseId);
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get total number of registrations (for dashboard)
     */
    public int getTotalRegistrations() {
        String sql = "SELECT COUNT(*) FROM registrations";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total active registrations for a course
     */
    public int getActiveRegistrationsForCourse(int courseId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE course_id = ? AND status = 'Active'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, courseId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total active registrations for a student
     */
    public int getActiveRegistrationsForStudent(int studentId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND status = 'Active'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
