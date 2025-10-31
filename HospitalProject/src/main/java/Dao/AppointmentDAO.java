package DAO;

import DBConnection.DBConnection;
import Model.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    /* ==========================================================
       1️⃣ Lấy danh sách Appointment có phân trang, tìm kiếm, lọc
       ========================================================== */
    public List<Appointment> getAllAppointments(String search, Integer doctorId, Integer specializationId,
            String sortBy, int page, int size) {
        List<Appointment> list = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT a.*, "
                + "u.full_name AS patient_name, "
                + "d.id AS doctor_id, du.full_name AS doctor_name, "
                + "s.specialization_name, "
                + "r.room_name "
                + "FROM Appointments a "
                + "LEFT JOIN FamilyMembers f ON a.patient_id = f.id "
                + "LEFT JOIN Users u ON f.user_id = u.id "
                + "LEFT JOIN Doctors d ON a.doctor_id = d.id "
                + "LEFT JOIN Users du ON d.user_id = du.id "
                + "LEFT JOIN Specializations s ON a.specialization_id = s.id "
                + "LEFT JOIN Rooms r ON a.room_id = r.id "
                + "WHERE 1=1 "
        );

        if (search != null && !search.trim().isEmpty()) {
            query.append(" AND (u.full_name LIKE ? OR du.full_name LIKE ? OR s.specialization_name LIKE ?)");
        }
        if (doctorId != null) {
            query.append(" AND a.doctor_id = ?");
        }
        if (specializationId != null) {
            query.append(" AND a.specialization_id = ?");
        }

        query.append(" ORDER BY ");
        query.append((sortBy != null && !sortBy.trim().isEmpty()) ? sortBy : "a.created_at DESC");
        query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
            int idx = 1;
            if (search != null && !search.trim().isEmpty()) {
                String s = "%" + search.trim() + "%";
                ps.setString(idx++, s);
                ps.setString(idx++, s);
                ps.setString(idx++, s);
            }
            if (doctorId != null) {
                ps.setInt(idx++, doctorId);
            }
            if (specializationId != null) {
                ps.setInt(idx++, specializationId);
            }
            ps.setInt(idx++, (page - 1) * size);
            ps.setInt(idx, size);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Appointment ap = mapAppointment(rs);
                // Gán thêm thông tin phụ
                FamilyMember patient = new FamilyMember();
                User patientUser = new User();
                patientUser.setFullName(rs.getString("patient_name"));
                patient.setFullName(patientUser.getFullName());
                ap.setPatient(patient);

                Doctor doc = new Doctor();
                User docUser = new User();
                docUser.setFullName(rs.getString("doctor_name"));
                doc.setUser(docUser);
                ap.setDoctor(doc);

                Specialization sp = new Specialization();
                sp.setSpecializationName(rs.getString("specialization_name"));
                ap.setSpecialization(sp);

                Room room = new Room();
                room.setRoomName(rs.getString("room_name"));
                ap.setRoom(room);

                list.add(ap);
            }

        } catch (SQLException e) {
            System.out.println("Get all appointments error: " + e.getMessage());
        }
        return list;
    }

    /* ==========================================================
       2️⃣ Đếm tổng số Appointment cho phân trang
       ========================================================== */
    public int getTotalAppointments(String search, Integer doctorId, Integer specializationId) {
        StringBuilder query = new StringBuilder(
                "SELECT COUNT(*) "
                + "FROM Appointments a "
                + "LEFT JOIN FamilyMembers f ON a.patient_id = f.id "
                + "LEFT JOIN Users u ON f.user_id = u.id "
                + "LEFT JOIN Doctors d ON a.doctor_id = d.id "
                + "LEFT JOIN Users du ON d.user_id = du.id "
                + "LEFT JOIN Specializations s ON a.specialization_id = s.id "
                + "WHERE 1=1"
        );

        if (search != null && !search.trim().isEmpty()) {
            query.append(" AND (u.full_name LIKE ? OR du.full_name LIKE ? OR s.specialization_name LIKE ?)");
        }
        if (doctorId != null) {
            query.append(" AND a.doctor_id = ?");
        }
        if (specializationId != null) {
            query.append(" AND a.specialization_id = ?");
        }

        try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
            int idx = 1;
            if (search != null && !search.trim().isEmpty()) {
                String s = "%" + search.trim() + "%";
                ps.setString(idx++, s);
                ps.setString(idx++, s);
                ps.setString(idx++, s);
            }
            if (doctorId != null) {
                ps.setInt(idx++, doctorId);
            }
            if (specializationId != null) {
                ps.setInt(idx++, specializationId);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Get total appointments error: " + e.getMessage());
        }
        return 0;
    }

    /* ==========================================================
       4️⃣ Thêm Appointment
       ========================================================== */
    public boolean addAppointment(Appointment ap) {
        String sql
                = "INSERT INTO Appointments "
                + "(patient_id, specialization_id, room_id, doctor_id, "
                + " appointment_date, appointment_time, health_insurance_id, "
                + " reason, previous_appointment_id, is_follow_up, status, "
                + " created_at, updated_at, created_by, updated_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ap.getPatientId());
            ps.setInt(2, ap.getSpecializationId());
            ps.setInt(3, ap.getRoomId());
            ps.setInt(4, ap.getDoctorId());
            ps.setDate(5, Date.valueOf(ap.getAppointmentDate()));
            ps.setTime(6, Time.valueOf(ap.getAppointmentTime()));
            if (ap.getHealthInsuranceId() != null) {
                ps.setInt(7, ap.getHealthInsuranceId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, ap.getReason());
            if (ap.getPreviousAppointmentId() != null) {
                ps.setInt(9, ap.getPreviousAppointmentId());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            ps.setBoolean(10, ap.isFollowUp());
            ps.setString(11, ap.getStatus());
            ps.setTimestamp(12, Timestamp.valueOf(ap.getCreatedAt()));
            ps.setTimestamp(13, Timestamp.valueOf(ap.getUpdatedAt()));
            if (ap.getCreatedBy() != null) {
                ps.setInt(14, ap.getCreatedBy());
            } else {
                ps.setNull(14, Types.INTEGER);
            }
            if (ap.getUpdatedBy() != null) {
                ps.setInt(15, ap.getUpdatedBy());
            } else {
                ps.setNull(15, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add appointment error: " + e.getMessage());
            return false;
        }
    }

    /* ==========================================================
       5️⃣ Cập nhật Appointment
       ========================================================== */
    public boolean updateAppointment(Appointment ap) {
        String sql
                = "UPDATE Appointments "
                + "SET patient_id=?, specialization_id=?, room_id=?, doctor_id=?, "
                + "appointment_date=?, appointment_time=?, health_insurance_id=?, "
                + "reason=?, previous_appointment_id=?, is_follow_up=?, status=?, "
                + "updated_at=?, updated_by=? "
                + "WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ap.getPatientId());
            ps.setInt(2, ap.getSpecializationId());
            ps.setInt(3, ap.getRoomId());
            ps.setInt(4, ap.getDoctorId());
            ps.setDate(5, Date.valueOf(ap.getAppointmentDate()));
            ps.setTime(6, Time.valueOf(ap.getAppointmentTime()));
            if (ap.getHealthInsuranceId() != null) {
                ps.setInt(7, ap.getHealthInsuranceId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, ap.getReason());
            if (ap.getPreviousAppointmentId() != null) {
                ps.setInt(9, ap.getPreviousAppointmentId());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            ps.setBoolean(10, ap.isFollowUp());
            ps.setString(11, ap.getStatus());
            ps.setTimestamp(12, Timestamp.valueOf(ap.getUpdatedAt()));
            if (ap.getUpdatedBy() != null) {
                ps.setInt(13, ap.getUpdatedBy());
            } else {
                ps.setNull(13, Types.INTEGER);
            }
            ps.setInt(14, ap.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update appointment error: " + e.getMessage());
            return false;
        }
    }

    /* ==========================================================
       6️⃣ Xóa Appointment
       ========================================================== */
    public boolean deleteAppointment(int id) {
        String sql = "DELETE FROM Appointments WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete appointment error: " + e.getMessage());
            return false;
        }
    }

    public Appointment getAppointmentById(int id) {
        Appointment appointment = null;

        String sql
                = "SELECT "
                + "    a.id, a.patient_id, a.specialization_id, a.room_id, a.doctor_id, "
                + "    a.appointment_date, a.appointment_time, a.health_insurance_id, a.reason, "
                + "    a.previous_appointment_id, a.is_follow_up, a.status, "
                + "    a.created_at, a.updated_at, a.created_by, a.updated_by, "
                + "    u.full_name AS patient_name, "
                + "    du.full_name AS doctor_name, "
                + "    d.id AS doc_id, "
                + "    s.specialization_name, r.room_name, "
                + "    hi.provider AS provider_name "
                + "FROM Appointments a "
                + "JOIN Users u ON a.patient_id = u.id "
                + "JOIN Users du ON a.doctor_id = du.id "
                + "JOIN Doctors d ON d.user_id = du.id "
                + "JOIN Rooms r ON a.room_id = r.id "
                + "JOIN Specializations s ON a.specialization_id = s.id "
                + "LEFT JOIN HealthInsurances hi ON a.health_insurance_id = hi.id "
                + "WHERE a.id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                appointment = new Appointment();

                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(rs.getInt("patient_id"));
                appointment.setSpecializationId(rs.getInt("specialization_id"));
                appointment.setRoomId(rs.getInt("room_id"));
                appointment.setDoctorId(rs.getInt("doctor_id"));

                Date date = rs.getDate("appointment_date");
                Time time = rs.getTime("appointment_time");
                if (date != null) {
                    appointment.setAppointmentDate(date.toLocalDate());
                }
                if (time != null) {
                    appointment.setAppointmentTime(time.toLocalTime());
                }

                appointment.setHealthInsuranceId((Integer) rs.getObject("health_insurance_id"));
                appointment.setReason(rs.getString("reason"));
                appointment.setPreviousAppointmentId((Integer) rs.getObject("previous_appointment_id"));
                appointment.setFollowUp(rs.getBoolean("is_follow_up"));
                appointment.setStatus(rs.getString("status"));

                Timestamp created = rs.getTimestamp("created_at");
                Timestamp updated = rs.getTimestamp("updated_at");
                if (created != null) {
                    appointment.setCreatedAt(created.toLocalDateTime());
                }
                if (updated != null) {
                    appointment.setUpdatedAt(updated.toLocalDateTime());
                }

                appointment.setCreatedBy((Integer) rs.getObject("created_by"));
                appointment.setUpdatedBy((Integer) rs.getObject("updated_by"));

                // --- Object mapping ---
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("doc_id"));
                User doctorUser = new User();
                doctorUser.setFullName(rs.getString("doctor_name"));
                doctor.setUser(doctorUser);
                appointment.setDoctor(doctor);

                Room room = new Room();
                room.setRoomName(rs.getString("room_name"));
                appointment.setRoom(room);

                Specialization spec = new Specialization();
                spec.setSpecializationName(rs.getString("specialization_name"));
                appointment.setSpecialization(spec);

                HealthInsurance hi = new HealthInsurance();
                hi.setProvider(rs.getString("provider_name"));
                appointment.setHealthInsurance(hi);

                FamilyMember patient = new FamilyMember();
                patient.setFullName(rs.getString("patient_name"));
                appointment.setPatient(patient);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointment;
    }

    public List<Appointment> getAppointmentsByUsername(String username, int page, int pageSize) {
        List<Appointment> list = new ArrayList<>();

        String sql
                = "SELECT a.id, a.appointment_date, a.appointment_time, a.status, "
                + "       d.id AS doctor_id, du.full_name AS doctor_name, "
                + "       s.id AS spec_id, s.specialization_name, "
                + "       r.id AS room_id, r.room_name "
                + "FROM Appointments a "
                + "JOIN Users u ON a.patient_id = u.id "
                + "JOIN Users du ON a.doctor_id = du.id "
                + "JOIN Specializations s ON a.specialization_id = s.id "
                + "JOIN Rooms r ON a.room_id = r.id "
                + "JOIN Doctors d ON d.user_id = du.id "
                + "WHERE u.username = ? "
                + "ORDER BY a.appointment_date DESC, a.appointment_time DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Appointment a = new Appointment();
                a.setId(rs.getInt("id"));
                LocalDate apDate = rs.getObject("appointment_date", java.time.LocalDate.class);
                LocalTime apTime = rs.getObject("appointment_time", java.time.LocalTime.class);
                a.setAppointmentDate(apDate);   // nếu method nhận LocalDate
                a.setAppointmentTime(apTime);
                a.setStatus(rs.getString("status"));

                Doctor doctor = new Doctor();
                User doctorUser = new User();
                doctorUser.setFullName(rs.getString("doctor_name"));
                doctor.setUser(doctorUser);
                a.setDoctor(doctor);

                Specialization s = new Specialization();
                s.setSpecializationName(rs.getString("specialization_name"));
                a.setSpecialization(s);

                Room r = new Room();
                r.setRoomName(rs.getString("room_name"));
                a.setRoom(r);

                list.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getTotalAppointmentsByUserId(int userId) {
        String sql
                = "SELECT COUNT(*) "
                + "FROM Appointments a "
                + "LEFT JOIN FamilyMembers f ON a.patient_id = f.id "
                + "LEFT JOIN Users u ON f.user_id = u.id "
                + "WHERE u.id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting appointments: " + e.getMessage());
        }
        return 0;
    }

    public int countAppointmentsByUsername(String username) {
        String sql = "SELECT COUNT(*) "
                + "FROM Appointments a "
                + "INNER JOIN Users u ON a.patient_id = u.id "
                + "WHERE u.username = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting appointments: " + e.getMessage());
        }
        return 0;
    }

    /* ==========================================================
       7️⃣ Map ResultSet → Appointment Object
       ========================================================== */
    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        Appointment ap = new Appointment();
        ap.setId(rs.getInt("id"));
        ap.setPatientId(rs.getInt("patient_id"));
        ap.setSpecializationId(rs.getInt("specialization_id"));
        ap.setRoomId(rs.getInt("room_id"));
        ap.setDoctorId(rs.getInt("doctor_id"));
        ap.setAppointmentDate(rs.getObject("appointment_date", LocalDate.class));
        ap.setAppointmentTime(rs.getObject("appointment_time", LocalTime.class));
        ap.setHealthInsuranceId(rs.getObject("health_insurance_id", Integer.class));
        ap.setReason(rs.getString("reason"));
        ap.setPreviousAppointmentId(rs.getObject("previous_appointment_id", Integer.class));
        ap.setFollowUp(rs.getBoolean("is_follow_up"));
        ap.setStatus(rs.getString("status"));
        ap.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        ap.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        ap.setCreatedBy(rs.getObject("created_by", Integer.class));
        ap.setUpdatedBy(rs.getObject("updated_by", Integer.class));
        return ap;
    }
}
