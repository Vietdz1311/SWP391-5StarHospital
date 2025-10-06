// Declare necessary variables and functions
function createHeader(title) {
  return `<header><h1>${title}</h1></header>`
}

function createFooter() {
  return `<footer><p>© 2023 All rights reserved</p></footer>`
}

function getAllBookings() {
  // Dummy data for demonstration purposes
  return [
    {
      code: "A001",
      fullName: "John Doe",
      phone: "1234567890",
      specialty: "Cardiology",
      doctor: "Dr. Smith",
      date: "2023-10-01",
      timeSlot: "10:00 AM",
      room: "101",
      notes: "None",
    },
    {
      code: "A002",
      fullName: "Jane Doe",
      phone: "0987654321",
      specialty: "Dermatology",
      doctor: "Dr. Johnson",
      date: "2023-10-02",
      timeSlot: "2:00 PM",
      room: "102",
      notes: "None",
    },
  ]
}

function formatDate(dateString) {
  const date = new Date(dateString)
  return `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`
}

function deleteBooking(code) {
  // Dummy function for demonstration purposes
  console.log(`Booking with code ${code} has been canceled.`)
}

// Render header và footer
document.getElementById("header").innerHTML = createHeader("appointments")
document.getElementById("footer").innerHTML = createFooter()

// Render appointments
function renderAppointments() {
  const container = document.getElementById("appointmentsList")
  const bookings = getAllBookings()

  if (bookings.length === 0) {
    container.innerHTML = `
      <div class="empty-appointments">
        <div class="empty-icon">📅</div>
        <h2>Chưa có lịch khám nào</h2>
        <p>Bạn chưa đặt lịch khám nào. Hãy đặt lịch ngay để được chăm sóc sức khỏe tốt nhất!</p>
        <a href="booking.html" class="btn">Đặt lịch khám</a>
      </div>
    `
    return
  }

  // Sort by date (newest first)
  bookings.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

  container.innerHTML = `
    <div class="appointments-grid">
      ${bookings
        .map(
          (booking) => `
        <div class="appointment-card">
          <div class="appointment-header">
            <div class="appointment-code">${booking.code}</div>
            <div class="appointment-date-badge">
              📅 ${formatDate(booking.date)}
            </div>
          </div>
          
          <div class="appointment-body">
            <div class="appointment-info">
              <span class="info-icon">👤</span>
              <span class="info-label">Bệnh nhân:</span>
              <span class="info-value">${booking.fullName}</span>
            </div>
            
            <div class="appointment-info">
              <span class="info-icon">📞</span>
              <span class="info-label">Số điện thoại:</span>
              <span class="info-value">${booking.phone}</span>
            </div>
            
            <div class="appointment-info">
              <span class="info-icon">🏥</span>
              <span class="info-label">Chuyên khoa:</span>
              <span class="info-value">${booking.specialty}</span>
            </div>
            
            <div class="appointment-info">
              <span class="info-icon">👨‍⚕️</span>
              <span class="info-label">Bác sĩ:</span>
              <span class="info-value">${booking.doctor}</span>
            </div>
            
            <div class="appointment-info">
              <span class="info-icon">🕐</span>
              <span class="info-label">Khung giờ:</span>
              <span class="info-value">${booking.timeSlot}</span>
            </div>
            
            <div class="appointment-info">
              <span class="info-icon">🚪</span>
              <span class="info-label">Phòng khám:</span>
              <span class="info-value">${booking.room}</span>
            </div>
            
            ${
              booking.notes
                ? `
              <div class="appointment-info">
                <span class="info-icon">📝</span>
                <span class="info-label">Ghi chú:</span>
                <span class="info-value">${booking.notes}</span>
              </div>
            `
                : ""
            }
          </div>
          
          <div class="appointment-footer">
            <button class="btn-view" onclick="viewDetails('${booking.code}')">
              Xem chi tiết
            </button>
            <button class="btn-cancel" onclick="cancelAppointment('${booking.code}')">
              Hủy lịch
            </button>
          </div>
        </div>
      `,
        )
        .join("")}
    </div>
  `
}

// View appointment details
function viewDetails(code) {
  const bookings = getAllBookings()
  const booking = bookings.find((b) => b.code === code)

  if (booking) {
    alert(
      `Chi tiết lịch khám:\n\nMã: ${booking.code}\nBệnh nhân: ${booking.fullName}\nNgày: ${formatDate(booking.date)}\nGiờ: ${booking.timeSlot}\nBác sĩ: ${booking.doctor}\nPhòng: ${booking.room}`,
    )
  }
}

// Cancel appointment
function cancelAppointment(code) {
  if (confirm("Bạn có chắc chắn muốn hủy lịch khám này?")) {
    deleteBooking(code)
    renderAppointments()
  }
}

// Initialize
renderAppointments()
