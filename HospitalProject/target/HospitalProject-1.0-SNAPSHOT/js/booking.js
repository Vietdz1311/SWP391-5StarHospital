// Declare necessary variables and functions
const HOSPITAL_DATA = {
  specialties: [],
  timeSlots: [],
  rooms: [],
}

function createHeader(title) {
  return `<header><h1>${title}</h1></header>`
}

function createFooter() {
  return `<footer><p>&copy; 2023 Hospital Booking System</p></footer>`
}

function getUrlParameter(name) {
  name = name.replace(/[[\]]/g, "\\$&")
  const regex = new RegExp(`[?&]${name}(=([^&#]*)|&|#|$)`)
  const results = regex.exec(window.location.href)
  if (!results) return null
  if (!results[2]) return ""
  return decodeURIComponent(results[2].replace(/\+/g, " "))
}

function getDoctorById(id) {
  // Placeholder function to get doctor by ID
  return HOSPITAL_DATA.doctors.find((doctor) => doctor.id === id)
}

function getDoctorsBySpecialty(specialtyId) {
  // Placeholder function to get doctors by specialty ID
  return HOSPITAL_DATA.doctors.filter((doctor) => doctor.specialty === specialtyId)
}

function saveBooking(booking) {
  // Placeholder function to save booking
  console.log("Booking saved:", booking)
}

function saveToSession(key, value) {
  // Placeholder function to save value to session
  sessionStorage.setItem(key, JSON.stringify(value))
}

function getSpecialtyById(id) {
  // Placeholder function to get specialty by ID
  return HOSPITAL_DATA.specialties.find((specialty) => specialty.id === id)
}

function generateBookingCode() {
  // Placeholder function to generate booking code
  return Math.random().toString(36).substr(2, 9)
}

// Render header và footer
document.getElementById("header").innerHTML = createHeader("booking")
document.getElementById("footer").innerHTML = createFooter()

// Lấy thông tin bác sĩ từ URL hoặc session
const doctorParam = getUrlParameter("doctor")
let selectedDoctor = null

if (doctorParam) {
  selectedDoctor = getDoctorById(doctorParam)
}

// Populate form dropdowns
function populateForm() {
  // Populate specialties
  const specialtySelect = document.getElementById("specialty")
  HOSPITAL_DATA.specialties.forEach((specialty) => {
    const option = document.createElement("option")
    option.value = specialty.id
    option.textContent = specialty.name
    specialtySelect.appendChild(option)
  })

  // Populate time slots
  const timeSlotSelect = document.getElementById("timeSlot")
  HOSPITAL_DATA.timeSlots.forEach((slot) => {
    const option = document.createElement("option")
    option.value = slot
    option.textContent = slot
    timeSlotSelect.appendChild(option)
  })

  // Populate rooms
  const roomSelect = document.getElementById("room")
  HOSPITAL_DATA.rooms.forEach((room) => {
    const option = document.createElement("option")
    option.value = room
    option.textContent = room
    roomSelect.appendChild(option)
  })

  // Pre-select doctor if available
  if (selectedDoctor) {
    specialtySelect.value = selectedDoctor.specialty
    updateDoctorDropdown()
    setTimeout(() => {
      document.getElementById("doctor").value = selectedDoctor.id
    }, 100)
  }
}

// Update doctor dropdown based on specialty
function updateDoctorDropdown() {
  const specialtyId = document.getElementById("specialty").value
  const doctorSelect = document.getElementById("doctor")

  doctorSelect.innerHTML = '<option value="">-- Chọn bác sĩ --</option>'

  if (specialtyId) {
    const doctors = getDoctorsBySpecialty(specialtyId)
    doctors.forEach((doctor) => {
      const option = document.createElement("option")
      option.value = doctor.id
      option.textContent = `${doctor.name} - ${doctor.title}`
      doctorSelect.appendChild(option)
    })
    doctorSelect.disabled = false
  } else {
    doctorSelect.disabled = true
  }
}

// Set minimum date (tomorrow)
function setMinDate() {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  const minDate = tomorrow.toISOString().split("T")[0]
  document.getElementById("appointmentDate").setAttribute("min", minDate)
}

// Validate form
function validateForm() {
  let isValid = true

  // Validate full name
  const fullName = document.getElementById("fullName")
  if (!fullName.value.trim()) {
    showError("fullName", "Vui lòng nhập họ và tên")
    isValid = false
  } else {
    hideError("fullName")
  }

  // Validate phone
  const phone = document.getElementById("phone")
  const phoneRegex = /^[0-9]{10,11}$/
  if (!phone.value.trim() || !phoneRegex.test(phone.value.trim())) {
    showError("phone", "Vui lòng nhập số điện thoại hợp lệ (10-11 số)")
    isValid = false
  } else {
    hideError("phone")
  }

  // Validate email (if provided)
  const email = document.getElementById("email")
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (email.value.trim() && !emailRegex.test(email.value.trim())) {
    showError("email", "Email không hợp lệ")
    isValid = false
  } else {
    hideError("email")
  }

  // Validate date
  const appointmentDate = document.getElementById("appointmentDate")
  if (!appointmentDate.value) {
    showError("appointmentDate", "Vui lòng chọn ngày khám")
    isValid = false
  } else {
    hideError("appointmentDate")
  }

  // Validate specialty
  const specialty = document.getElementById("specialty")
  if (!specialty.value) {
    showError("specialty", "Vui lòng chọn chuyên khoa")
    isValid = false
  } else {
    hideError("specialty")
  }

  // Validate doctor
  const doctor = document.getElementById("doctor")
  if (!doctor.value) {
    showError("doctor", "Vui lòng chọn bác sĩ")
    isValid = false
  } else {
    hideError("doctor")
  }

  // Validate time slot
  const timeSlot = document.getElementById("timeSlot")
  if (!timeSlot.value) {
    showError("timeSlot", "Vui lòng chọn khung giờ")
    isValid = false
  } else {
    hideError("timeSlot")
  }

  // Validate room
  const room = document.getElementById("room")
  if (!room.value) {
    showError("room", "Vui lòng chọn phòng khám")
    isValid = false
  } else {
    hideError("room")
  }

  return isValid
}

// Show error
function showError(fieldId, message) {
  const field = document.getElementById(fieldId)
  const errorElement = document.getElementById(fieldId + "Error")

  field.classList.add("error")
  errorElement.textContent = message
  errorElement.classList.add("show")
}

// Hide error
function hideError(fieldId) {
  const field = document.getElementById(fieldId)
  const errorElement = document.getElementById(fieldId + "Error")

  field.classList.remove("error")
  errorElement.classList.remove("show")
}

// Handle form submission
document.getElementById("bookingForm").addEventListener("submit", (e) => {
  e.preventDefault()

  if (validateForm()) {
    const booking = createBookingData()
    saveBooking(booking)
    saveToSession("currentBooking", booking)
    window.location.href = "confirm.html"
  }
})

// Create booking data
function createBookingData() {
  const specialtyId = document.getElementById("specialty").value
  const specialtyName = getSpecialtyById(specialtyId).name

  const doctorId = Number.parseInt(document.getElementById("doctor").value)
  const doctor = getDoctorById(doctorId)

  return {
    code: generateBookingCode(),
    fullName: document.getElementById("fullName").value.trim(),
    phone: document.getElementById("phone").value.trim(),
    email: document.getElementById("email").value.trim(),
    date: document.getElementById("appointmentDate").value,
    specialty: specialtyName,
    doctor: doctor.name,
    doctorTitle: doctor.title,
    timeSlot: document.getElementById("timeSlot").value,
    room: document.getElementById("room").value,
    notes: document.getElementById("notes").value.trim(),
    createdAt: new Date().toISOString(),
  }
}

// Event listeners
document.getElementById("specialty").addEventListener("change", updateDoctorDropdown)

// Initialize
populateForm()
setMinDate()
