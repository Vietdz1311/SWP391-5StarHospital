document.addEventListener("DOMContentLoaded", () => {
  const specialtyFilter = document.getElementById("specialtyFilter")
  const doctorCards = document.querySelectorAll(".doctor-card")
  const resultsCount = document.getElementById("resultsCount")

  // Check URL parameters for specialty filter
  const urlParams = new URLSearchParams(window.location.search)
  const deptParam = urlParams.get("dept")

  if (deptParam) {
    specialtyFilter.value = deptParam
    filterDoctors()
  }

  // Filter doctors by specialty
  specialtyFilter.addEventListener("change", filterDoctors)

  function filterDoctors() {
    const selectedSpecialty = specialtyFilter.value
    let visibleCount = 0

    doctorCards.forEach((card) => {
      const cardSpecialty = card.getAttribute("data-specialty")

      if (selectedSpecialty === "" || cardSpecialty === selectedSpecialty) {
        card.classList.remove("hidden")
        visibleCount++
      } else {
        card.classList.add("hidden")
      }
    })

    // Update results count
    if (selectedSpecialty === "") {
      resultsCount.textContent = `Hiển thị tất cả ${visibleCount} bác sĩ`
    } else {
      const specialtyName = specialtyFilter.options[specialtyFilter.selectedIndex].text
      resultsCount.textContent = `Hiển thị ${visibleCount} bác sĩ chuyên khoa ${specialtyName}`
    }
  }

  // Initial count
  resultsCount.textContent = `Hiển thị tất cả ${doctorCards.length} bác sĩ`
})
