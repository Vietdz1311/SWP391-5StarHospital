document.addEventListener("DOMContentLoaded", () => {
  const menuToggle = document.getElementById("menuToggle")
  const mainNav = document.getElementById("mainNav")

  if (menuToggle && mainNav) {
    menuToggle.addEventListener("click", () => {
      mainNav.classList.toggle("active")

      // Toggle icon
      const icon = menuToggle.querySelector("i")
      if (icon) {
        if (mainNav.classList.contains("active")) {
          icon.className = "bx bx-x"
        } else {
          icon.className = "bx bx-menu"
        }
      }
    })

    // Close menu when clicking outside
    document.addEventListener("click", (e) => {
      if (!mainNav.contains(e.target) && !menuToggle.contains(e.target)) {
        mainNav.classList.remove("active")
        const icon = menuToggle.querySelector("i")
        if (icon) {
          icon.className = "bx bx-menu"
        }
      }
    })

    // Close menu when clicking on a link
    const navLinks = mainNav.querySelectorAll("a")
    navLinks.forEach((link) => {
      link.addEventListener("click", () => {
        mainNav.classList.remove("active")
        const icon = menuToggle.querySelector("i")
        if (icon) {
          icon.className = "bx bx-menu"
        }
      })
    })
  }
})

// Utility functions for localStorage
function saveBooking(booking) {
  const bookings = JSON.parse(localStorage.getItem("bookings") || "[]")
  bookings.push(booking)
  localStorage.setItem("bookings", JSON.stringify(bookings))
}

function getAllBookings() {
  return JSON.parse(localStorage.getItem("bookings") || "[]")
}

function deleteBooking(code) {
  let bookings = getAllBookings()
  bookings = bookings.filter((b) => b.code !== code)
  localStorage.setItem("bookings", JSON.stringify(bookings))
}

function getBookingByCode(code) {
  const bookings = getAllBookings()
  return bookings.find((b) => b.code === code)
}

// Generate booking code
function generateBookingCode() {
  const date = new Date()
  const dateStr = date.toISOString().slice(0, 10).replace(/-/g, "")
  const random = Math.random().toString(36).substring(2, 8).toUpperCase()
  return `BK-${dateStr}-${random}`
}

// Format date
function formatDate(dateString) {
  const date = new Date(dateString)
  const day = String(date.getDate()).padStart(2, "0")
  const month = String(date.getMonth() + 1).padStart(2, "0")
  const year = date.getFullYear()
  return `${day}/${month}/${year}`
}

// Smooth scroll to top
function scrollToTop() {
  window.scrollTo({
    top: 0,
    behavior: "smooth",
  })
}
