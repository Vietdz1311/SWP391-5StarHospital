package Model;

import java.time.LocalDateTime;

public class AuditLog {
    private int id;
    private Integer userId; 
    private String tableName;
    private String action;
    private Integer recordId;
    private String description;
    private LocalDateTime createdAt;
    private Integer createdBy; 
    private Integer updatedBy;

    public AuditLog() {
    }

    public AuditLog(int id, Integer userId, String tableName, String action, Integer recordId,
                    String description, LocalDateTime createdAt, Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.userId = userId;
        this.tableName = tableName;
        this.action = action;
        this.recordId = recordId;
        this.description = description;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
}