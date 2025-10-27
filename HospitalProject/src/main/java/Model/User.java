package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {
    private int id;
    private int roleId;
    private String fullName;
    private String idCardNumber; 
    private String phoneNumber; 
    private LocalDate birthDate; 
    private String gender; 
    private String originalAddress; 
    private String country;
    private String ethnicity;
    private String occupation;
        private String provinceCity; 
    private String wardCommune; 
    private String detailedAddress; 
    private String username;
    private String profilePicture; 
    private String email;
    private String password;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy; 

    public User() {
    }

    public User(int id, int roleId, String fullName, String idCardNumber, String phoneNumber,
                LocalDate birthDate, String gender, String originalAddress, String country,
                String ethnicity, String occupation, String provinceCity, String wardCommune,
                String detailedAddress, String username, String profilePicture, String email,
                String password, String status, LocalDateTime createdAt, LocalDateTime updatedAt,
                Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.roleId = roleId;
        this.fullName = fullName;
        this.idCardNumber = idCardNumber;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.gender = gender;
        this.originalAddress = originalAddress;
        this.country = country;
        this.ethnicity = ethnicity;
        this.occupation = occupation;
        this.provinceCity = provinceCity;
        this.wardCommune = wardCommune;
        this.detailedAddress = detailedAddress;
        this.username = username;
        this.profilePicture = profilePicture;
        this.email = email;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getIdCardNumber() { return idCardNumber; }
    public void setIdCardNumber(String idCardNumber) { this.idCardNumber = idCardNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getOriginalAddress() { return originalAddress; }
    public void setOriginalAddress(String originalAddress) { this.originalAddress = originalAddress; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getEthnicity() { return ethnicity; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getProvinceCity() { return provinceCity; }
    public void setProvinceCity(String provinceCity) { this.provinceCity = provinceCity; }
    public String getWardCommune() { return wardCommune; }
    public void setWardCommune(String wardCommune) { this.wardCommune = wardCommune; }
    public String getDetailedAddress() { return detailedAddress; }
    public void setDetailedAddress(String detailedAddress) { this.detailedAddress = detailedAddress; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
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