package Model;

import java.time.LocalDateTime;

public class Specialization {
    private int id;
    private String specializationName;
    private String description;
    private int headDoctorId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy; 
    private Integer updatedBy;
    
    private User headDoctor;

    public Specialization() {
    }

    public Specialization(int id, String specializationName, String description, int headDoctorId,
                          String status, LocalDateTime createdAt, LocalDateTime updatedAt,
                          Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.specializationName = specializationName;
        this.description = description;
        this.headDoctorId = headDoctorId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSpecializationName() { return specializationName; }
    public void setSpecializationName(String specializationName) { this.specializationName = specializationName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getHeadDoctorId() { return headDoctorId; }
    public void setHeadDoctorId(int headDoctorId) { this.headDoctorId = headDoctorId; }
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

    public User getHeadDoctor() {
        return headDoctor;
    }

    public void setHeadDoctor(User headDoctor) {
        this.headDoctor = headDoctor;
    }

}