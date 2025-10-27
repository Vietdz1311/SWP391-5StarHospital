<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="./components/header.jsp" />

<!-- Page Header -->
<section class="relative bg-gradient-to-r from-blue-50 to-indigo-100 py-20 overflow-hidden">
  <div class="absolute inset-0 bg-[url('data:image/svg+xml,%3Csvg width=\"60\" height=\"60\" viewBox=\"0 0 60 60\" xmlns=\"http://www.w3.org/2000/svg\"%3E%3Cg fill=\"none\" fill-rule=\"evenodd\"%3E%3Cg fill=\"%239C92AC\" fill-opacity=\"0.05\"%3E%3Ccircle cx=\"30\" cy=\"30\" r=\"2\"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')] opacity-50"></div>
  <div class="container mx-auto px-4 text-center relative z-10">
    <h1 class="text-5xl font-bold text-gray-900 mb-4 bg-gradient-to-r from-blue-600 to-indigo-700 bg-clip-text text-transparent">Chi tiết lịch hẹn</h1>
    <p class="text-xl text-gray-600 max-w-3xl mx-auto">Thông tin chi tiết về lịch hẹn của bạn</p>
  </div>
</section>

<!-- Appointment Details -->
<section class="py-20 bg-gray-50">
  <div class="container mx-auto px-4">
    <div class="bg-white rounded-2xl shadow-xl p-8 max-w-4xl mx-auto">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <h2 class="text-2xl font-bold text-gray-900 mb-6">Thông tin lịch hẹn</h2>
          <div class="space-y-4">
            <div class="flex items-center text-gray-600">
              <i class='bx bx-calendar text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Ngày giờ</p>
                <p class="text-lg">
                    ${appointment.appointmentDate} - ${appointment.appointmentTime}
                </p>
              </div>
            </div>
            <div class="flex items-center text-gray-600">
              <i class='bx bx-user text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Bệnh nhân</p>
                <p class="text-lg">${appointment.patient.fullName} ${appointment.patient.relationship != null ? '(' + appointment.patient.relationship + ')' : '(Bản thân)'}</p>
              </div>
            </div>
            <div class="flex items-center text-gray-600">
              <i class='bx bx-clinic text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Chuyên khoa</p>
                <p class="text-lg">${appointment.specialization.specializationName}</p>
              </div>
            </div>
            <div class="flex items-center text-gray-600">
              <i class='bx bx-door-open text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Phòng</p>
                <p class="text-lg">${appointment.room.roomName}</p>
              </div>
            </div>
            <div class="flex items-center text-gray-600">
              <i class='bx bx-user-plus text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Bác sĩ</p>
                <p class="text-lg">${appointment.doctor.licenseNumber}</p>
              </div>
            </div>
            <div class="flex items-center text-gray-600">
              <i class='bx bx-shield-alt text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Bảo hiểm y tế</p>
                <p class="text-lg">${appointment.healthInsurance.insuranceNumber != null ? appointment.healthInsurance.provider + ' (' + appointment.healthInsurance.insuranceNumber + ')' : 'Không sử dụng'}</p>
              </div>
            </div>
          </div>
        </div>
        <div>
          <h2 class="text-2xl font-bold text-gray-900 mb-6">Chi tiết thêm</h2>
          <div class="space-y-4">
            <div class="flex items-center text-gray-600">
              <i class='bx bx-message-detail text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Lý do</p>
                <p class="text-lg">${appointment.reason}</p>
              </div>
            </div>
            <div class="flex items-center text-gray-600">
              <i class='bx bx-repeat text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Tái khám</p>
                <span class="inline-block px-4 py-2 rounded-full text-sm font-medium ${appointment.followUp ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">
                  ${appointment.followUp ? 'Có' : 'Không'}
                </span>
              </div>
            </div>
            <div class="flex items-center text-gray-600">
              <i class='bx bx-link text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Lịch hẹn trước</p>
                <p class="text-lg">${appointment.previousAppointmentId != null ? appointment.previousAppointmentId : 'Không có'}</p>
              </div>
            </div>
            <div class="flex items-center text-gray-600">
              <i class='bx bx-time text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Trạng thái</p>
                <span class="inline-block px-4 py-2 rounded-full text-sm font-medium ${appointment.status == 'Pending' ? 'bg-yellow-100 text-yellow-800' : appointment.status == 'Confirmed' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">
                  ${appointment.status}
                </span>
              </div>
            </div>
            <div class="flex items-center text-gray-600">
              <i class='bx bx-calendar-check text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Ngày tạo</p>
                <p class="text-lg">${appointment.createdAt}</p>
              </div>
            </div>
            <div class="flex items-center text-gray-600">
              <i class='bx bx-calendar-edit text-blue-600 mr-4 text-2xl'></i>
              <div>
                <p class="font-semibold text-gray-900">Ngày cập nhật</p>
                <p class="text-lg">${appointment.updatedAt}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="mt-8 text-center">
        <a href="appointments" class="bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-8 py-4 rounded-xl font-semibold hover:from-blue-700 hover:to-indigo-700 transition-all duration-300 transform hover:-translate-y-0.5 inline-flex items-center">
          <i class='bx bx-arrow-back mr-2 text-xl'></i> Quay về danh sách
        </a>
      </div>
    </div>
  </div>
</section>

<jsp:include page="./components/footer.jsp" />