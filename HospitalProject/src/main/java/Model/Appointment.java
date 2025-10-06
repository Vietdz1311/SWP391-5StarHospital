package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment {
    private int id;
    private int patientId;
    private int specializationId;
    private int roomId;
    private int doctorId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Integer healthInsuranceId; 
    private String reason;
    private Integer previousAppointmentId;
    private boolean isFollowUp;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy; 
    private Integer updatedBy; 
    
    private FamilyMember patient; // Or User if self
    private Doctor doctor;
    private Room room;
    private Specialization specialization;
    private HealthInsurance healthInsurance;
    private Appointment previousAppointment;

    public Appointment() {
    }

    public Appointment(int id, int patientId, int specializationId, int roomId, int doctorId,
                       LocalDate appointmentDate, LocalTime appointmentTime, Integer healthInsuranceId,
                       String reason, Integer previousAppointmentId, boolean isFollowUp, String status,
                       LocalDateTime createdAt, LocalDateTime updatedAt, Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.patientId = patientId;
        this.specializationId = specializationId;
        this.roomId = roomId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.healthInsuranceId = healthInsuranceId;
        this.reason = reason;
        this.previousAppointmentId = previousAppointmentId;
        this.isFollowUp = isFollowUp;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getSpecializationId() { return specializationId; }
    public void setSpecializationId(int specializationId) { this.specializationId = specializationId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public Integer getHealthInsuranceId() { return healthInsuranceId; }
    public void setHealthInsuranceId(Integer healthInsuranceId) { this.healthInsuranceId = healthInsuranceId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getPreviousAppointmentId() { return previousAppointmentId; }
    public void setPreviousAppointmentId(Integer previousAppointmentId) { this.previousAppointmentId = previousAppointmentId; }
    public boolean isFollowUp() { return isFollowUp; }
    public void setFollowUp(boolean isFollowUp) { this.isFollowUp = isFollowUp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
    
    public FamilyMember getPatient() { return patient; }
    public void setPatient(FamilyMember patient) { this.patient = patient; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public Specialization getSpecialization() { return specialization; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }
    public HealthInsurance getHealthInsurance() { return healthInsurance; }
    public void setHealthInsurance(HealthInsurance healthInsurance) { this.healthInsurance = healthInsurance; }
    public Appointment getPreviousAppointment() { return previousAppointment; }
    public void setPreviousAppointment(Appointment previousAppointment) { this.previousAppointment = previousAppointment; }
}