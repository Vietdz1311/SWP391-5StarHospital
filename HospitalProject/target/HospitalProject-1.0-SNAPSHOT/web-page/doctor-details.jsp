<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="./components/header.jsp" />

<!-- Page Header -->
<section class="relative bg-gradient-to-r from-blue-50 to-indigo-100 py-20 overflow-hidden">
  <div class="absolute inset-0 bg-[url('data:image/svg+xml,%3Csvg width=\"60\" height=\"60\" viewBox=\"0 0 60 60\" xmlns=\"http://www.w3.org/2000/svg\"%3E%3Cg fill=\"none\" fill-rule=\"evenodd\"%3E%3Cg fill=\"%239C92AC\" fill-opacity=\"0.05\"%3E%3Ccircle cx=\"30\" cy=\"30\" r=\"2\"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')] opacity-50"></div>
  <div class="container mx-auto px-4 text-center relative z-10">
    <h1 class="text-5xl md:text-6xl font-bold text-gray-900 mb-4 bg-gradient-to-r from-blue-600 to-indigo-700 bg-clip-text text-transparent">
      ${doctor.user.fullName != null ? doctor.user.fullName : doctor.licenseNumber}
    </h1>
    <p class="text-xl text-gray-600 max-w-3xl mx-auto">${doctor.bio != null ? doctor.bio : 'Không có thông tin tiểu sử'}</p>
  </div>
</section>

<!-- Doctor Details -->
<section class="py-20 bg-gray-50">
  <div class="container mx-auto px-4">
    <div class="bg-white rounded-2xl shadow-xl overflow-hidden max-w-7xl mx-auto">
      <div class="relative">
        <img src="${doctor.user.profilePicture != null ? doctor.user.profilePicture : 'https://via.placeholder.com/800x300?text=Doctor+Profile'}" alt="${doctor.user.fullName}" class="w-full h-80 object-cover">
      </div>
      <div class="p-8 lg:p-12">
        <div class="flex flex-col lg:flex-row items-start gap-8 lg:gap-12">
          <!-- Doctor Image -->
          <div class="flex-shrink-0">
            <img src="${doctor.user.profilePicture != null ? doctor.user.profilePicture : 'https://via.placeholder.com/800x300?text=Doctor+Profile'}" alt="${doctor.user.fullName}" class="w-48 h-48 object-cover rounded-2xl shadow-lg border-4 border-white">
          </div>
          <!-- Doctor Information -->
          <div class="flex-1">
            <h2 class="text-3xl font-bold text-gray-900 mb-6">Thông tin bác sĩ</h2>
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <!-- Personal Details -->
              <div class="space-y-4">
                  <div class="flex items-center text-gray-600">
                  <i class='bx bx-id-card text-blue-600 mr-4 text-2xl'></i>
                  <div>
                    <p class="font-semibold text-gray-900">Thông tin bác sĩ</p>
                    <p class="text-lg">${doctor.user.fullName != null ? doctor.user.fullName : 'N/A'}</p>
                  </div>
                </div>
                <div class="flex items-center text-gray-600">
                  <i class='bx bx-id-card text-blue-600 mr-4 text-2xl'></i>
                  <div>
                    <p class="font-semibold text-gray-900">Mã giấy phép</p>
                    <p class="text-lg">${doctor.licenseNumber != null ? doctor.licenseNumber : 'N/A'}</p>
                  </div>
                </div>
                <div class="flex items-center text-gray-600">
                  <i class='bx bx-clinic text-blue-600 mr-4 text-2xl'></i>
                  <div>
                    <p class="font-semibold text-gray-900">Chuyên khoa</p>
                    <p class="text-lg">${doctor.specialization.specializationName != null ? doctor.specialization.specializationName : 'N/A'}</p>
                  </div>
                </div>
                <div class="flex items-center text-gray-600">
                  <i class='bx bx-award text-blue-600 mr-4 text-2xl'></i>
                  <div>
                    <p class="font-semibold text-gray-900">Kinh nghiệm</p>
                    <p class="text-lg">${doctor.yearsOfExperience != null ? doctor.yearsOfExperience : 'N/A'} năm</p>
                  </div>
                </div>
                <div class="flex items-center text-gray-600">
                  <i class='bx bx-bookmark-minus text-blue-600 mr-4 text-2xl'></i>
                  <div>
                    <p class="font-semibold text-gray-900">Chứng chỉ</p>
                    <p class="text-lg">${doctor.certification != null ? doctor.certification : 'N/A'}</p>
                  </div>
                </div>
                <div class="flex items-center text-gray-600">
                  <i class='bx bx-bookmark-plus text-blue-600 mr-4 text-2xl'></i>
                  <div>
                    <p class="font-semibold text-gray-900">Xác minh</p>
                    <span class="inline-block px-4 py-2 rounded-full text-sm font-medium ${doctor.getIsVerified() ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">
                      ${doctor.getIsVerified() ? 'Đã xác minh' : 'Chưa xác minh'}
                    </span>
                  </div>
                </div>
              </div>
            </div>
            <div class="mt-8">
              <a href="booking" class="inline-flex items-center justify-center w-full lg:w-auto px-8 py-4 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg hover:from-blue-700 hover:to-indigo-700 transition-all duration-300 transform hover:-translate-y-0.5">
                <i class='bx bx-calendar-plus mr-3 text-xl'></i>
                Đặt lịch khám ngay
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

<jsp:include page="./components/footer.jsp" />