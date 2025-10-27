<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<!-- Page Header -->
<section class="relative bg-gradient-to-r from-blue-50 to-indigo-100 py-20 overflow-hidden">
  <div class="absolute inset-0 bg-[url('data:image/svg+xml,%3Csvg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="none" fill-rule="evenodd"%3E%3Cg fill="%239C92AC" fill-opacity="0.05"%3E%3Ccircle cx="30" cy="30" r="2"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')] opacity-50"></div>
  <div class="container mx-auto px-4 text-center relative z-10">
    <h1 class="text-5xl md:text-6xl font-bold text-gray-900 mb-4 bg-gradient-to-r from-blue-600 to-indigo-700 bg-clip-text text-transparent">Bác sĩ</h1>
    <p class="text-xl text-gray-600 max-w-2xl mx-auto">Đội ngũ bác sĩ giàu kinh nghiệm, tận tâm và chuyên môn cao, luôn đồng hành cùng sức khỏe của bạn.</p>
  </div>
</section>
<!-- Doctors Grid -->
<section class="py-20 bg-white">
  <div class="container mx-auto px-4">
    <div class="mb-12">
      <form action="doctors" method="get" class="bg-white rounded-2xl shadow-lg p-6 flex flex-col lg:flex-row gap-4 items-end">
        <div class="flex-1 flex flex-col sm:flex-row gap-4">
          <div class="flex-1 relative">
            <input type="text" name="search" placeholder="Tìm kiếm bác sĩ..." value="${search}" class="w-full pl-12 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200">
            <i class='bx bx-search absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400'></i>
          </div>
          <select name="filterSpecialization" class="w-full sm:w-auto px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200">
            <option value="">Tất cả chuyên khoa</option>
            <c:forEach var="spec" items="${specializations}">
              <option value="${spec.id}" ${filterSpecialization == spec.id ? 'selected' : ''}>${spec.specializationName}</option>
            </c:forEach>
          </select>
          <select name="sortBy" class="w-full sm:w-auto px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200">
            <option value="">Sắp xếp theo</option>
            <option value="years_of_experience DESC" ${sortBy == 'years_of_experience DESC' ? 'selected' : ''}>Kinh nghiệm giảm dần</option>
            <option value="years_of_experience ASC" ${sortBy == 'years_of_experience ASC' ? 'selected' : ''}>Kinh nghiệm tăng dần</option>
          </select>
        </div>
        <button type="submit" class="bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-8 py-3 rounded-xl hover:from-blue-700 hover:to-indigo-700 transition duration-300 font-medium shadow-md hover:shadow-lg transform hover:-translate-y-0.5">
          <i class='bx bx-filter-alt mr-2'></i>Lọc & Tìm
        </button>
      </form>
    </div>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12 mt-6">
      <c:forEach var="doc" items="${doctors}">
        <div class="group bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-xl transition-all duration-300 transform hover:-translate-y-2">
          <div class="relative overflow-hidden">
            <img src="${doc.user.profilePicture != null ? doc.user.profilePicture : 'https://via.placeholder.com/400x300?text=Doctor+Profile'}" alt="${doc.licenseNumber}" class="w-full h-64 object-cover group-hover:scale-105 transition-transform duration-300">
          </div>
          <div class="p-6">
            <h3 class="text-2xl font-bold text-gray-900 mb-2">${doc.licenseNumber}</h3>
            <p class="text-gray-600 mb-1">Chuyên khoa: <span class="font-medium text-blue-600">${doc.specialization.specializationName}</span></p>
            <p class="text-gray-600 mb-6">Kinh nghiệm: <span class="font-medium text-blue-600">${doc.yearsOfExperience} năm</span></p>
            <a href="doctors?action=details&id=${doc.id}" class="inline-flex items-center justify-center w-full px-6 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg hover:from-blue-700 hover:to-indigo-700 transition-all duration-300 transform hover:-translate-y-0.5">
              Xem chi tiết <i class='bx bx-chevron-right ml-2 transform group-hover:translate-x-1 transition duration-200'></i>
            </a>
          </div>
        </div>
      </c:forEach>
    </div>
    <c:if test="${totalPages > 1}">
      <div class="flex justify-center">
        <nav class="flex space-x-2 bg-white rounded-xl p-2 shadow-sm">
          <c:forEach begin="1" end="${totalPages}" var="i">
            <a href="doctors?page=${i}&search=${search}&filterSpecialization=${filterSpecialization}&sortBy=${sortBy}"
               class="px-4 py-2 rounded-lg font-medium transition duration-200 ${currentPage == i ? 'bg-gradient-to-r from-blue-600 to-indigo-600 text-white shadow-md' : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50'}">
              ${i}
            </a>
          </c:forEach>
        </nav>
      </div>
    </c:if>
  </div>
</section>
<jsp:include page="./components/footer.jsp" />