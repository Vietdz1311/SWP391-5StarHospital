<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />

<!-- Page Header -->
<section class="bg-gray-100 py-16">
  <div class="container mx-auto px-4 text-center">
    <h1 class="text-4xl font-bold text-gray-800 mb-4">Lịch sử lịch hẹn</h1>
    <p class="text-lg text-gray-600">Xem và quản lý các lịch hẹn của bạn</p>
  </div>
</section>

<!-- Appointments List -->
<section class="py-16">
  <div class="container mx-auto px-4">
    <div class="mb-8">
      <form action="appointments" method="get" class="flex flex-col sm:flex-row gap-4 items-center">
        <input type="text" name="search" placeholder="Tìm kiếm theo bệnh nhân, lý do hoặc ngày..." value="${search}" class="border rounded-lg p-2 w-full sm:w-1/3 focus:outline-none focus:ring-2 focus:ring-blue-600">
        <select name="filterStatus" class="border rounded-lg p-2 w-full sm:w-1/4 focus:outline-none focus:ring-2 focus:ring-blue-600">
          <option value="">Tất cả trạng thái</option>
          <option value="Pending" ${filterStatus == 'Pending' ? 'selected' : ''}>Chờ xác nhận</option>
          <option value="Confirmed" ${filterStatus == 'Confirmed' ? 'selected' : ''}>Đã xác nhận</option>
          <option value="Canceled" ${filterStatus == 'Canceled' ? 'selected' : ''}>Đã hủy</option>
        </select>
        <select name="sortBy" class="border rounded-lg p-2 w-full sm:w-1/4 focus:outline-none focus:ring-2 focus:ring-blue-600">
          <option value="">Sắp xếp theo</option>
          <option value="appointment_date DESC" ${sortBy == 'appointment_date DESC' ? 'selected' : ''}>Ngày mới nhất</option>
          <option value="appointment_date ASC" ${sortBy == 'appointment_date ASC' ? 'selected' : ''}>Ngày cũ nhất</option>
        </select>
        <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition duration-300">Lọc</button>
      </form>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full bg-white rounded-lg shadow-md">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ngày giờ</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Bệnh nhân</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Bác sĩ</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Phòng</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Lý do</th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <c:forEach var="appt" items="${appointments}">
            <tr class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">
                  <c:if test="${appt.appointmentDate != null}">
                    ${appt.appointmentDate} - ${appt.appointmentTime}
                  </c:if>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">${appt.patient.fullName != null ? appt.patient.fullName : 'Bản thân'}</div>
                <div class="text-sm text-gray-500">${appt.patient.relationship != null ? appt.patient.relationship : ''}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">${appt.doctor.licenseNumber}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">${appt.room.roomName}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${appt.status == 'Pending' ? 'bg-yellow-100 text-yellow-800' : appt.status == 'Confirmed' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">
                  ${appt.status == 'Pending' ? 'Chờ xác nhận' : appt.status == 'Confirmed' ? 'Đã xác nhận' : 'Đã hủy'}
                </span>
              </td>
              <td class="px-6 py-4">
                <div class="text-sm text-gray-900">${appt.reason}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <a href="appointments?action=details&id=${appt.id}" class="text-blue-600 hover:text-blue-900">Chi tiết</a>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>

    <div class="mt-8 flex justify-center gap-2">
      <c:forEach begin="1" end="${totalPages}" var="i">
        <a href="appointments?page=${i}&search=${search}&filterStatus=${filterStatus}&sortBy=${sortBy}" class="px-4 py-2 rounded-lg ${currentPage == i ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-800 hover:bg-gray-300'} transition duration-300">${i}</a>
      </c:forEach>
    </div>
  </div>
</section>

<jsp:include page="./components/footer.jsp" />