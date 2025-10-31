<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="web-page/components/header.jsp" />

<section class="py-10 bg-gray-50 min-h-screen">
  <div class="container mx-auto px-4">
    <h1 class="text-2xl font-bold text-center mb-8">Chi tiết lịch hẹn</h1>

    <div class="bg-white shadow rounded-lg border border-gray-200 max-w-2xl mx-auto p-6">
      <c:if test="${not empty appointment}">
        <div class="space-y-4">
          <div class="flex justify-between border-b pb-2">
            <span class="font-semibold text-gray-700">Mã lịch hẹn:</span>
            <span>${appointment.id}</span>
          </div>

          <div class="flex justify-between border-b pb-2">
            <span class="font-semibold text-gray-700">Ngày:</span>
            <span>${appointment.appointmentDate}</span>
          </div>

          <div class="flex justify-between border-b pb-2">
            <span class="font-semibold text-gray-700">Giờ:</span>
            <span>${appointment.appointmentTime}</span>
          </div>

          <div class="flex justify-between border-b pb-2">
            <span class="font-semibold text-gray-700">Bác sĩ:</span>
            <span>${appointment.doctor.user.fullName}</span>
          </div>

          <div class="flex justify-between border-b pb-2">
            <span class="font-semibold text-gray-700">Chuyên khoa:</span>
            <span>${appointment.specialization.specializationName}</span>
          </div>

          <div class="flex justify-between border-b pb-2">
            <span class="font-semibold text-gray-700">Phòng:</span>
            <span>${appointment.room.roomName}</span>
          </div>

          <div class="flex justify-between border-b pb-2">
            <span class="font-semibold text-gray-700">Trạng thái:</span>
            <span class="
              px-2 py-1 rounded-full text-sm
              ${appointment.status eq 'Completed' ? 'bg-green-100 text-green-700'
              : appointment.status eq 'Cancelled' ? 'bg-red-100 text-red-700'
              : 'bg-yellow-100 text-yellow-700'}">
              ${appointment.status}
            </span>
          </div>

          <c:if test="${not empty appointment.reason}">
            <div class="border-b pb-2">
              <span class="font-semibold text-gray-700 block mb-1">Ghi chú / Lý do khám:</span>
              <p class="text-gray-600">${appointment.reason}</p>
            </div>
          </c:if>
        </div>

        <div class="text-center mt-6">
          <a href="appointments" 
             class="inline-block bg-blue-600 text-white px-5 py-2 rounded-full hover:bg-blue-700 transition">
            ← Quay lại danh sách
          </a>
        </div>
      </c:if>

      <c:if test="${empty appointment}">
        <p class="text-center text-red-500 font-medium">Không tìm thấy thông tin lịch hẹn.</p>
        <div class="text-center mt-6">
          <a href="appointments" 
             class="inline-block bg-blue-600 text-white px-5 py-2 rounded-full hover:bg-blue-700 transition">
            ← Quay lại danh sách
          </a>
        </div>
      </c:if>
    </div>
  </div>
</section>

<jsp:include page="web-page/components/footer.jsp" />
