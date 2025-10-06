package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicalRecord {
    private int id;
    private int appointmentId;
    private int doctorId;
    private String examinationResult;
    private LocalDate reExaminationDate; 
    private Integer previousMedicalRecordId; 
    private String type;
    private String note; 
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy; 

    public MedicalRecord() {
    }

    public MedicalRecord(int id, int appointmentId, int doctorId, String examinationResult,
                         LocalDate reExaminationDate, Integer previousMedicalRecordId, String type,
                         String note, String status, LocalDateTime createdAt, LocalDateTime updatedAt,
                         Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.examinationResult = examinationResult;
        this.reExaminationDate = reExaminationDate;
        this.previousMedicalRecordId = previousMedicalRecordId;
        this.type = type;
        this.note = note;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public String getExaminationResult() { return examinationResult; }
    public void setExaminationResult(String examinationResult) { this.examinationResult = examinationResult; }
    public LocalDate getReExaminationDate() { return reExaminationDate; }
    public void setReExaminationDate(LocalDate reExaminationDate) { this.reExaminationDate = reExaminationDate; }
    public Integer getPreviousMedicalRecordId() { return previousMedicalRecordId; }
    public void setPreviousMedicalRecordId(Integer previousMedicalRecordId) { this.previousMedicalRecordId = previousMedicalRecordId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
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
}