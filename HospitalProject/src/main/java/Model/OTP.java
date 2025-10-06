package Model;

import java.time.LocalDateTime;

public class OTP {
    private int id;
    private int userId;
    private String otpCode;
    private String purpose;
    private LocalDateTime expiryTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy; 
    private Integer updatedBy; 

    public OTP() {
    }

    public OTP(int id, int userId, String otpCode, String purpose, LocalDateTime expiryTime,
               String status, LocalDateTime createdAt, LocalDateTime updatedAt,
               Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.userId = userId;
        this.otpCode = otpCode;
        this.purpose = purpose;
        this.expiryTime = expiryTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }
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