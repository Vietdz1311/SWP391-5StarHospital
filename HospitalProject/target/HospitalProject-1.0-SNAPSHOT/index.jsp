<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="web-page/components/header.jsp" />

<!-- Hero Section -->
<section class="bg-gradient-to-r from-blue-500 to-teal-500 text-white py-20">
  <div class="container mx-auto px-4">
    <div class="flex flex-col items-center text-center">
      <div class="inline-flex items-center bg-white text-blue-600 px-4 py-2 rounded-full mb-6">
        <i class='bx bxs-award text-2xl mr-2'></i>
        <span>Hơn 20 năm kinh nghiệm</span>
      </div>
      <h1 class="text-4xl md:text-5xl font-bold mb-4">Chăm sóc sức khỏe toàn diện cho gia đình bạn</h1>
      <p class="text-lg md:text-xl mb-8">Đội ngũ bác sĩ chuyên môn cao, trang thiết bị hiện đại, dịch vụ tận tâm</p>
      <div class="flex flex-col sm:flex-row gap-4">
        <a href="booking" class="bg-white text-blue-600 px-6 py-3 rounded-full font-semibold hover:bg-blue-100 transition duration-300">
          <i class='bx bx-calendar-plus mr-2'></i>Đặt lịch ngay
        </a>
        <a href="specializations" class="border-2 border-white text-white px-6 py-3 rounded-full font-semibold hover:bg-white hover:text-blue-600 transition duration-300">
          <i class='bx bx-search-alt mr-2'></i>Tìm chuyên khoa
        </a>
      </div>
      <div class="mt-10 grid grid-cols-2 md:grid-cols-4 gap-6">
        <div class="flex flex-col items-center">
          <i class='bx bxs-user-check text-3xl mb-2'></i>
          <div class="text-2xl font-bold">100K+</div>
          <div>Bệnh nhân</div>
        </div>
        <div class="flex flex-col items-center">
          <i class='bx bxs-user-badge text-3xl mb-2'></i>
          <div class="text-2xl font-bold">50+</div>
          <div>Bác sĩ</div>
        </div>
        <div class="flex flex-col items-center">
          <i class='bx bxs-clinic text-3xl mb-2'></i>
          <div class="text-2xl font-bold">8</div>
          <div>Chuyên khoa</div>
        </div>
        <div class="flex flex-col items-center">
          <i class='bx bxs-star text-3xl mb-2'></i>
          <div class="text-2xl font-bold">4.9/5</div>
          <div>Đánh giá</div>
        </div>
      </div>
    </div>
  </div>
</section>

<!-- Features Section -->
<section class="py-16 bg-gray-100">
  <div class="container mx-auto px-4">
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition duration-300">
        <i class='bx bxs-time-five text-4xl text-blue-600 mb-4'></i>
        <h3 class="text-xl font-semibold mb-2">Đặt lịch nhanh chóng</h3>
        <p>Đặt lịch khám online 24/7, tiết kiệm thời gian chờ đợi</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition duration-300">
        <i class='bx bxs-user-detail text-4xl text-blue-600 mb-4'></i>
        <h3 class="text-xl font-semibold mb-2">Bác sĩ chuyên môn cao</h3>
        <p>Đội ngũ bác sĩ giàu kinh nghiệm, tận tâm với bệnh nhân</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition duration-300">
        <i class='bx bxs-devices text-4xl text-blue-600 mb-4'></i>
        <h3 class="text-xl font-semibold mb-2">Trang thiết bị hiện đại</h3>
        <p>Công nghệ y tế tiên tiến, chẩn đoán chính xác</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition duration-300">
        <i class='bx bxs-heart text-4xl text-blue-600 mb-4'></i>
        <h3 class="text-xl font-semibold mb-2">Chăm sóc tận tình</h3>
        <p>Dịch vụ chu đáo, quan tâm đến từng bệnh nhân</p>
      </div>
    </div>
  </div>
</section>

<!-- Specialties Section -->
<section class="py-16">
  <div class="container mx-auto px-4">
    <div class="text-center mb-10">
      <h2 class="text-3xl font-bold">Chuyên khoa nổi bật</h2>
      <p class="text-gray-600">Các chuyên khoa với đội ngũ bác sĩ giàu kinh nghiệm</p>
    </div>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <c:forEach var="spec" items="${featuredSpecializations}">
        <div class="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition duration-300">
          <div class="flex items-center mb-4">
            <i class='bx ${spec.specializationName == "Tim mạch" ? "bxs-heart" : spec.specializationName == "Nội khoa" ? "bxs-brain" : spec.specializationName == "Nhi khoa" ? "bxs-baby-carriage" : spec.specializationName == "Ngoại khoa" ? "bxs-injection" : spec.specializationName == "Sản phụ khoa" ? "bxs-female" : spec.specializationName == "Mắt" ? "bxs-low-vision" : spec.specializationName == "Tai Mũi Họng" ? "bxs-face-mask" : "bxs-droplet"} text-4xl text-blue-600 mr-4'></i>
            <h3 class="text-xl font-semibold">${spec.specializationName}</h3>
          </div>
          <p class="text-gray-600 mb-4">${spec.description}</p>
          <div class="flex items-center text-gray-500 mb-4">
            <i class='bx bx-user mr-2'></i>
            <span>Bác sĩ trưởng: ${spec.headDoctorId}</span>
          </div>
          <a href="specializations?action=details&id=${spec.id}" class="text-blue-600 hover:underline">
            Xem bác sĩ <i class='bx bx-right-arrow-alt'></i>
          </a>
        </div>
      </c:forEach>
    </div>
    <div class="text-center mt-8">
      <a href="specializations" class="bg-blue-600 text-white px-6 py-3 rounded-full font-semibold hover:bg-blue-700 transition duration-300">
        Xem tất cả chuyên khoa <i class='bx bx-right-arrow-alt'></i>
      </a>
    </div>
  </div>
</section>

<!-- Doctors Section -->
<section class="py-16 bg-gray-100">
  <div class="container mx-auto px-4">
    <div class="text-center mb-10">
      <h2 class="text-3xl font-bold">Đội ngũ bác sĩ</h2>
      <p class="text-gray-600">Bác sĩ giàu kinh nghiệm, tận tâm với nghề</p>
    </div>
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <c:forEach var="doc" items="${featuredDoctors}">
        <div class="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition duration-300">
          <div class="relative mb-4">
            <img src="${doc.user.profilePicture != null ? doc.user.profilePicture : 'https://via.placeholder.com/150'}" alt="${doc.licenseNumber}" class="w-full h-48 object-cover rounded-lg">
          </div>
          <h3 class="text-xl font-semibold">${doc.licenseNumber}</h3>
          <p class="text-gray-600 flex items-center">
            <i class='bx bxs-heart mr-2'></i> Chuyên khoa ${doc.specialization.specializationName}
          </p>
          <p class="text-gray-600 flex items-center">
            <i class='bx bx-time mr-2'></i> ${doc.yearsOfExperience} năm kinh nghiệm
          </p>
          <a href="doctors?action=details&id=${doc.id}" class="bg-blue-600 text-white px-4 py-2 rounded-full mt-4 inline-block hover:bg-blue-700 transition duration-300">
            <i class='bx bx-calendar-plus mr-2'></i> Đặt lịch
          </a>
        </div>
      </c:forEach>
    </div>
    <div class="text-center mt-8">
      <a href="doctors" class="bg-blue-600 text-white px-6 py-3 rounded-full font-semibold hover:bg-blue-700 transition duration-300">
        Xem tất cả bác sĩ <i class='bx bx-right-arrow-alt'></i>
      </a>
    </div>
  </div>
</section>
<jsp:include page="web-page/components/footer.jsp" />