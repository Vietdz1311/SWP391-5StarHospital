// Dữ liệu mẫu - Có thể chỉnh sửa dễ dàng
const HOSPITAL_DATA = {
  hospital: {
    name: "Bệnh viện Đa khoa Tâm Đức",
    address: "123 Đường Nguyễn Văn Linh, Quận 7, TP.HCM",
    phone: "1900 1234",
    email: "info@bvtamduc.vn",
    hotline: "0909 123 456",
  },

  specialties: [
    {
      id: "tim-mach",
      name: "Tim mạch",
      icon: "❤️",
      description: "Chẩn đoán và điều trị các bệnh về tim mạch, huyết áp, nhồi máu cơ tim",
    },
    {
      id: "noi-khoa",
      name: "Nội tổng hợp",
      icon: "🩺",
      description: "Khám và điều trị các bệnh nội khoa tổng quát, tiểu đường, gan mật",
    },
    {
      id: "ngoai-khoa",
      name: "Ngoại khoa",
      icon: "🔬",
      description: "Phẫu thuật và điều trị các bệnh ngoại khoa, chấn thương",
    },
    {
      id: "san-phu-khoa",
      name: "Sản phụ khoa",
      icon: "👶",
      description: "Chăm sóc sức khỏe phụ nữ, thai sản, sinh đẻ",
    },
    {
      id: "nhi-khoa",
      name: "Nhi khoa",
      icon: "🧸",
      description: "Khám và điều trị bệnh cho trẻ em từ sơ sinh đến 16 tuổi",
    },
    {
      id: "mat",
      name: "Mắt",
      icon: "👁️",
      description: "Khám và điều trị các bệnh về mắt, phẫu thuật mắt",
    },
    {
      id: "tai-mui-hong",
      name: "Tai mũi họng",
      icon: "👂",
      description: "Điều trị các bệnh về tai, mũi, họng",
    },
    {
      id: "da-lieu",
      name: "Da liễu",
      icon: "🧴",
      description: "Điều trị các bệnh về da, thẩm mỹ da",
    },
  ],

  doctors: [
    {
      id: 1,
      name: "BS. Nguyễn Văn An",
      specialty: "tim-mach",
      specialtyName: "Tim mạch",
      title: "Bác sĩ chuyên khoa II",
      experience: "15 năm kinh nghiệm",
      description: "Chuyên điều trị bệnh tim mạch, tăng huyết áp",
      hours: "Thứ 2-6: 8:00-17:00",
      avatar: "/male-doctor-cardiology.jpg",
    },
    {
      id: 2,
      name: "BS. Trần Thị Bình",
      specialty: "noi-khoa",
      specialtyName: "Nội tổng hợp",
      title: "Thạc sĩ, Bác sĩ",
      experience: "12 năm kinh nghiệm",
      description: "Chuyên điều trị bệnh tiểu đường, gan mật",
      hours: "Thứ 2-7: 8:00-17:00",
      avatar: "/female-doctor-internal-medicine.jpg",
    },
    {
      id: 3,
      name: "BS. Lê Minh Cường",
      specialty: "ngoai-khoa",
      specialtyName: "Ngoại khoa",
      title: "Bác sĩ chuyên khoa I",
      experience: "10 năm kinh nghiệm",
      description: "Chuyên phẫu thuật ngoại khoa tổng quát",
      hours: "Thứ 2-6: 8:00-16:00",
      avatar: "/male-doctor-surgery.jpg",
    },
    {
      id: 4,
      name: "BS. Phạm Thu Dung",
      specialty: "san-phu-khoa",
      specialtyName: "Sản phụ khoa",
      title: "Thạc sĩ, Bác sĩ",
      experience: "18 năm kinh nghiệm",
      description: "Chuyên sản khoa, chăm sóc thai sản",
      hours: "Thứ 2-7: 8:00-17:00",
      avatar: "/female-doctor-obstetrics.jpg",
    },
    {
      id: 5,
      name: "BS. Hoàng Văn Em",
      specialty: "nhi-khoa",
      specialtyName: "Nhi khoa",
      title: "Bác sĩ chuyên khoa II",
      experience: "14 năm kinh nghiệm",
      description: "Chuyên điều trị bệnh nhi khoa",
      hours: "Thứ 2-6: 8:00-17:00",
      avatar: "/male-doctor-pediatrics.jpg",
    },
    {
      id: 6,
      name: "BS. Đỗ Thị Phương",
      specialty: "mat",
      specialtyName: "Mắt",
      title: "Bác sĩ chuyên khoa I",
      experience: "11 năm kinh nghiệm",
      description: "Chuyên điều trị bệnh về mắt",
      hours: "Thứ 3-7: 8:00-16:00",
      avatar: "/female-doctor-ophthalmology.jpg",
    },
    {
      id: 7,
      name: "BS. Vũ Quang Hải",
      specialty: "tim-mach",
      specialtyName: "Tim mạch",
      title: "Tiến sĩ, Bác sĩ",
      experience: "20 năm kinh nghiệm",
      description: "Chuyên tim mạch can thiệp",
      hours: "Thứ 2-6: 9:00-18:00",
      avatar: "/senior-male-doctor-cardiology.jpg",
    },
    {
      id: 8,
      name: "BS. Ngô Thị Lan",
      specialty: "noi-khoa",
      specialtyName: "Nội tổng hợp",
      title: "Bác sĩ chuyên khoa I",
      experience: "9 năm kinh nghiệm",
      description: "Chuyên nội tiết, chuyển hóa",
      hours: "Thứ 2-6: 8:00-17:00",
      avatar: "/female-doctor-endocrinology.jpg",
    },
    {
      id: 9,
      name: "BS. Bùi Văn Khoa",
      specialty: "tai-mui-hong",
      specialtyName: "Tai mũi họng",
      title: "Bác sĩ chuyên khoa II",
      experience: "13 năm kinh nghiệm",
      description: "Chuyên điều trị bệnh tai mũi họng",
      hours: "Thứ 2-6: 8:00-17:00",
      avatar: "/male-doctor-ent.jpg",
    },
    {
      id: 10,
      name: "BS. Lý Thị Mai",
      specialty: "da-lieu",
      specialtyName: "Da liễu",
      title: "Thạc sĩ, Bác sĩ",
      experience: "10 năm kinh nghiệm",
      description: "Chuyên điều trị bệnh da và thẩm mỹ da",
      hours: "Thứ 3-7: 8:00-17:00",
      avatar: "/female-doctor-dermatology.jpg",
    },
  ],

  rooms: ["Phòng khám 101", "Phòng khám 102", "Phòng khám 103", "Phòng khám 201", "Phòng khám 202", "Phòng khám 203"],

  timeSlots: [
    "08:00 - 09:00",
    "09:00 - 10:00",
    "10:00 - 11:00",
    "11:00 - 12:00",
    "14:00 - 15:00",
    "15:00 - 16:00",
    "16:00 - 17:00",
  ],
}

// Hàm tiện ích
function getSpecialtyById(id) {
  return HOSPITAL_DATA.specialties.find((s) => s.id === id)
}

function getDoctorById(id) {
  return HOSPITAL_DATA.doctors.find((d) => d.id === Number.parseInt(id))
}

function getDoctorsBySpecialty(specialtyId) {
  return HOSPITAL_DATA.doctors.filter((d) => d.specialty === specialtyId)
}

function formatDate(dateStr) {
  const date = new Date(dateStr)
  const day = date.getDate().toString().padStart(2, "0")
  const month = (date.getMonth() + 1).toString().padStart(2, "0")
  const year = date.getFullYear()
  return `${day}/${month}/${year}`
}

function generateBookingCode() {
  const date = new Date()
  const dateStr = date.toISOString().split("T")[0].replace(/-/g, "")
  const random = Math.random().toString(36).substring(2, 8).toUpperCase()
  return `BK-${dateStr}-${random}`
}
