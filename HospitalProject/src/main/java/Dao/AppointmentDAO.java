package Dao;

import Dao.DoctorDAO;
import DBConnection.DBConnection;
import Model.Appointment;
import Model.Doctor;
import Model.FamilyMember;
import Model.HealthInsurance;
import Model.Room;
import Model.Specialization;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    private Connection conn;

    public AppointmentDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public int createAppointment(Appointment appointment) {
        DoctorDAO doctorDAO = new DoctorDAO();
        Doctor doctor = doctorDAO.getDoctorById(appointment.getDoctorId());
        if (doctor == null) {
            return -1;
        }

        String query = "INSERT INTO Appointments (patient_id, specialization_id, room_id, doctor_id, appointment_date, appointment_time, health_insurance_id, reason, previous_appointment_id, is_follow_up, status, created_at, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, appointment.getPatientId());
            statement.setInt(2, appointment.getSpecializationId());
            statement.setInt(3, appointment.getRoomId());
            statement.setInt(4, doctor.getUserId());
            statement.setObject(5, appointment.getAppointmentDate());
            statement.setObject(6, appointment.getAppointmentTime());
            statement.setObject(7, appointment.getHealthInsuranceId(), java.sql.Types.INTEGER);
            statement.setString(8, appointment.getReason());
            statement.setObject(9, appointment.getPreviousAppointmentId(), java.sql.Types.INTEGER);
            statement.setBoolean(10, appointment.isFollowUp());
            statement.setString(11, appointment.getStatus());
            statement.setObject(12, appointment.getCreatedAt());
            statement.setObject(13, appointment.getCreatedBy(), java.sql.Types.INTEGER);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    appointment.setId(newId);
                    TimeSlotDAO timeSlotDAO = new TimeSlotDAO();
                    timeSlotDAO.updateSlotStatus(newId, "Booked");
                    return newId;
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println("Create appointment error: " + e.getMessage());
            return -1;
        }
    }

    public List<Appointment> getAppointmentsForUser(int userId, String search, String filterStatus, String sortBy, int page, int size) {
        List<Appointment> appointments = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT a.*,"
                + "u.full_name as patient_user_name, du.full_name as doctor_name, r.room_name, s.specialization_name, hi.insurance_number, hi.provider, "
                + "uc.full_name as created_by_name, up.full_name as updated_by_name "
                + "FROM Appointments a "
                + "LEFT JOIN Users u ON a.patient_id = u.id "
                + "LEFT JOIN Doctors d ON a.doctor_id = d.id "
                + "LEFT JOIN Users du ON d.user_id = du.id "
                + "LEFT JOIN Rooms r ON a.room_id = r.id "
                + "LEFT JOIN Specializations s ON a.specialization_id = s.id "
                + "LEFT JOIN HealthInsurances hi ON a.health_insurance_id = hi.id "
                + "LEFT JOIN Users uc ON a.created_by = uc.id "
                + "LEFT JOIN Users up ON a.updated_by = up.id "
                + "WHERE a.created_by = ? "
                + (search != null && !search.trim().isEmpty() ? "AND (u.full_name LIKE ? OR a.reason LIKE ? OR CAST(a.appointment_date AS NVARCHAR) LIKE ?)" : "")
                + (filterStatus != null && !filterStatus.trim().isEmpty() ? "AND a.status = ?" : "")
                + " ORDER BY " + (sortBy != null && !sortBy.trim().isEmpty() ? sortBy : "a.appointment_date DESC")
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"
        );

        try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            statement.setInt(paramIndex++, userId);
            if (search != null && !search.trim().isEmpty()) {
                String searchParam = "%" + search.trim() + "%";
                statement.setString(paramIndex++, searchParam);
                statement.setString(paramIndex++, searchParam);
                statement.setString(paramIndex++, searchParam);
                statement.setString(paramIndex++, searchParam);
            }
            if (filterStatus != null && !filterStatus.trim().isEmpty()) {
                statement.setString(paramIndex++, filterStatus);
            }
            statement.setInt(paramIndex++, (page - 1) * size);
            statement.setInt(paramIndex, size);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointments.add(mapAppointment(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Get appointments error: " + e.getMessage());
        }
        return appointments;
    }

    public int getTotalAppointmentsForUser(int userId, String search, String filterStatus) {
        StringBuilder query = new StringBuilder(
                "SELECT COUNT(*) FROM Appointments a "
                + "LEFT JOIN Users u ON a.patient_id = u.id "
                + "WHERE a.patient_id = ? "
                + (search != null && !search.trim().isEmpty() ? "AND (u.full_name LIKE ? OR a.reason LIKE ?)" : "")
                + (filterStatus != null && !filterStatus.trim().isEmpty() ? "AND a.status = ?" : "")
        );

        try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            statement.setInt(paramIndex++, userId);
            if (search != null && !search.trim().isEmpty()) {
                String searchParam = "%" + search.trim() + "%";
                statement.setString(paramIndex++, searchParam);
                statement.setString(paramIndex++, searchParam);
                statement.setString(paramIndex++, searchParam);
            }
            if (filterStatus != null && !filterStatus.trim().isEmpty()) {
                statement.setString(paramIndex, filterStatus);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Get total appointments error: " + e.getMessage());
        }
        return 0;
    }

    public Appointment getAppointmentById(int id, int userId) {
        String query
                = "SELECT a.*, "
                + "u.full_name as patient_user_name, du.full_name as doctor_name, r.room_name, s.specialization_name, hi.insurance_number, hi.provider, "
                + "uc.full_name as created_by_name, up.full_name as updated_by_name "
                + "FROM Appointments a "
                + "LEFT JOIN Users u ON a.patient_id = u.id "
                + "LEFT JOIN Doctors d ON a.doctor_id = d.id "
                + "LEFT JOIN Users du ON d.user_id = du.id "
                + "LEFT JOIN Rooms r ON a.room_id = r.id "
                + "LEFT JOIN Specializations s ON a.specialization_id = s.id "
                + "LEFT JOIN HealthInsurances hi ON a.health_insurance_id = hi.id "
                + "LEFT JOIN Users uc ON a.created_by = uc.id "
                + "LEFT JOIN Users up ON a.updated_by = up.id "
                + "WHERE a.id = ? AND a.created_by = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapAppointment(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("Get appointment by id error: " + e.getMessage());
        }
        return null;
    }
    
     public boolean updateAppointmentStatus(int bookingId, String status) {
        String query = "UPDATE Appointments SET status = ?, updated_at = GETDATE() WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, bookingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update appointment status error: " + e.getMessage());
            return false;
        }
    }

    private Appointment mapAppointment(ResultSet resultSet) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(resultSet.getInt("id"));
        appointment.setPatientId(resultSet.getInt("patient_id"));
        appointment.setSpecializationId(resultSet.getInt("specialization_id"));
        appointment.setRoomId(resultSet.getInt("room_id"));
        appointment.setDoctorId(resultSet.getInt("doctor_id"));
        appointment.setAppointmentDate(resultSet.getDate("appointment_date").toLocalDate());
        appointment.setAppointmentTime(resultSet.getTime("appointment_time").toLocalTime());
        appointment.setHealthInsuranceId(resultSet.getObject("health_insurance_id", Integer.class));
        appointment.setReason(resultSet.getString("reason"));
        appointment.setPreviousAppointmentId(resultSet.getObject("previous_appointment_id", Integer.class));
        appointment.setFollowUp(resultSet.getBoolean("is_follow_up"));
        appointment.setStatus(resultSet.getString("status"));
        appointment.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        appointment.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
        appointment.setCreatedBy(resultSet.getObject("created_by", Integer.class));
        appointment.setUpdatedBy(resultSet.getObject("updated_by", Integer.class));

        // Related Patient
//        FamilyMember patient = new FamilyMember();
//        String patientName = resultSet.getString("patient_name") != null ? resultSet.getString("patient_name") : resultSet.getString("patient_user_name");
//        patient.setFullName(patientName);
//        patient.setRelationship(resultSet.getString("relationship"));
//        patient.setBirthDate(resultSet.getObject("patient_birth_date", LocalDate.class));
//        patient.setGender(resultSet.getString("patient_gender"));
//        patient.setIdCardNumber(resultSet.getString("patient_id_card"));
//        appointment.setPatient(patient);

        // Doctor
        Doctor doctor = new Doctor();
        appointment.setDoctor(doctor);

        // Room
        Room room = new Room();
        room.setRoomName(resultSet.getString("room_name"));
        appointment.setRoom(room);

        // Specialization
        Specialization spec = new Specialization();
        spec.setSpecializationName(resultSet.getString("specialization_name"));
        appointment.setSpecialization(spec);

        // Health Insurance
        HealthInsurance hi = new HealthInsurance();
        hi.setInsuranceNumber(resultSet.getString("insurance_number"));
        hi.setProvider(resultSet.getString("provider"));
        appointment.setHealthInsurance(hi);

        return appointment;
    }
}
