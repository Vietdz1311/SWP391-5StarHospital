<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="web-page/components/header.jsp" />

<section class="py-10 bg-gray-50 min-h-screen">
  <div class="container mx-auto px-4">
    <h1 class="text-2xl font-bold text-center mb-8">Lịch hẹn của tôi</h1>

    <table class="w-full bg-white shadow rounded-lg border border-gray-200">
      <thead class="bg-blue-100">
        <tr>
          <th class="py-2 px-4 text-left">#</th>
          <th class="py-2 px-4 text-left">Ngày</th>
          <th class="py-2 px-4 text-left">Giờ</th>
          <th class="py-2 px-4 text-left">Bác sĩ</th>
          <th class="py-2 px-4 text-left">Chuyên khoa</th>
          <th class="py-2 px-4 text-left">Phòng</th>
          <th class="py-2 px-4 text-left">Trạng thái</th>
          <th class="py-2 px-4 text-center">Hành động</th>
        </tr>
      </thead>

      <tbody>
        <c:forEach var="a" items="${appointments}" varStatus="loop">
          <tr class="border-t hover:bg-gray-50 transition">
            <td class="py-2 px-4">${loop.index + 1}</td>
            <td class="py-2 px-4">${a.appointmentDate}</td>
            <td class="py-2 px-4">${a.appointmentTime}</td>
            <td class="py-2 px-4">${a.doctor.user.fullName}</td>
            <td class="py-2 px-4">${a.specialization.specializationName}</td>
            <td class="py-2 px-4">${a.room.roomName}</td>
            <td class="py-2 px-4">
              <span class="px-2 py-1 rounded-full text-sm
                ${a.status eq 'Completed' ? 'bg-green-100 text-green-700'
                : a.status eq 'Cancelled' ? 'bg-red-100 text-red-700'
                : 'bg-yellow-100 text-yellow-700'}">
                ${a.status}
              </span>
            </td>

            <!-- 🔹 Nút Xem chi tiết -->
            <td class="py-2 px-4 text-center">
              <a href="appointments?action=details&id=${a.id}"
                 class="bg-blue-600 text-white px-4 py-1.5 rounded-full hover:bg-blue-700 transition">
                 <i class='bx bx-detail mr-1'></i>Chi tiết
              </a>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>

    <!-- PHÂN TRANG -->
    <div class="flex justify-center items-center mt-6 space-x-2">
      <c:if test="${currentPage > 1}">
        <a href="appointments?page=${currentPage - 1}" class="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300">&laquo; Trước</a>
      </c:if>

      <c:forEach var="i" begin="1" end="${totalPages}">
        <a href="appointments?page=${i}" 
           class="px-3 py-1 rounded ${i eq currentPage ? 'bg-blue-600 text-white' : 'bg-gray-200 hover:bg-gray-300'}">${i}</a>
      </c:forEach>

      <c:if test="${currentPage < totalPages}">
        <a href="appointments?page=${currentPage + 1}" class="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300">Sau &raquo;</a>
      </c:if>
    </div>
  </div>
</section>

<jsp:include page="web-page/components/footer.jsp" />
