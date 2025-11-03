/* =========================================================
   0) DROP DATABASE (nếu tồn tại) - Đóng tất cả connections
   ========================================================= */

-- Chuyển sang master database để có thể drop HospitalDB
USE master;
GO

-- Đóng tất cả connections và drop database nếu tồn tại
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'HospitalDB')
BEGIN
    -- Đóng tất cả connections đến HospitalDB (ROLLBACK IMMEDIATE sẽ ngắt tất cả transactions)
    ALTER DATABASE HospitalDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    
    -- Drop database
    DROP DATABASE HospitalDB;
END
GO

/* =========================================================
   1) TẠO DATABASE
   ========================================================= */
CREATE DATABASE HospitalDB;
GO

USE HospitalDB;
GO

/* =========================================================
   2) DROP TRIGGERS (nếu tồn tại)
   ========================================================= */
IF OBJECT_ID('cleanup_timeslots', 'TR') IS NOT NULL DROP TRIGGER cleanup_timeslots;
GO

/* =========================================================
   3) DROP TABLES (theo thứ tự ngược phụ thuộc)
   ========================================================= */
IF OBJECT_ID('PrescriptionItems', 'U') IS NOT NULL DROP TABLE PrescriptionItems;
IF OBJECT_ID('Prescriptions', 'U')     IS NOT NULL DROP TABLE Prescriptions;
IF OBJECT_ID('Drugs', 'U')             IS NOT NULL DROP TABLE Drugs;
IF OBJECT_ID('MedicalRecords', 'U')    IS NOT NULL DROP TABLE MedicalRecords;
IF OBJECT_ID('Appointments', 'U')      IS NOT NULL DROP TABLE Appointments;
IF OBJECT_ID('HealthInsurances', 'U')  IS NOT NULL DROP TABLE HealthInsurances;
IF OBJECT_ID('TimeSlots', 'U')         IS NOT NULL DROP TABLE TimeSlots;
IF OBJECT_ID('Rooms', 'U')             IS NOT NULL DROP TABLE Rooms;
IF OBJECT_ID('Areas', 'U')             IS NOT NULL DROP TABLE Areas;
IF OBJECT_ID('Doctors', 'U')           IS NOT NULL DROP TABLE Doctors;
IF OBJECT_ID('Specializations', 'U')   IS NOT NULL DROP TABLE Specializations;
IF OBJECT_ID('Hospitals', 'U')         IS NOT NULL DROP TABLE Hospitals;
IF OBJECT_ID('Posts', 'U')             IS NOT NULL DROP TABLE Posts;
IF OBJECT_ID('Categories', 'U')        IS NOT NULL DROP TABLE Categories;
IF OBJECT_ID('OTP', 'U')               IS NOT NULL DROP TABLE OTP;
IF OBJECT_ID('Notifications', 'U')     IS NOT NULL DROP TABLE Notifications;
IF OBJECT_ID('AuditLogs', 'U')         IS NOT NULL DROP TABLE AuditLogs;
IF OBJECT_ID('Users', 'U')             IS NOT NULL DROP TABLE Users;
IF OBJECT_ID('Roles', 'U')             IS NOT NULL DROP TABLE Roles;
GO

/* =========================================================
   4) CREATE TABLES
   ========================================================= */

-- Roles
CREATE TABLE Roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    role_name NVARCHAR(50) NOT NULL UNIQUE CHECK (role_name IN ('patient', 'doctor', 'admin', 'manager')),
    description NVARCHAR(1000),
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT CHK_Roles_updated_at CHECK (updated_at >= created_at)
);
GO

-- Seed Roles
INSERT INTO Roles (role_name, description) VALUES
('patient',  N'Bệnh nhân - Người dùng thông thường đặt lịch khám'),
('doctor',   N'Bác sĩ - Đăng nhập để quản lý lịch khám và hồ sơ bệnh án'),
('admin',    N'Quản trị viên - Quản lý hệ thống toàn diện'),
('manager',  N'Quản lý - Quản lý khu vực hoặc chuyên khoa cụ thể');
GO

-- Users (rút gọn phần địa chỉ & bỏ occupation)
CREATE TABLE Users (
    id INT PRIMARY KEY IDENTITY(1,1),
    role_id INT NOT NULL,
    full_name NVARCHAR(255) NOT NULL,
    id_card_number NVARCHAR(20) UNIQUE,
    phone_number NVARCHAR(15) UNIQUE,
    birth_date DATE,
    gender NVARCHAR(10) CHECK (gender IN ('Male', 'Female')),
    address NVARCHAR(1000), -- địa chỉ gộp
    username NVARCHAR(50) NOT NULL UNIQUE,
    profile_picture NVARCHAR(500) NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    status NVARCHAR(20) NOT NULL CHECK (status IN ('pending', 'active', 'inactive', 'suspended')) DEFAULT 'pending',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Users_Roles FOREIGN KEY (role_id) REFERENCES Roles(id),
    CONSTRAINT FK_Users_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Users_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Users_updated_at CHECK (updated_at >= created_at)
);
GO

-- Hospitals
CREATE TABLE Hospitals (
    id INT PRIMARY KEY IDENTITY(1,1),
    hospital_name NVARCHAR(255) NOT NULL UNIQUE,
    address NVARCHAR(1000) NOT NULL,
    phone_number NVARCHAR(15),
    email NVARCHAR(255) UNIQUE,
    status NVARCHAR(20) NOT NULL CHECK (status IN ('active', 'inactive')) DEFAULT 'active',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Hospitals_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Hospitals_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Hospitals_updated_at CHECK (updated_at >= created_at)
);
GO

-- Specializations
CREATE TABLE Specializations (
    id INT PRIMARY KEY IDENTITY(1,1),
    specialization_name NVARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(1000) NOT NULL,
    head_doctor_id INT NOT NULL, -- Users(id) with role 'doctor'
    status NVARCHAR(20) NOT NULL CHECK (status IN ('active', 'inactive')) DEFAULT 'active',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Specializations_Users FOREIGN KEY (head_doctor_id) REFERENCES Users(id),
    CONSTRAINT FK_Specializations_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Specializations_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Specializations_updated_at CHECK (updated_at >= created_at)
);
GO

-- Doctors
CREATE TABLE Doctors (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL UNIQUE,
    license_number NVARCHAR(100) UNIQUE,
    specialization_id INT,
    years_of_experience INT CHECK (years_of_experience >= 0),
    certification NVARCHAR(1000),
    bio NVARCHAR(1000),
    status NVARCHAR(20) NOT NULL CHECK (status IN ('active', 'inactive')) DEFAULT 'active',
    is_verified BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Doctors_Users FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT FK_Doctors_Specializations FOREIGN KEY (specialization_id) REFERENCES Specializations(id),
    CONSTRAINT FK_Doctors_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Doctors_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Doctors_updated_at CHECK (updated_at >= created_at)
);
GO

-- Areas
CREATE TABLE Areas (
    id INT PRIMARY KEY IDENTITY(1,1),
    area_name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(1000),
    status NVARCHAR(20) NOT NULL CHECK (status IN ('active', 'inactive')) DEFAULT 'active',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Areas_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Areas_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Areas_updated_at CHECK (updated_at >= created_at)
);
GO

-- Rooms
CREATE TABLE Rooms (
    id INT PRIMARY KEY IDENTITY(1,1),
    room_name NVARCHAR(255) NOT NULL,
    specialization_id INT NOT NULL,
    area_id INT NOT NULL,
    status NVARCHAR(20) NOT NULL CHECK (status IN ('available', 'maintenance')) DEFAULT 'available',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Rooms_Specializations FOREIGN KEY (specialization_id) REFERENCES Specializations(id),
    CONSTRAINT FK_Rooms_Areas FOREIGN KEY (area_id) REFERENCES Areas(id),
    CONSTRAINT FK_Rooms_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Rooms_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Rooms_updated_at CHECK (updated_at >= created_at)
);
GO

-- TimeSlots
CREATE TABLE TimeSlots (
    id INT PRIMARY KEY IDENTITY(1,1),
    room_id INT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status NVARCHAR(20) NOT NULL CHECK (status IN ('available', 'booked', 'cancelled')) DEFAULT 'available',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_TimeSlots_Rooms FOREIGN KEY (room_id) REFERENCES Rooms(id),
    CONSTRAINT FK_TimeSlots_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_TimeSlots_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_TimeSlots_updated_at CHECK (updated_at >= created_at)
);
GO

-- HealthInsurances
CREATE TABLE HealthInsurances (
    id INT PRIMARY KEY IDENTITY(1,1),
    patient_id INT NOT NULL,
    insurance_number NVARCHAR(20) NOT NULL UNIQUE,
    provider NVARCHAR(255) NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE,
    status NVARCHAR(20) NOT NULL CHECK (status IN ('valid', 'expired', 'invalid')) DEFAULT 'valid',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_HealthInsurances_Users FOREIGN KEY (patient_id) REFERENCES Users(id),
    CONSTRAINT FK_HealthInsurances_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_HealthInsurances_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_HealthInsurances_updated_at CHECK (updated_at >= created_at)
);
GO

-- Appointments
CREATE TABLE Appointments (
    id INT PRIMARY KEY IDENTITY(1,1),
    patient_id INT NOT NULL,
    specialization_id INT NOT NULL,
    room_id INT NOT NULL,
    doctor_id INT NOT NULL, -- Users(id) with role 'doctor'
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    health_insurance_id INT,
    reason NVARCHAR(1000) NOT NULL,
    previous_appointment_id INT,
    is_follow_up BIT NOT NULL DEFAULT 0,
    status NVARCHAR(20) NOT NULL CHECK (status IN ('pending', 'confirmed', 'cancelled', 'completed')) DEFAULT 'pending',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Appointments_Users FOREIGN KEY (patient_id) REFERENCES Users(id),
    CONSTRAINT FK_Appointments_Specializations FOREIGN KEY (specialization_id) REFERENCES Specializations(id),
    CONSTRAINT FK_Appointments_Rooms FOREIGN KEY (room_id) REFERENCES Rooms(id),
    CONSTRAINT FK_Appointments_Doctors FOREIGN KEY (doctor_id) REFERENCES Users(id),
    CONSTRAINT FK_Appointments_HealthInsurances FOREIGN KEY (health_insurance_id) REFERENCES HealthInsurances(id),
    CONSTRAINT FK_Appointments_Appointments FOREIGN KEY (previous_appointment_id) REFERENCES Appointments(id),
    CONSTRAINT FK_Appointments_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Appointments_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Appointments_updated_at CHECK (updated_at >= created_at)
);
GO

-- MedicalRecords
CREATE TABLE MedicalRecords (
    id INT PRIMARY KEY IDENTITY(1,1),
    appointment_id INT NOT NULL,
    doctor_id INT NOT NULL, -- Users(id) with role 'doctor'
    examination_result NVARCHAR(1000) NOT NULL,
    re_examination_date DATE,
    previous_medical_record_id INT,
    type NVARCHAR(20) NOT NULL CHECK (type IN ('initial', 'follow_up', 'routine')) DEFAULT 'initial',
    note NVARCHAR(1000),
    status NVARCHAR(20) NOT NULL CHECK (status IN ('draft', 'finalized', 'archived')) DEFAULT 'draft',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_MedicalRecords_Appointments FOREIGN KEY (appointment_id) REFERENCES Appointments(id),
    CONSTRAINT FK_MedicalRecords_Doctors FOREIGN KEY (doctor_id) REFERENCES Users(id),
    CONSTRAINT FK_MedicalRecords_MedicalRecords FOREIGN KEY (previous_medical_record_id) REFERENCES MedicalRecords(id),
    CONSTRAINT FK_MedicalRecords_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_MedicalRecords_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_MedicalRecords_updated_at CHECK (updated_at >= created_at)
);
GO

-- Prescriptions
CREATE TABLE Prescriptions (
    id INT PRIMARY KEY IDENTITY(1,1),
    medical_record_id INT NOT NULL,
    doctor_id INT NOT NULL, -- Users(id) with role 'doctor'
    issue_date DATE NOT NULL DEFAULT SYSDATETIME(),
    status NVARCHAR(20) NOT NULL CHECK (status IN ('issued', 'dispensed', 'expired')) DEFAULT 'issued',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Prescriptions_MedicalRecords FOREIGN KEY (medical_record_id) REFERENCES MedicalRecords(id),
    CONSTRAINT FK_Prescriptions_Doctors FOREIGN KEY (doctor_id) REFERENCES Users(id),
    CONSTRAINT FK_Prescriptions_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Prescriptions_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Prescriptions_updated_at CHECK (updated_at >= created_at)
);
GO

-- Drugs
CREATE TABLE Drugs (
    id INT PRIMARY KEY IDENTITY(1,1),
    drug_name NVARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(1000),
    status NVARCHAR(20) NOT NULL CHECK (status IN ('active', 'inactive')) DEFAULT 'active',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Drugs_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Drugs_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Drugs_updated_at CHECK (updated_at >= created_at)
);
GO

-- PrescriptionItems
CREATE TABLE PrescriptionItems (
    id INT PRIMARY KEY IDENTITY(1,1),
    prescription_id INT NOT NULL,
    drug_id INT NOT NULL,
    dosage NVARCHAR(255) NOT NULL,
    note NVARCHAR(1000),
    quantity INT NOT NULL,
    status NVARCHAR(20) NOT NULL CHECK (status IN ('active', 'completed')) DEFAULT 'active',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_PrescriptionItems_Prescriptions FOREIGN KEY (prescription_id) REFERENCES Prescriptions(id),
    CONSTRAINT FK_PrescriptionItems_Drugs FOREIGN KEY (drug_id) REFERENCES Drugs(id),
    CONSTRAINT CHK_PrescriptionItems_updated_at CHECK (updated_at >= created_at)
);
GO

-- Categories (Blog)
CREATE TABLE Categories (
    id INT PRIMARY KEY IDENTITY(1,1),
    category_name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(1000),
    status NVARCHAR(20) NOT NULL CHECK (status IN ('active', 'inactive')) DEFAULT 'active',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT CHK_Categories_updated_at CHECK (updated_at >= created_at),
    CONSTRAINT FK_Categories_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Categories_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id)
);
GO

-- Posts (Blog)
CREATE TABLE Posts (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    content NVARCHAR(1000) NOT NULL,
    image_url NVARCHAR(255),
    status NVARCHAR(20) NOT NULL CHECK (status IN ('draft', 'published', 'deleted')) DEFAULT 'draft',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Posts_Users FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT FK_Posts_Categories FOREIGN KEY (category_id) REFERENCES Categories(id),
    CONSTRAINT FK_Posts_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Posts_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Posts_updated_at CHECK (updated_at >= created_at)
);
GO

-- OTP
CREATE TABLE OTP (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    otp_code NVARCHAR(255) NOT NULL,
    purpose NVARCHAR(50) NOT NULL CHECK (purpose IN ('register', 'change_password', 'login', 'verify_registration')),
    expiry_time DATETIME2 NOT NULL,
    status NVARCHAR(20) NOT NULL CHECK (status IN ('unused', 'used', 'expired')) DEFAULT 'unused',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_OTP_Users FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT FK_OTP_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_OTP_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_OTP_updated_at CHECK (updated_at >= created_at)
);
GO

-- Notifications
CREATE TABLE Notifications (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    type NVARCHAR(50) NOT NULL CHECK (type IN ('appointment_reminder', 're_examination', 'otp', 'general')),
    content NVARCHAR(1000) NOT NULL,
    sent_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    status NVARCHAR(20) NOT NULL CHECK (status IN ('sent', 'read', 'failed')) DEFAULT 'sent',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_Notifications_Users FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT FK_Notifications_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_Notifications_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id),
    CONSTRAINT CHK_Notifications_updated_at CHECK (updated_at >= created_at)
);
GO

-- AuditLogs
CREATE TABLE AuditLogs (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NULL,
    table_name NVARCHAR(100) NOT NULL,
    action NVARCHAR(50) NOT NULL CHECK (action IN ('CREATE', 'UPDATE', 'DELETE')),
    record_id INT NULL,
    description NVARCHAR(1000),
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    created_by INT NULL,
    updated_by INT NULL,
    CONSTRAINT FK_AuditLogs_Users FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT FK_AuditLogs_Users_CreatedBy FOREIGN KEY (created_by) REFERENCES Users(id),
    CONSTRAINT FK_AuditLogs_Users_UpdatedBy FOREIGN KEY (updated_by) REFERENCES Users(id)
);
GO

/* =========================================================
   5) TRIGGERS
   ========================================================= */

-- Dọn TimeSlots cũ hơn 7 ngày sau mỗi INSERT
CREATE TRIGGER cleanup_timeslots
ON TimeSlots
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM TimeSlots
    WHERE date < DATEADD(day, -7, CAST(SYSDATETIME() AS DATE));

    INSERT INTO AuditLogs (user_id, table_name, action, record_id, description, created_at, created_by)
    VALUES (NULL, 'TimeSlots', 'DELETE', NULL, N'Xóa slot thời gian cũ hơn 7 ngày', SYSDATETIME(), NULL);
END;
GO

/* =========================================================
   6) INSERT SAMPLE DATA - DOCTORS
   ========================================================= */

-- =========================================================
-- INSERT USERS CHO BÁC SĨ (Role ID = 2)
-- Password: 123456 (MD5: e10adc3949ba59abbe56e057f20f883e)
-- Phải insert Users trước để có user_id cho head_doctor_id
-- =========================================================

-- Bác sĩ 1: Bác sĩ Tim mạch
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'dr.nguyenvanan@hospital.com')
BEGIN
    INSERT INTO Users (role_id, full_name, id_card_number, phone_number, birth_date, gender, address, username, email, password, status, created_at, updated_at, created_by, updated_by)
    SELECT 2, N'Nguyễn Văn An', '001234567890', '0912345678', '1975-05-15', 'Male', N'123 Đường Lê Lợi, Quận 1, TP.HCM', 'dr.nguyenvanan', 'dr.nguyenvanan@hospital.com', 'e10adc3949ba59abbe56e057f20f883e', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 2: Bác sĩ Thần kinh
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'dr.tranthibinh@hospital.com')
BEGIN
    INSERT INTO Users (role_id, full_name, id_card_number, phone_number, birth_date, gender, address, username, email, password, status, created_at, updated_at, created_by, updated_by)
    SELECT 2, N'Trần Thị Bình', '001234567891', '0912345679', '1980-08-20', 'Female', N'456 Đường Nguyễn Huệ, Quận 1, TP.HCM', 'dr.tranthibinh', 'dr.tranthibinh@hospital.com', 'e10adc3949ba59abbe56e057f20f883e', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 3: Bác sĩ Nhi khoa
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'dr.levanchi@hospital.com')
BEGIN
    INSERT INTO Users (role_id, full_name, id_card_number, phone_number, birth_date, gender, address, username, email, password, status, created_at, updated_at, created_by, updated_by)
    SELECT 2, N'Lê Văn Chi', '001234567892', '0912345680', '1978-03-10', 'Male', N'789 Đường Võ Văn Tần, Quận 3, TP.HCM', 'dr.levanchi', 'dr.levanchi@hospital.com', 'e10adc3949ba59abbe56e057f20f883e', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 4: Bác sĩ Sản phụ khoa
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'dr.hoangthidung@hospital.com')
BEGIN
    INSERT INTO Users (role_id, full_name, id_card_number, phone_number, birth_date, gender, address, username, email, password, status, created_at, updated_at, created_by, updated_by)
    SELECT 2, N'Hoàng Thị Dung', '001234567893', '0912345681', '1982-11-25', 'Female', N'321 Đường Điện Biên Phủ, Quận Bình Thạnh, TP.HCM', 'dr.hoangthidung', 'dr.hoangthidung@hospital.com', 'e10adc3949ba59abbe56e057f20f883e', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 5: Bác sĩ Da liễu
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'dr.phamvanem@hospital.com')
BEGIN
    INSERT INTO Users (role_id, full_name, id_card_number, phone_number, birth_date, gender, address, username, email, password, status, created_at, updated_at, created_by, updated_by)
    SELECT 2, N'Phạm Văn Em', '001234567894', '0912345682', '1976-07-30', 'Male', N'654 Đường Cách Mạng Tháng 8, Quận 10, TP.HCM', 'dr.phamvanem', 'dr.phamvanem@hospital.com', 'e10adc3949ba59abbe56e057f20f883e', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 6: Bác sĩ Răng hàm mặt
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'dr.nguyenthifong@hospital.com')
BEGIN
    INSERT INTO Users (role_id, full_name, id_card_number, phone_number, birth_date, gender, address, username, email, password, status, created_at, updated_at, created_by, updated_by)
    SELECT 2, N'Nguyễn Thị Fong', '001234567895', '0912345683', '1985-02-14', 'Female', N'987 Đường Lý Thường Kiệt, Quận 10, TP.HCM', 'dr.nguyenthifong', 'dr.nguyenthifong@hospital.com', 'e10adc3949ba59abbe56e057f20f883e', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 7: Bác sĩ Mắt
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'dr.nguyenvanghi@hospital.com')
BEGIN
    INSERT INTO Users (role_id, full_name, id_card_number, phone_number, birth_date, gender, address, username, email, password, status, created_at, updated_at, created_by, updated_by)
    SELECT 2, N'Nguyễn Văn Ghi', '001234567896', '0912345684', '1979-09-05', 'Male', N'147 Đường Hai Bà Trưng, Quận 1, TP.HCM', 'dr.nguyenvanghi', 'dr.nguyenvanghi@hospital.com', 'e10adc3949ba59abbe56e057f20f883e', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 8: Bác sĩ Tiêu hóa
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'dr.tranvanhoa@hospital.com')
BEGIN
    INSERT INTO Users (role_id, full_name, id_card_number, phone_number, birth_date, gender, address, username, email, password, status, created_at, updated_at, created_by, updated_by)
    SELECT 2, N'Trần Văn Hoa', '001234567897', '0912345685', '1981-12-18', 'Male', N'258 Đường Nam Kỳ Khởi Nghĩa, Quận 3, TP.HCM', 'dr.tranvanhoa', 'dr.tranvanhoa@hospital.com', 'e10adc3949ba59abbe56e057f20f883e', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- =========================================================
-- INSERT SPECIALIZATIONS (Chuyên khoa)
-- Insert sau Users để có user_id cho head_doctor_id
-- =========================================================

IF NOT EXISTS (SELECT 1 FROM Specializations WHERE specialization_name = N'Tim mạch')
BEGIN
    INSERT INTO Specializations (specialization_name, description, head_doctor_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Tim mạch', 
        N'Chuyên khoa về bệnh tim và mạch máu',
        (SELECT id FROM Users WHERE email = 'dr.nguyenvanan@hospital.com'),
        'active', 
        SYSDATETIME(), 
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

IF NOT EXISTS (SELECT 1 FROM Specializations WHERE specialization_name = N'Thần kinh')
BEGIN
    INSERT INTO Specializations (specialization_name, description, head_doctor_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Thần kinh', 
        N'Chuyên khoa về bệnh thần kinh và não bộ',
        (SELECT id FROM Users WHERE email = 'dr.tranthibinh@hospital.com'),
        'active', 
        SYSDATETIME(), 
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

IF NOT EXISTS (SELECT 1 FROM Specializations WHERE specialization_name = N'Nhi khoa')
BEGIN
    INSERT INTO Specializations (specialization_name, description, head_doctor_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Nhi khoa', 
        N'Chuyên khoa điều trị bệnh trẻ em',
        (SELECT id FROM Users WHERE email = 'dr.levanchi@hospital.com'),
        'active', 
        SYSDATETIME(), 
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

IF NOT EXISTS (SELECT 1 FROM Specializations WHERE specialization_name = N'Sản phụ khoa')
BEGIN
    INSERT INTO Specializations (specialization_name, description, head_doctor_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Sản phụ khoa', 
        N'Chuyên khoa về sản khoa và phụ khoa',
        (SELECT id FROM Users WHERE email = 'dr.hoangthidung@hospital.com'),
        'active', 
        SYSDATETIME(), 
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

IF NOT EXISTS (SELECT 1 FROM Specializations WHERE specialization_name = N'Da liễu')
BEGIN
    INSERT INTO Specializations (specialization_name, description, head_doctor_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Da liễu', 
        N'Chuyên khoa về bệnh da và các vấn đề da liễu',
        (SELECT id FROM Users WHERE email = 'dr.phamvanem@hospital.com'),
        'active', 
        SYSDATETIME(), 
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

IF NOT EXISTS (SELECT 1 FROM Specializations WHERE specialization_name = N'Răng hàm mặt')
BEGIN
    INSERT INTO Specializations (specialization_name, description, head_doctor_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Răng hàm mặt', 
        N'Chuyên khoa về răng miệng và hàm mặt',
        (SELECT id FROM Users WHERE email = 'dr.nguyenthifong@hospital.com'),
        'active', 
        SYSDATETIME(), 
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

IF NOT EXISTS (SELECT 1 FROM Specializations WHERE specialization_name = N'Mắt')
BEGIN
    INSERT INTO Specializations (specialization_name, description, head_doctor_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Mắt', 
        N'Chuyên khoa về mắt và thị lực',
        (SELECT id FROM Users WHERE email = 'dr.nguyenvanghi@hospital.com'),
        'active', 
        SYSDATETIME(), 
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

IF NOT EXISTS (SELECT 1 FROM Specializations WHERE specialization_name = N'Tiêu hóa')
BEGIN
    INSERT INTO Specializations (specialization_name, description, head_doctor_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Tiêu hóa', 
        N'Chuyên khoa về hệ tiêu hóa và gan mật',
        (SELECT id FROM Users WHERE email = 'dr.tranvanhoa@hospital.com'),
        'active', 
        SYSDATETIME(), 
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- =========================================================
-- INSERT DOCTORS RECORDS
-- =========================================================

-- Bác sĩ 1: Nguyễn Văn An - Tim mạch
IF NOT EXISTS (SELECT 1 FROM Doctors WHERE user_id = (SELECT id FROM Users WHERE email = 'dr.nguyenvanan@hospital.com'))
BEGIN
    INSERT INTO Doctors (user_id, license_number, specialization_id, years_of_experience, certification, bio, status, is_verified, created_at, updated_at, created_by, updated_by)
    SELECT 
        (SELECT id FROM Users WHERE email = 'dr.nguyenvanan@hospital.com'),
        'BS-TM-001234',
        (SELECT id FROM Specializations WHERE specialization_name = N'Tim mạch'),
        20,
        N'Tiến sĩ Y khoa - Chuyên ngành Tim mạch, Đại học Y Dược TP.HCM',
        N'Bác sĩ với hơn 20 năm kinh nghiệm trong điều trị các bệnh về tim mạch. Chuyên gia về phẫu thuật tim mạch và can thiệp tim mạch.',
        'active',
        1,
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 2: Trần Thị Bình - Thần kinh
IF NOT EXISTS (SELECT 1 FROM Doctors WHERE user_id = (SELECT id FROM Users WHERE email = 'dr.tranthibinh@hospital.com'))
BEGIN
    INSERT INTO Doctors (user_id, license_number, specialization_id, years_of_experience, certification, bio, status, is_verified, created_at, updated_at, created_by, updated_by)
    SELECT 
        (SELECT id FROM Users WHERE email = 'dr.tranthibinh@hospital.com'),
        'BS-TK-001235',
        (SELECT id FROM Specializations WHERE specialization_name = N'Thần kinh'),
        15,
        N'Thạc sĩ Y khoa - Chuyên ngành Thần kinh, Đại học Y khoa Phạm Ngọc Thạch',
        N'Bác sĩ chuyên điều trị các bệnh về thần kinh, đau đầu, động kinh. Có kinh nghiệm trong chẩn đoán và điều trị bệnh Alzheimer.',
        'active',
        1,
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 3: Lê Văn Chi - Nhi khoa
IF NOT EXISTS (SELECT 1 FROM Doctors WHERE user_id = (SELECT id FROM Users WHERE email = 'dr.levanchi@hospital.com'))
BEGIN
    INSERT INTO Doctors (user_id, license_number, specialization_id, years_of_experience, certification, bio, status, is_verified, created_at, updated_at, created_by, updated_by)
    SELECT 
        (SELECT id FROM Users WHERE email = 'dr.levanchi@hospital.com'),
        'BS-NK-001236',
        (SELECT id FROM Specializations WHERE specialization_name = N'Nhi khoa'),
        18,
        N'Tiến sĩ Nhi khoa - Đại học Y Dược TP.HCM',
        N'Bác sĩ Nhi khoa với hơn 18 năm kinh nghiệm, chuyên điều trị các bệnh thường gặp ở trẻ em và trẻ sơ sinh.',
        'active',
        1,
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 4: Hoàng Thị Dung - Sản phụ khoa
IF NOT EXISTS (SELECT 1 FROM Doctors WHERE user_id = (SELECT id FROM Users WHERE email = 'dr.hoangthidung@hospital.com'))
BEGIN
    INSERT INTO Doctors (user_id, license_number, specialization_id, years_of_experience, certification, bio, status, is_verified, created_at, updated_at, created_by, updated_by)
    SELECT 
        (SELECT id FROM Users WHERE email = 'dr.hoangthidung@hospital.com'),
        'BS-SPK-001237',
        (SELECT id FROM Specializations WHERE specialization_name = N'Sản phụ khoa'),
        12,
        N'Thạc sĩ Sản phụ khoa - Đại học Y Dược Huế',
        N'Bác sĩ chuyên về sản phụ khoa, có kinh nghiệm trong siêu âm thai, khám thai định kỳ và đỡ đẻ.',
        'active',
        1,
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 5: Phạm Văn Em - Da liễu
IF NOT EXISTS (SELECT 1 FROM Doctors WHERE user_id = (SELECT id FROM Users WHERE email = 'dr.phamvanem@hospital.com'))
BEGIN
    INSERT INTO Doctors (user_id, license_number, specialization_id, years_of_experience, certification, bio, status, is_verified, created_at, updated_at, created_by, updated_by)
    SELECT 
        (SELECT id FROM Users WHERE email = 'dr.phamvanem@hospital.com'),
        'BS-DL-001238',
        (SELECT id FROM Specializations WHERE specialization_name = N'Da liễu'),
        16,
        N'Bác sĩ Chuyên khoa Da liễu - Đại học Y Dược TP.HCM',
        N'Chuyên điều trị các bệnh về da như mụn, viêm da, dị ứng da và các vấn đề về da khác.',
        'active',
        1,
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 6: Nguyễn Thị Fong - Răng hàm mặt
IF NOT EXISTS (SELECT 1 FROM Doctors WHERE user_id = (SELECT id FROM Users WHERE email = 'dr.nguyenthifong@hospital.com'))
BEGIN
    INSERT INTO Doctors (user_id, license_number, specialization_id, years_of_experience, certification, bio, status, is_verified, created_at, updated_at, created_by, updated_by)
    SELECT 
        (SELECT id FROM Users WHERE email = 'dr.nguyenthifong@hospital.com'),
        'BS-RHM-001239',
        (SELECT id FROM Specializations WHERE specialization_name = N'Răng hàm mặt'),
        10,
        N'Bác sĩ Răng hàm mặt - Đại học Y Dược TP.HCM',
        N'Chuyên về nha khoa tổng quát, chỉnh nha và phẫu thuật răng hàm mặt.',
        'active',
        1,
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 7: Nguyễn Văn Ghi - Mắt
IF NOT EXISTS (SELECT 1 FROM Doctors WHERE user_id = (SELECT id FROM Users WHERE email = 'dr.nguyenvanghi@hospital.com'))
BEGIN
    INSERT INTO Doctors (user_id, license_number, specialization_id, years_of_experience, certification, bio, status, is_verified, created_at, updated_at, created_by, updated_by)
    SELECT 
        (SELECT id FROM Users WHERE email = 'dr.nguyenvanghi@hospital.com'),
        'BS-MAT-001240',
        (SELECT id FROM Specializations WHERE specialization_name = N'Mắt'),
        14,
        N'Bác sĩ Chuyên khoa Mắt - Đại học Y Dược TP.HCM',
        N'Chuyên điều trị các bệnh về mắt, phẫu thuật đục thủy tinh thể và các vấn đề về thị lực.',
        'active',
        1,
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Bác sĩ 8: Trần Văn Hoa - Tiêu hóa
IF NOT EXISTS (SELECT 1 FROM Doctors WHERE user_id = (SELECT id FROM Users WHERE email = 'dr.tranvanhoa@hospital.com'))
BEGIN
    INSERT INTO Doctors (user_id, license_number, specialization_id, years_of_experience, certification, bio, status, is_verified, created_at, updated_at, created_by, updated_by)
    SELECT 
        (SELECT id FROM Users WHERE email = 'dr.tranvanhoa@hospital.com'),
        'BS-TH-001241',
        (SELECT id FROM Specializations WHERE specialization_name = N'Tiêu hóa'),
        17,
        N'Tiến sĩ Tiêu hóa - Đại học Y Dược TP.HCM',
        N'Chuyên gia về nội soi tiêu hóa, điều trị các bệnh về dạ dày, gan mật và đường ruột.',
        'active',
        1,
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

/* =========================================================
   7) INSERT SAMPLE DATA - AREAS, ROOMS, TIMESLOTS
   ========================================================= */

-- =========================================================
-- INSERT AREAS (Khu vực)
-- =========================================================

IF NOT EXISTS (SELECT 1 FROM Areas WHERE area_name = N'Khu vực A')
BEGIN
    INSERT INTO Areas (area_name, description, status, created_at, updated_at, created_by, updated_by)
    SELECT N'Khu vực A', N'Khu vực A - Tầng 1, khu vực khám tổng quát', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

IF NOT EXISTS (SELECT 1 FROM Areas WHERE area_name = N'Khu vực B')
BEGIN
    INSERT INTO Areas (area_name, description, status, created_at, updated_at, created_by, updated_by)
    SELECT N'Khu vực B', N'Khu vực B - Tầng 2, khu vực khám chuyên khoa', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

IF NOT EXISTS (SELECT 1 FROM Areas WHERE area_name = N'Khu vực C')
BEGIN
    INSERT INTO Areas (area_name, description, status, created_at, updated_at, created_by, updated_by)
    SELECT N'Khu vực C', N'Khu vực C - Tầng 3, khu vực khám đặc biệt', 'active', SYSDATETIME(), SYSDATETIME(), (SELECT TOP 1 id FROM Users WHERE role_id = 3), (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- =========================================================
-- INSERT ROOMS (Phòng khám)
-- =========================================================

-- Rooms cho Tim mạch
IF NOT EXISTS (SELECT 1 FROM Rooms WHERE room_name = N'Phòng Tim mạch 1' AND specialization_id = (SELECT id FROM Specializations WHERE specialization_name = N'Tim mạch'))
BEGIN
    INSERT INTO Rooms (room_name, specialization_id, area_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Phòng Tim mạch 1',
        (SELECT id FROM Specializations WHERE specialization_name = N'Tim mạch'),
        (SELECT id FROM Areas WHERE area_name = N'Khu vực B'),
        'available',
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Rooms cho Thần kinh
IF NOT EXISTS (SELECT 1 FROM Rooms WHERE room_name = N'Phòng Thần kinh 1' AND specialization_id = (SELECT id FROM Specializations WHERE specialization_name = N'Thần kinh'))
BEGIN
    INSERT INTO Rooms (room_name, specialization_id, area_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Phòng Thần kinh 1',
        (SELECT id FROM Specializations WHERE specialization_name = N'Thần kinh'),
        (SELECT id FROM Areas WHERE area_name = N'Khu vực B'),
        'available',
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Rooms cho Nhi khoa
IF NOT EXISTS (SELECT 1 FROM Rooms WHERE room_name = N'Phòng Nhi khoa 1' AND specialization_id = (SELECT id FROM Specializations WHERE specialization_name = N'Nhi khoa'))
BEGIN
    INSERT INTO Rooms (room_name, specialization_id, area_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Phòng Nhi khoa 1',
        (SELECT id FROM Specializations WHERE specialization_name = N'Nhi khoa'),
        (SELECT id FROM Areas WHERE area_name = N'Khu vực A'),
        'available',
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Rooms cho Sản phụ khoa
IF NOT EXISTS (SELECT 1 FROM Rooms WHERE room_name = N'Phòng Sản phụ khoa 1' AND specialization_id = (SELECT id FROM Specializations WHERE specialization_name = N'Sản phụ khoa'))
BEGIN
    INSERT INTO Rooms (room_name, specialization_id, area_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Phòng Sản phụ khoa 1',
        (SELECT id FROM Specializations WHERE specialization_name = N'Sản phụ khoa'),
        (SELECT id FROM Areas WHERE area_name = N'Khu vực B'),
        'available',
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Rooms cho Da liễu
IF NOT EXISTS (SELECT 1 FROM Rooms WHERE room_name = N'Phòng Da liễu 1' AND specialization_id = (SELECT id FROM Specializations WHERE specialization_name = N'Da liễu'))
BEGIN
    INSERT INTO Rooms (room_name, specialization_id, area_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Phòng Da liễu 1',
        (SELECT id FROM Specializations WHERE specialization_name = N'Da liễu'),
        (SELECT id FROM Areas WHERE area_name = N'Khu vực A'),
        'available',
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Rooms cho Răng hàm mặt
IF NOT EXISTS (SELECT 1 FROM Rooms WHERE room_name = N'Phòng Răng hàm mặt 1' AND specialization_id = (SELECT id FROM Specializations WHERE specialization_name = N'Răng hàm mặt'))
BEGIN
    INSERT INTO Rooms (room_name, specialization_id, area_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Phòng Răng hàm mặt 1',
        (SELECT id FROM Specializations WHERE specialization_name = N'Răng hàm mặt'),
        (SELECT id FROM Areas WHERE area_name = N'Khu vực C'),
        'available',
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Rooms cho Mắt
IF NOT EXISTS (SELECT 1 FROM Rooms WHERE room_name = N'Phòng Mắt 1' AND specialization_id = (SELECT id FROM Specializations WHERE specialization_name = N'Mắt'))
BEGIN
    INSERT INTO Rooms (room_name, specialization_id, area_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Phòng Mắt 1',
        (SELECT id FROM Specializations WHERE specialization_name = N'Mắt'),
        (SELECT id FROM Areas WHERE area_name = N'Khu vực B'),
        'available',
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

-- Rooms cho Tiêu hóa
IF NOT EXISTS (SELECT 1 FROM Rooms WHERE room_name = N'Phòng Tiêu hóa 1' AND specialization_id = (SELECT id FROM Specializations WHERE specialization_name = N'Tiêu hóa'))
BEGIN
    INSERT INTO Rooms (room_name, specialization_id, area_id, status, created_at, updated_at, created_by, updated_by)
    SELECT 
        N'Phòng Tiêu hóa 1',
        (SELECT id FROM Specializations WHERE specialization_name = N'Tiêu hóa'),
        (SELECT id FROM Areas WHERE area_name = N'Khu vực B'),
        'available',
        SYSDATETIME(),
        SYSDATETIME(),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3),
        (SELECT TOP 1 id FROM Users WHERE role_id = 3);
END
GO

/* ========================= DONE ========================= */
