// Import necessary functions
function createHeader() {
  return "<header>Header Content</header>"
}

function createFooter() {
  return "<footer>Footer Content</footer>"
}

function getFromSession(key) {
  return JSON.parse(sessionStorage.getItem(key))
}

function formatDate(date) {
  const options = { year: "numeric", month: "long", day: "numeric" }
  return new Date(date).toLocaleDateString("vi-VN", options)
}

// Render header và footer
document.getElementById("header").innerHTML = createHeader()
document.getElementById("footer").innerHTML = createFooter()

// Lấy thông tin booking từ session
const booking = getFromSession("currentBooking")

if (!booking) {
  // Nếu không có booking, chuyển về trang chủ
  window.location.href = "index.html"
} else {
  displayBookingConfirmation(booking)
}

function displayBookingConfirmation(booking) {
  // Display booking code
  document.getElementById("bookingCode").textContent = booking.code

  // Display booking details
  const detailsContainer = document.getElementById("bookingDetails")
  detailsContainer.innerHTML = `
    <div class="detail-row">
      <div class="detail-label">Họ và tên:</div>
      <div class="detail-value">${booking.fullName}</div>
    </div>
    <div class="detail-row">
      <div class="detail-label">Số điện thoại:</div>
      <div class="detail-value">${booking.phone}</div>
    </div>
    ${
      booking.email
        ? `
      <div class="detail-row">
        <div class="detail-label">Email:</div>
        <div class="detail-value">${booking.email}</div>
      </div>
    `
        : ""
    }
    <div class="detail-row">
      <div class="detail-label">Ngày khám:</div>
      <div class="detail-value">${formatDate(booking.date)}</div>
    </div>
    <div class="detail-row">
      <div class="detail-label">Khung giờ:</div>
      <div class="detail-value">${booking.timeSlot}</div>
    </div>
    <div class="detail-row">
      <div class="detail-label">Chuyên khoa:</div>
      <div class="detail-value">${booking.specialty}</div>
    </div>
    <div class="detail-row">
      <div class="detail-label">Bác sĩ:</div>
      <div class="detail-value">${booking.doctor} - ${booking.doctorTitle}</div>
    </div>
    <div class="detail-row">
      <div class="detail-label">Phòng khám:</div>
      <div class="detail-value">${booking.room}</div>
    </div>
    ${
      booking.notes
        ? `
      <div class="detail-row">
        <div class="detail-label">Ghi chú:</div>
        <div class="detail-value">${booking.notes}</div>
      </div>
    `
        : ""
    }
  `

  // Clear session after displaying
  sessionStorage.removeItem("currentBooking")
}
