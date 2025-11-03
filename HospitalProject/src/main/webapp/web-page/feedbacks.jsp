<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />

<div class="min-h-screen bg-gray-50 py-8">
    <div class="container mx-auto px-4 max-w-7xl">
        <!-- Header -->
        <div class="mb-6">
            <h1 class="text-3xl font-bold text-gray-900 mb-2">Quản lý Đánh giá</h1>
            <p class="text-gray-600">Xem và quản lý tất cả phản hồi từ người dùng</p>
        </div>

        <!-- Alert Messages -->
        <c:if test="${not empty param.success}">
            <div class="mb-4 p-4 bg-green-50 border border-green-200 rounded-lg flex items-center gap-2">
                <i class='bx bx-check-circle text-green-600 text-xl'></i>
                <span class="text-green-800">${param.success}</span>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg flex items-center gap-2">
                <i class='bx bx-error-circle text-red-600 text-xl'></i>
                <span class="text-red-800">${param.error}</span>
            </div>
        </c:if>

        <!-- Filters -->
        <div class="bg-white rounded-lg shadow-sm p-6 mb-6">
            <form method="get" action="feedback" class="flex flex-wrap gap-4 items-end">
                <input type="hidden" name="action" value="list" />
                <div class="flex-1 min-w-[200px]">
                    <label class="block text-sm font-medium text-gray-700 mb-1">Trạng thái</label>
                    <select name="status" 
                            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                        <option value="" ${empty status ? 'selected' : ''}>Tất cả</option>
                        <option value="Active" ${status == 'Active' ? 'selected' : ''}>Hiển thị</option>
                        <option value="Hidden" ${status == 'Hidden' ? 'selected' : ''}>Ẩn</option>
                    </select>
                </div>
                <button type="submit" 
                        class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition duration-200 flex items-center gap-2">
                    <i class='bx bx-filter'></i>
                    <span>Lọc</span>
                </button>
            </form>
        </div>

        <!-- Table (Desktop) -->
        <div class="bg-white rounded-lg shadow-sm overflow-hidden hidden md:block">
            <div class="overflow-x-auto">
                <table class="w-full">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Người dùng</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Đánh giá</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nhận xét</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <c:forEach var="f" items="${feedbacks}">
                            <tr class="hover:bg-gray-50 transition-colors">
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">#${f.id}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                                    <c:choose>
                                        <c:when test="${f.user != null && f.user.fullName != null}">
                                            <div class="flex items-center gap-2">
                                                <i class='bx bx-user-circle text-gray-400'></i>
                                                <span><c:out value="${f.user.fullName}"/></span>
                                            </div>
                                        </c:when>
                                        <c:when test="${f.userId != null}">
                                            <span class="text-gray-600">User #${f.userId}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-gray-400">N/A</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <c:if test="${f.rating != null && f.rating > 0}">
                                        <div class="flex items-center gap-1">
                                            <c:forEach begin="1" end="5" var="star">
                                                <i class='bx ${star <= f.rating ? "bxs-star text-yellow-400" : "bx-star text-gray-300"}'></i>
                                            </c:forEach>
                                            <span class="ml-2 text-sm font-medium text-gray-700">${f.rating}/5</span>
                                        </div>
                                    </c:if>
                                    <c:if test="${f.rating == null || f.rating == 0}">
                                        <span class="text-gray-400">N/A</span>
                                    </c:if>
                                </td>
                                <td class="px-6 py-4 text-sm text-gray-700 max-w-md">
                                    <div class="truncate">
                                        <c:out value="${f.comment != null && !empty f.comment ? f.comment : 'Không có nhận xét'}"/>
                                    </div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <c:choose>
                                        <c:when test="${f.status == 'Active'}">
                                            <span class="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs font-medium">Hiển thị</span>
                                        </c:when>
                                        <c:when test="${f.status == 'Hidden'}">
                                            <span class="px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-xs font-medium">Ẩn</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-xs font-medium">
                                                <c:out value="${f.status != null ? f.status : 'N/A'}"/>
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                    <div class="flex items-center gap-3">
                                        <a href="feedback?action=view&id=${f.id}" 
                                           class="text-blue-600 hover:text-blue-900 flex items-center gap-1">
                                            <i class='bx bx-show'></i>
                                            <span>Xem</span>
                                        </a>
                                        <a href="feedback?action=edit&id=${f.id}" 
                                           class="text-yellow-600 hover:text-yellow-900 flex items-center gap-1">
                                            <i class='bx bx-edit'></i>
                                            <span>Sửa</span>
                                        </a>
                                        <form action="feedback" method="post" class="inline" 
                                              onsubmit="return confirm('Bạn có chắc muốn xóa đánh giá này?');">
                                            <input type="hidden" name="action" value="delete" />
                                            <input type="hidden" name="id" value="${f.id}" />
                                            <button type="submit" 
                                                    class="text-red-600 hover:text-red-900 flex items-center gap-1">
                                                <i class='bx bx-trash'></i>
                                                <span>Xóa</span>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty feedbacks}">
                            <tr>
                                <td colspan="6" class="px-6 py-12 text-center">
                                    <div class="flex flex-col items-center gap-2">
                                        <i class='bx bx-message-square-dots text-4xl text-gray-400'></i>
                                        <span class="text-gray-500">Không có đánh giá nào</span>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Cards (Mobile) -->
        <div class="md:hidden space-y-4">
            <c:forEach var="f" items="${feedbacks}">
                <div class="bg-white rounded-lg shadow-sm p-4 border-l-4 border-blue-500">
                    <div class="flex justify-between items-start mb-3">
                        <div>
                            <span class="text-xs text-gray-500">#${f.id}</span>
                            <h3 class="font-semibold text-gray-900 mt-1">
                                <c:choose>
                                    <c:when test="${f.user != null && f.user.fullName != null}">
                                        <c:out value="${f.user.fullName}"/>
                                    </c:when>
                                    <c:when test="${f.userId != null}">
                                        User #${f.userId}
                                    </c:when>
                                    <c:otherwise>
                                        N/A
                                    </c:otherwise>
                                </c:choose>
                            </h3>
                        </div>
                        <c:choose>
                            <c:when test="${f.status == 'Active'}">
                                <span class="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs font-medium">Hiển thị</span>
                            </c:when>
                            <c:otherwise>
                                <span class="px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-xs font-medium">Ẩn</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <div class="mb-3">
                        <c:if test="${f.rating != null && f.rating > 0}">
                            <div class="flex items-center gap-1 mb-2">
                                <c:forEach begin="1" end="5" var="star">
                                    <i class='bx ${star <= f.rating ? "bxs-star text-yellow-400" : "bx-star text-gray-300"}'></i>
                                </c:forEach>
                                <span class="ml-2 text-sm font-medium text-gray-700">${f.rating}/5</span>
                            </div>
                        </c:if>
                        <p class="text-sm text-gray-700 line-clamp-2">
                            <c:out value="${f.comment != null && !empty f.comment ? f.comment : 'Không có nhận xét'}"/>
                        </p>
                    </div>
                    
                    <div class="flex gap-2 pt-3 border-t border-gray-100">
                        <a href="feedback?action=view&id=${f.id}" 
                           class="flex-1 px-3 py-2 bg-blue-50 text-blue-600 rounded-lg text-center text-sm font-medium hover:bg-blue-100">
                            <i class='bx bx-show'></i> Xem
                        </a>
                        <a href="feedback?action=edit&id=${f.id}" 
                           class="flex-1 px-3 py-2 bg-yellow-50 text-yellow-600 rounded-lg text-center text-sm font-medium hover:bg-yellow-100">
                            <i class='bx bx-edit'></i> Sửa
                        </a>
                        <form action="feedback" method="post" class="flex-1"
                              onsubmit="return confirm('Bạn có chắc muốn xóa đánh giá này?');">
                            <input type="hidden" name="action" value="delete" />
                            <input type="hidden" name="id" value="${f.id}" />
                            <button type="submit" 
                                    class="w-full px-3 py-2 bg-red-50 text-red-600 rounded-lg text-sm font-medium hover:bg-red-100">
                                <i class='bx bx-trash'></i> Xóa
                            </button>
                        </form>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty feedbacks}">
                <div class="bg-white rounded-lg shadow-sm p-12 text-center">
                    <i class='bx bx-message-square-dots text-5xl text-gray-400 mb-4'></i>
                    <p class="text-gray-500">Không có đánh giá nào</p>
                </div>
            </c:if>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <div class="mt-6 flex justify-center items-center gap-2">
                <c:forEach var="p" begin="1" end="${totalPages}">
                    <c:choose>
                        <c:when test="${p == currentPage}">
                            <span class="px-4 py-2 bg-blue-600 text-white rounded-lg font-medium">${p}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="feedback?action=list&page=${p}&status=${status}" 
                               class="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition duration-200">
                                ${p}
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="./components/footer.jsp" />


