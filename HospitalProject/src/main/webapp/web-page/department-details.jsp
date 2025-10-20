<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<!-- Page Header -->
<section class="relative bg-gradient-to-r from-blue-50 to-indigo-100 py-20 overflow-hidden">
  <div class="absolute inset-0 bg-[url('data:image/svg+xml,%3Csvg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="none" fill-rule="evenodd"%3E%3Cg fill="%239C92AC" fill-opacity="0.05"%3E%3Ccircle cx="30" cy="30" r="2"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')] opacity-50"></div>
  <div class="container mx-auto px-4 text-center relative z-10">
    <h1 class="text-5xl md:text-6xl font-bold text-gray-900 mb-4 bg-gradient-to-r from-blue-600 to-indigo-700 bg-clip-text text-transparent">${specialization.specializationName}</h1>
    <p class="text-xl text-gray-600 max-w-3xl mx-auto leading-relaxed">${specialization.description}</p>
  </div>
</section>
<!-- Department Details -->
<section class="py-20 bg-gray-50">
  <div class="container mx-auto px-4">
    <!-- Info Card -->
    <div class="bg-white rounded-2xl shadow-xl max-w-7xl mx-auto mb-16 p-8 lg:p-12">
      <h2 class="text-3xl font-bold text-gray-900 mb-8 flex items-center">
        <i class='bx bx-clinic text-blue-600 mr-3 text-4xl'></i>
        Thông tin chuyên khoa
      </h2>
      <div class="space-y-6">
        <div class="flex items-center text-gray-600">
          <i class='bx bx-check-circle text-blue-600 mr-4 text-2xl'></i>
          <div>
            <p class="font-semibold text-gray-900">Trạng thái</p>
            <span class="inline-block px-4 py-2 rounded-full text-sm font-medium ${specialization.status == 'Active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">
              ${specialization.status == 'Active' ? 'Hoạt động' : 'Không hoạt động'}
            </span>
          </div>
        </div>
        <div class="flex items-center text-gray-600">
          <i class='bx bx-user text-blue-600 mr-4 text-2xl'></i>
          <div>
            <p class="font-semibold text-gray-900">Bác sĩ trưởng</p>
            <p class="text-lg font-medium text-gray-900">${specialization.headDoctor.fullName}</p>
          </div>
        </div>
      </div>
    </div>
    <!-- Doctors List -->
    <div class="max-w-7xl mx-auto">
      <h2 class="text-3xl font-bold text-gray-900 mb-12 flex items-center justify-center">
        <i class='bx bx-user-plus text-blue-600 mr-3 text-4xl'></i>
        Bác sĩ trong chuyên khoa
      </h2>
      <c:if test="${empty doctors}">
        <div class="text-center py-12">
          <i class='bx bx-search-alt text-6xl text-gray-300 mb-4'></i>
          <p class="text-xl text-gray-500">Chưa có bác sĩ nào trong chuyên khoa này.</p>
        </div>
      </c:if>
      <c:if test="${not empty doctors}">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <c:forEach var="doctor" items="${doctors}">
            <div class="group bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-xl transition-all duration-300 transform hover:-translate-y-2">
              <div class="p-6 lg:p-8">
                <div class="flex items-start mb-4">
                  <div class="p-2 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-xl mr-4 flex-shrink-0 group-hover:from-blue-200 group-hover:to-indigo-200 transition duration-300">
                    <i class='bx bx-user-plus text-2xl text-blue-600 group-hover:text-blue-700 transition duration-300'></i>
                  </div>
                  <div class="flex-1">
                    <h3 class="text-xl font-bold text-gray-900 mb-1">${doctor.licenseNumber}</h3>
                    <p class="text-gray-600 text-sm">Kinh nghiệm: <span class="font-medium text-blue-600">${doctor.yearsOfExperience} năm</span></p>
                  </div>
                </div>
                <p class="text-gray-600 mb-6 leading-relaxed text-sm">${doctor.bio}</p>
                <a href="doctors?action=details&id=${doctor.id}" class="inline-flex items-center text-blue-600 hover:text-blue-800 font-semibold transition duration-200">
                  Xem chi tiết <i class='bx bx-chevron-right ml-1 transform group-hover:translate-x-1 transition duration-200'></i>
                </a>
              </div>
            </div>
          </c:forEach>
        </div>
      </c:if>
    </div>
  </div>
</section>
<jsp:include page="./components/footer.jsp" />