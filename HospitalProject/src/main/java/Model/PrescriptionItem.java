package Model;

import java.time.LocalDateTime;

public class PrescriptionItem {
    private int id;
    private int prescriptionId;
    private int drugId;
    private String dosage;
    private String note; 
    private int quantity;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy; 
    private Integer updatedBy; 

    public PrescriptionItem() {
    }

    public PrescriptionItem(int id, int prescriptionId, int drugId, String dosage, String note,
                            int quantity, String status, LocalDateTime createdAt, LocalDateTime updatedAt,
                            Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.prescriptionId = prescriptionId;
        this.drugId = drugId;
        this.dosage = dosage;
        this.note = note;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(int prescriptionId) { this.prescriptionId = prescriptionId; }
    public int getDrugId() { return drugId; }
    public void setDrugId(int drugId) { this.drugId = drugId; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
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