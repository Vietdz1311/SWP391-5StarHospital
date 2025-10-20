<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    request.setAttribute("pageTitle", "Chuyên khoa - Bệnh viện Tâm Đức");
    request.setAttribute("pageCss", "css/departments.css");
    request.setAttribute("activePage", "departments");
%>
<jsp:include page="./components/header.jsp" />
<!-- Page Header -->
<section class="relative bg-gradient-to-r from-blue-50 to-indigo-100 py-20 overflow-hidden">
  <div class="absolute inset-0 bg-[url('data:image/svg+xml,%3Csvg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="none" fill-rule="evenodd"%3E%3Cg fill="%239C92AC" fill-opacity="0.05"%3E%3Ccircle cx="30" cy="30" r="2"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')] opacity-50"></div>
  <div class="container mx-auto px-4 text-center relative z-10">
    <h1 class="text-5xl md:text-6xl font-bold text-gray-900 mb-4 bg-gradient-to-r from-blue-600 to-indigo-700 bg-clip-text text-transparent">Chuyên khoa</h1>
    <p class="text-xl text-gray-600 max-w-2xl mx-auto">Khám phá các chuyên khoa với đội ngũ bác sĩ giàu kinh nghiệm, công nghệ hiện đại và dịch vụ chăm sóc tận tâm.</p>
  </div>
</section>
<!-- Departments Grid -->
<section class="py-20 bg-white">
  <div class="container mx-auto px-4">
    <div class="mb-12">
      <form action="specializations" method="get" class="bg-white rounded-2xl shadow-lg p-6 flex flex-col lg:flex-row gap-4 items-end">
        <div class="flex-1 flex flex-col sm:flex-row gap-4">
          <div class="flex-1 relative">
            <input type="text" name="search" placeholder="Tìm kiếm chuyên khoa..." value="${search}" class="w-full pl-12 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200">
            <i class='bx bx-search absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400'></i>
          </div>
          <select name="filterStatus" class="w-full sm:w-auto px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200">
            <option value="">Tất cả trạng thái</option>
            <option value="Active" ${filterStatus == 'Active' ? 'selected' : ''}>Hoạt động</option>
            <option value="Inactive" ${filterStatus == 'Inactive' ? 'selected' : ''}>Không hoạt động</option>
          </select>
          <select name="sortBy" class="w-full sm:w-auto px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200">
            <option value="">Sắp xếp theo</option>
            <option value="specialization_name ASC" ${sortBy == 'specialization_name ASC' ? 'selected' : ''}>Tên A-Z</option>
            <option value="specialization_name DESC" ${sortBy == 'specialization_name DESC' ? 'selected' : ''}>Tên Z-A</option>
          </select>
        </div>
        <button type="submit" class="bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-8 py-3 rounded-xl hover:from-blue-700 hover:to-indigo-700 transition duration-300 font-medium shadow-md hover:shadow-lg transform hover:-translate-y-0.5">
          <i class='bx bx-filter-alt mr-2'></i>Lọc & Tìm
        </button>
      </form>
    </div>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12 mt-6">
      <c:forEach var="dept" items="${specializations}">
        <div class="group bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-xl transition-all duration-300 transform hover:-translate-y-2">
          <div class="p-8">
            <div class="flex items-start mb-6">
              <div class="p-3 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-xl mr-4 flex-shrink-0 group-hover:from-blue-200 group-hover:to-indigo-200 transition duration-300">
                <i class='bx ${dept.specializationName == "Tim mạch" ? "bxs-heart" : dept.specializationName == "Nội khoa" ? "bxs-brain" : dept.specializationName == "Nhi khoa" ? "bxs-baby-carriage" : dept.specializationName == "Ngoại khoa" ? "bxs-injection" : dept.specializationName == "Sản phụ khoa" ? "bxs-female" : dept.specializationName == "Mắt" ? "bxs-low-vision" : dept.specializationName == "Tai Mũi Họng" ? "bxs-face-mask" : "bxs-droplet"} text-3xl text-blue-600 group-hover:text-blue-700 transition duration-300'></i>
              </div>
              <div class="flex-1">
                <h3 class="text-2xl font-bold text-gray-900 mb-2">${dept.specializationName}</h3>
                <span class="inline-block px-3 py-1 rounded-full text-xs font-medium ${dept.status == 'Active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">
                  ${dept.status == 'Active' ? 'Hoạt động' : 'Không hoạt động'}
                </span>
              </div>
            </div>
            <p class="text-gray-600 mb-6 leading-relaxed">${dept.description}</p>
            <div class="space-y-3 mb-6">
              <div class="flex items-center text-gray-500">
                <i class='bx bx-user text-blue-600 mr-3'></i>
                <span class="font-medium">Bác sĩ trưởng: ${dept.headDoctorId}</span>
              </div>
              <div class="flex items-center text-gray-500">
                <i class='bx bx-time text-blue-600 mr-3'></i>
                <span class="font-medium">Giờ làm việc: Linh hoạt</span>
              </div>
            </div>
            <a href="specializations?action=details&id=${dept.id}" class="inline-flex items-center text-blue-600 hover:text-blue-800 font-semibold transition duration-200">
              Xem chi tiết <i class='bx bx-chevron-right ml-1 transform group-hover:translate-x-1 transition duration-200'></i>
            </a>
          </div>
        </div>
      </c:forEach>
    </div>
    <c:if test="${totalPages > 1}">
      <div class="flex justify-center">
        <nav class="flex space-x-2 bg-white rounded-xl p-2 shadow-sm">
          <c:forEach begin="1" end="${totalPages}" var="i">
            <a href="specializations?page=${i}&search=${search}&filterStatus=${filterStatus}&sortBy=${sortBy}" 
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