// Declare necessary variables and functions
const HOSPITAL_DATA = {
  specialties: [
    { id: "1", name: "Cardiology", description: "Heart and blood vessel diseases", icon: "❤️" },
    { id: "2", name: "Neurology", description: "Nervous system diseases", icon: "🧠" },
    // Add more specialties as needed
  ],
}

function createHeader(title) {
  return `<header><h1>${title}</h1></header>`
}

function createFooter() {
  return `<footer><p>&copy; 2023 Hospital</p></footer>`
}

function getDoctorsBySpecialty(specialtyId) {
  // Dummy implementation for demonstration purposes
  return [
    { id: "101", name: "Dr. Smith", specialtyId: "1" },
    { id: "102", name: "Dr. Johnson", specialtyId: "1" },
    { id: "103", name: "Dr. Brown", specialtyId: "2" },
    // Add more doctors as needed
  ].filter((doctor) => doctor.specialtyId === specialtyId)
}

// Render header và footer
document.getElementById("header").innerHTML = createHeader("departments")
document.getElementById("footer").innerHTML = createFooter()

// Render tất cả chuyên khoa
function renderDepartments() {
  const grid = document.getElementById("departmentsGrid")

  grid.innerHTML = HOSPITAL_DATA.specialties
    .map((specialty) => {
      const doctorCount = getDoctorsBySpecialty(specialty.id).length

      return `
      <div class="card department-card">
        <div class="card-icon">${specialty.icon}</div>
        <h3>${specialty.name}</h3>
        <p>${specialty.description}</p>
        <div class="department-info">
          <span class="doctor-count">👨‍⚕️ ${doctorCount} bác sĩ</span>
        </div>
        <button class="btn btn-outline view-doctors-btn" onclick="viewDoctors('${specialty.id}')">
          Xem bác sĩ
        </button>
      </div>
    `
    })
    .join("")
}

// Chuyển đến trang bác sĩ theo chuyên khoa
function viewDoctors(specialtyId) {
  window.location.href = `doctors.html?dept=${specialtyId}`
}

// Khởi tạo
renderDepartments()
