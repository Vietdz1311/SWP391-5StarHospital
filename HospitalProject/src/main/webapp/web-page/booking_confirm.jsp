<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<!-- Confirmation Success Header -->
<section class="relative bg-gradient-to-r from-blue-50 to-indigo-100 py-20 overflow-hidden">
  <div class="absolute inset-0 bg-[url('data:image/svg+xml,%3Csvg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="none" fill-rule="evenodd"%3E%3Cg fill="%239C92AC" fill-opacity="0.05"%3E%3Ccircle cx="30" cy="30" r="2"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')] opacity-50"></div>
  <div class="container mx-auto px-4 text-center relative z-10">
    <div class="inline-flex items-center justify-center w-20 h-20 bg-blue-100 rounded-full mb-6">
      <i class='bx bx-check-shield text-3xl text-blue-600'></i>
    </div>
    <h1 class="text-5xl md:text-6xl font-bold text-gray-900 mb-4 bg-gradient-to-r from-blue-600 to-indigo-700 bg-clip-text text-transparent">Xác nhận thành công!</h1>
    <p class="text-xl text-gray-600 max-w-2xl mx-auto">Lịch hẹn của bạn đã được xác nhận. Chúng tôi mong gặp bạn tại Bệnh viện Tâm Đức!</p>
  </div>
</section>
<!-- Confirmation Details -->
<section class="py-20 bg-gray-50">
  <div class="container mx-auto px-4">
    <div class="max-w-2xl mx-auto bg-white rounded-2xl shadow-xl p-8 lg:p-12">
      <h2 class="text-2xl font-bold text-gray-900 mb-8 text-center">Chi tiết lịch hẹn</h2>
      <div class="space-y-6 text-center">
        <div class="flex items-center justify-center text-gray-600 mb-4">
          <i class='bx bx-user-md text-blue-600 mr-3 text-2xl'></i>
          <span class="font-semibold text-gray-900">Bác sĩ: ${doctorName}</span>
        </div>
        <div class="flex items-center justify-center text-gray-600 mb-4">
          <i class='bx bx-calendar text-blue-600 mr-3 text-2xl'></i>
          <span class="font-semibold text-gray-900">Ngày: ${appointmentDate}</span>
        </div>
        <div class="flex items-center justify-center text-gray-600 mb-4">
          <i class='bx bx-time text-blue-600 mr-3 text-2xl'></i>
          <span class="font-semibold text-gray-900">Giờ: ${appointmentTime}</span>
        </div>
        <div class="flex items-center justify-center text-gray-600 mb-8">
          <i class='bx bx-location-plus text-blue-600 mr-3 text-2xl'></i>
          <span class="font-semibold text-gray-900">Địa điểm: Bệnh viện Tâm Đức</span>
        </div>
      </div>
      <div class="flex flex-col sm:flex-row gap-4 justify-center">
        <a href="appointments" class="inline-flex items-center justify-center px-6 py-3 bg-gradient-to-r from-green-600 to-emerald-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg hover:from-green-700 hover:to-emerald-700 transition-all duration-300 transform hover:-translate-y-0.5">
          <i class='bx bx-calendar-check mr-2'></i>
          Quản lý lịch hẹn
        </a>
        <a href="home" class="inline-flex items-center justify-center px-6 py-3 border-2 border-gray-300 text-gray-700 font-semibold rounded-xl hover:border-blue-600 hover:text-blue-600 transition-all duration-300">
          <i class='bx bx-home mr-2'></i>
          Về trang chủ
        </a>
      </div>
    </div>
  </div>
</section>
<jsp:include page="./components/footer.jsp" />