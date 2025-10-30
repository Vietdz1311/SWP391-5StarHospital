<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />

<!-- Page Header -->
<section class="relative bg-gradient-to-r from-blue-50 to-indigo-100 py-20 overflow-hidden">
  <div class="absolute inset-0 bg-[url('data:image/svg+xml,%3Csvg width=\"60\" height=\"60\" viewBox=\"0 0 60 60\" xmlns=\"http://www.w3.org/2000/svg\"%3E%3Cg fill=\"none\" fill-rule=\"evenodd\"%3E%3Cg fill=\"%239C92AC\" fill-opacity=\"0.05\"%3E%3Ccircle cx=\"30\" cy=\"30\" r=\"2\"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')] opacity-50"></div>
  <div class="container mx-auto px-4 text-center relative z-10">
    <h1 class="text-5xl font-bold text-gray-900 mb-4 bg-gradient-to-r from-blue-600 to-indigo-700 bg-clip-text text-transparent">Đặt lịch khám</h1>
    <p class="text-xl text-gray-600 max-w-3xl mx-auto">Dễ dàng đặt lịch với bác sĩ chuyên khoa phù hợp</p>
  </div>
</section>

<!-- Booking Form -->
<section class="py-20 bg-gray-50">
  <div class="container mx-auto px-4">
    <div class="bg-white rounded-2xl shadow-xl p-8 max-w-3xl mx-auto">
      <form id="bookingForm" class="space-y-6">
        <div>
          <label for="specializationId" class="block text-lg font-semibold text-gray-900 mb-2">Chuyên khoa</label>
          <select id="specializationId" name="specializationId" class="w-full border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-600" required>
            <option value="">Chọn chuyên khoa</option>
            <c:forEach var="spec" items="${specializations}">
              <option value="${spec.id}">${spec.specializationName}</option>
            </c:forEach>
          </select>
        </div>
        <div>
          <label for="areaId" class="block text-lg font-semibold text-gray-900 mb-2">Khu vực</label>
          <select id="areaId" name="areaId" class="w-full border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-600" required>
            <option value="">Chọn khu vực</option>
          </select>
        </div>
        <div>
          <label for="roomId" class="block text-lg font-semibold text-gray-900 mb-2">Phòng khám</label>
          <select id="roomId" name="roomId" class="w-full border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-600" disabled required>
            <option value="">Chọn phòng khám</option>
          </select>
        </div>
        <div>
          <label for="doctorId" class="block text-lg font-semibold text-gray-900 mb-2">Bác sĩ</label>
          <select id="doctorId" name="doctorId" class="w-full border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-600" disabled required>
            <option value="">Chọn bác sĩ</option>
          </select>
        </div>
        <div>
          <label for="appointmentDate" class="block text-lg font-semibold text-gray-900 mb-2">Ngày khám</label>
          <input type="date" id="appointmentDate" name="appointmentDate" class="w-full border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-600" required>
        </div>
        <div>
          <label for="appointmentTime" class="block text-lg font-semibold text-gray-900 mb-2">Khung giờ</label>
          <select id="appointmentTime" name="appointmentTime" class="w-full border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-600" disabled required>
            <option value="">Chọn khung giờ</option>
          </select>
        </div>
        <div>
          <label for="patientId" class="block text-lg font-semibold text-gray-900 mb-2">Đặt lịch cho</label>
          <select id="patientId" name="patientId" class="w-full border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-600" required>
            <option value="">Đang tải...</option>
          </select>
        </div>
        <div>
          <label for="healthInsuranceId" class="block text-lg font-semibold text-gray-900 mb-2">Bảo hiểm y tế</label>
          <select id="healthInsuranceId" name="healthInsuranceId" class="w-full border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-600">
            <option value="">Không sử dụng bảo hiểm</option>
          </select>
        </div>
        <div>
          <label for="reason" class="block text-lg font-semibold text-gray-900 mb-2">Lý do khám</label>
          <textarea id="reason" name="reason" class="w-full border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-600" rows="4" required></textarea>
        </div>
        <div>
          <label for="isFollowUp" class="block text-lg font-semibold text-gray-900 mb-2">Cuộc hẹn tái khám</label>
          <input type="checkbox" id="isFollowUp" name="isFollowUp" class="mr-2">
          <label for="isFollowUp" class="text-gray-600">Đây là cuộc hẹn tái khám</label>
        </div>
        <div id="previousAppointmentDiv" class="hidden">
          <label for="previousAppointmentId" class="block text-lg font-semibold text-gray-900 mb-2">Cuộc hẹn trước đó</label>
          <select id="previousAppointmentId" name="previousAppointmentId" class="w-full border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-600">
            <option value="">Không có</option>
          </select>
        </div>
        <div id="errorMessage" class="hidden text-red-600 font-medium"></div>
        <button type="submit" id="submitButton" class="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-6 py-4 rounded-xl font-semibold hover:from-blue-700 hover:to-indigo-700 transition-all duration-300 transform hover:-translate-y-0.5 disabled:opacity-50" disabled>
          <i class='bx bx-calendar-plus mr-2'></i> Xác nhận đặt lịch
        </button>
      </form>
      <div id="loadingSpinner" class="hidden flex items-center justify-center mt-4">
        <i class='bx bx-loader-alt animate-spin text-3xl text-blue-600'></i>
      </div>
    </div>
  </div>
</section>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
$(document).ready(function() {
    // Set minimum date using JavaScript
    const today = new Date().toISOString().split('T')[0];
    $('#appointmentDate').attr('min', today);

    // Load current user details
    let currentUser = null;
    $.ajax({
        url: 'booking?action=loadCurrentUser',
        method: 'GET',
        dataType: 'json',
        success: function(user) {
            if (user && user.id) {
                currentUser = user;
                const patientSelect = $('#patientId');
                patientSelect.html(`<option value="${user.id}">Đặt cho bản thân (${user.fullName})</option>`);
                // Load family members after user is loaded
                loadFamilyMembers();
            } else {
                $('#errorMessage').text('Vui lòng đăng nhập để đặt lịch').removeClass('hidden');
                $('#submitButton').prop('disabled', true);
            }
        },
        error: function() {
            $('#errorMessage').text('Không thể tải thông tin người dùng').removeClass('hidden');
            $('#submitButton').prop('disabled', true);
        }
    });

    // Load areas
    $.ajax({
        url: 'booking?action=loadAreas',
        method: 'GET',
        dataType: 'json',
        success: function(areas) {
            const areaSelect = $('#areaId');
            areas.forEach(area => {
                areaSelect.append('<option value="' + area.id + '">' + area.areaName + '</option>');
            });
        },
        error: function() {
            $('#errorMessage').text('Không thể tải danh sách khu vực').removeClass('hidden');
        }
    });

    // Load rooms on specialization or area change
    $('#specializationId, #areaId').change(function() {
        const specializationId = $('#specializationId').val();
        const areaId = $('#areaId').val();
        const roomSelect = $('#roomId');
        const doctorSelect = $('#doctorId');
        const timeSelect = $('#appointmentTime');
        roomSelect.prop('disabled', true).html('<option value="">Chọn phòng khám</option>');
        doctorSelect.prop('disabled', true).html('<option value="">Chọn bác sĩ</option>');
        timeSelect.prop('disabled', true).html('<option value="">Chọn khung giờ</option>');
        $('#submitButton').prop('disabled', true);

        if (specializationId && areaId) {
            $('#loadingSpinner').removeClass('hidden');
            $.ajax({
                url: 'booking?action=loadRooms',
                method: 'GET',
                data: { specializationId: specializationId, areaId: areaId },
                dataType: 'json',
                success: function(rooms) {
                    rooms.forEach(room => {
                        roomSelect.append('<option value="' + room.id + '">' + room.roomName + '</option>');
                    });
                    roomSelect.prop('disabled', false);
                    $('#loadingSpinner').addClass('hidden');
                },
                error: function() {
                    $('#errorMessage').text('Không thể tải danh sách phòng khám').removeClass('hidden');
                    $('#loadingSpinner').addClass('hidden');
                }
            });

            // Load doctors
            $.ajax({
                url: 'booking?action=loadDoctors',
                method: 'GET',
                data: { specializationId: specializationId },
                dataType: 'json',
                success: function(doctors) {
                    doctors.forEach(doctor => {
                        doctorSelect.append('<option value="' + doctor.id + '">' + doctor.user.fullName + '</option>');
                    });
                    doctorSelect.prop('disabled', false);
                },
                error: function() {
                    $('#errorMessage').text('Không thể tải danh sách bác sĩ').removeClass('hidden');
                }
            });
        }
    });

    // Load slots on room, doctor, or date change
    $('#appointmentDate').change(function() {
        const roomId = $('#roomId').val();
        const doctorId = $('#doctorId').val();
        const date = $('#appointmentDate').val();
        const timeSelect = $('#appointmentTime');
        timeSelect.prop('disabled', true).html('<option value="">Chọn khung giờ</option>');
        $('#submitButton').prop('disabled', true);

        if (roomId && doctorId && date) {
            $('#loadingSpinner').removeClass('hidden');
            $.ajax({
                url: 'booking?action=loadSlots',
                method: 'GET',
                data: { roomId: roomId, doctorId: doctorId, date: date },
                dataType: 'json',
                success: function(slots) {
                    slots.forEach(slot => {
                        timeSelect.append('<option value="' + slot.startTime + '">' + slot.startTime + ' - ' + slot.endTime + '</option>');
                    });
                    timeSelect.prop('disabled', false);
                    $('#submitButton').prop('disabled', false);
                    $('#loadingSpinner').addClass('hidden');
                },
                error: function() {
                    $('#errorMessage').text('Không thể tải danh sách khung giờ').removeClass('hidden');
                    $('#loadingSpinner').addClass('hidden');
                }
            });
        }
    });

    // Load family members
    function loadFamilyMembers() {
        $.ajax({
            url: 'booking?action=loadFamilyMembers',
            method: 'GET',
            dataType: 'json',
            success: function(familyMembers) {
                const patientSelect = $('#patientId');
                familyMembers.forEach(member => {
                    patientSelect.append('<option value="' + member.id + '">' + member.fullName + ' (' + member.relationship + ')</option>');
                });
            },
            error: function() {
                $('#errorMessage').text('Không thể tải danh sách thành viên gia đình').removeClass('hidden');
            }
        });
    }

    // Load health insurances
    $.ajax({
        url: 'booking?action=loadInsurances',
        method: 'GET',
        dataType: 'json',
        success: function(insurances) {
            const insuranceSelect = $('#healthInsuranceId');
            insurances.forEach(insurance => {
                insuranceSelect.append('<option value="' + insurance.id + '">' + insurance.provider + ' (' + insurance.insuranceNumber + ')</option>');
            });
        },
        error: function() {
            $('#errorMessage').text('Không thể tải danh sách bảo hiểm').removeClass('hidden');
        }
    });

    // Toggle previous appointment field
    $('#isFollowUp').change(function() {
        if ($(this).is(':checked')) {
            $('#previousAppointmentDiv').removeClass('hidden');
        } else {
            $('#previousAppointmentDiv').addClass('hidden');
            $('#previousAppointmentId').val('');
        }
    });

    // Handle form submission
    $('#bookingForm').submit(function(e) {
        e.preventDefault();
        $('#errorMessage').addClass('hidden');
        $('#loadingSpinner').removeClass('hidden');
        $('#submitButton').prop('disabled', true);

        $.ajax({
            url: 'booking?action=bookAppointment',
            method: 'POST',
            data: $(this).serialize(),
            dataType: 'json',
            success: function(response) {
                $('#loadingSpinner').addClass('hidden');
                if (response.success) {
                    alert(response.message);
                    window.location.href = 'booking-confirm?action=pre-confirm';
                } else {
                    $('#errorMessage').text(response.message).removeClass('hidden');
                    $('#submitButton').prop('disabled', false);
                }
            },
            error: function() {
                $('#loadingSpinner').addClass('hidden');
                $('#errorMessage').text('Đã có lỗi xảy ra khi đặt lịch').removeClass('hidden');
                $('#submitButton').prop('disabled', false);
            }
        });
    });
});
</script>

<jsp:include page="./components/footer.jsp" />