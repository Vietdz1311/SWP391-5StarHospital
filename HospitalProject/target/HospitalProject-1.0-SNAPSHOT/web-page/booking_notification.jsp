<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<!-- Success Header -->
<section class="relative bg-gradient-to-r from-green-50 to-emerald-100 py-20 overflow-hidden">
  <div class="absolute inset-0 bg-[url('data:image/svg+xml,%3Csvg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="none" fill-rule="evenodd"%3E%3Cg fill="%239C92AC" fill-opacity="0.05"%3E%3Ccircle cx="30" cy="30" r="2"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')] opacity-50"></div>
  <div class="container mx-auto px-4 text-center relative z-10">
    <div class="inline-flex items-center justify-center w-20 h-20 bg-green-100 rounded-full mb-6">
      <i class='bx bx-check-double text-3xl text-green-600'></i>
    </div>
    <h1 class="text-5xl md:text-6xl font-bold text-gray-900 mb-4 bg-gradient-to-r from-green-600 to-emerald-700 bg-clip-text text-transparent">Đặt lịch thành công!</h1>
    <p class="text-xl text-gray-600 max-w-2xl mx-auto">Cảm ơn bạn đã tin tưởng Bệnh viện Tâm Đức. Chúng tôi đã gửi email xác nhận đến hộp thư của bạn.</p>
  </div>
</section>
<!-- Confirmation Instructions -->
<section class="py-20 bg-white">
  <div class="container mx-auto px-4">
    <div class="max-w-md mx-auto bg-white rounded-2xl shadow-xl p-8 text-center">
      <h2 class="text-2xl font-bold text-gray-900 mb-6">Hướng dẫn xác nhận</h2>
      <div class="space-y-4 mb-8">
        <div class="flex items-start space-x-3">
          <i class='bx bx-envelope text-green-600 text-xl mt-1'></i>
          <p class="text-gray-600">Vui lòng kiểm tra email (và thư mục spam) để nhận thông báo xác nhận lịch hẹn.</p>
        </div>
        <div class="flex items-start space-x-3">
          <i class='bx bx-time-five text-green-600 text-xl mt-1'></i>
          <p class="text-gray-600">Lịch hẹn sẽ được kích hoạt sau khi bạn xác nhận qua email trong vòng 24 giờ.</p>
        </div>
      </div>
      <div class="flex flex-col sm:flex-row gap-4 justify-center">
        <a href="appointments" class="inline-flex items-center justify-center px-6 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg hover:from-blue-700 hover:to-indigo-700 transition-all duration-300 transform hover:-translate-y-0.5">
          <i class='bx bx-calendar-check mr-2'></i>
          Xem lịch hẹn
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