<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />

<div class="min-h-screen bg-gray-50 py-8">
    <div class="container mx-auto px-4 max-w-7xl">
        <!-- Header -->
        <div class="mb-6">
            <h1 class="text-3xl font-bold text-gray-900 mb-2">Quản lý Thông báo</h1>
            <p class="text-gray-600">Xem và quản lý tất cả thông báo của bạn</p>
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

        <!-- Filters and Actions -->
        <div class="bg-white rounded-lg shadow-sm p-6 mb-6">
            <form method="get" action="notifications" class="flex flex-wrap gap-4 items-end mb-4">
                <input type="hidden" name="action" value="list" />
                <div class="flex-1 min-w-[200px]">
                    <label class="block text-sm font-medium text-gray-700 mb-1">Loại thông báo</label>
                    <input type="text" name="type" value="${type}" 
                           placeholder="VD: system, reminder" 
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                </div>
                <div class="flex-1 min-w-[200px]">
                    <label class="block text-sm font-medium text-gray-700 mb-1">Trạng thái</label>
                    <select name="status" 
                            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                        <option value="" ${empty status ? 'selected' : ''}>Tất cả</option>
                        <option value="unread" ${status == 'unread' ? 'selected' : ''}>Chưa đọc</option>
                        <option value="read" ${status == 'read' ? 'selected' : ''}>Đã đọc</option>
                        <option value="archived" ${status == 'archived' ? 'selected' : ''}>Đã lưu trữ</option>
                    </select>
                </div>
                <button type="submit" 
                        class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition duration-200 flex items-center gap-2">
                    <i class='bx bx-filter'></i>
                    <span>Lọc</span>
                </button>
            </form>
            
            <div class="flex flex-wrap gap-3">
                <c:if test="${sessionScope.user != null}">
                    <form method="post" action="notifications" class="inline">
                        <input type="hidden" name="action" value="markAllRead" />
                        <button type="submit" 
                                class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition duration-200 flex items-center gap-2">
                            <i class='bx bx-check-double'></i>
                            <span>Đánh dấu tất cả đã đọc</span>
                        </button>
                    </form>
                </c:if>
                <c:if test="${sessionScope.user != null && sessionScope.user.roleId == 1}">
                    <a href="notifications?action=send" 
                       class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition duration-200 flex items-center gap-2 inline-block">
                        <i class='bx bx-plus-circle'></i>
                        <span>Gửi thông báo</span>
                    </a>
                </c:if>
            </div>
        </div>

        <!-- Table (Desktop) -->
        <div class="bg-white rounded-lg shadow-sm overflow-hidden hidden md:block">
            <div class="overflow-x-auto">
                <table class="w-full">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Người dùng</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Loại</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nội dung</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thời gian</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <c:forEach var="n" items="${notifications}">
                            <tr class="hover:bg-gray-50 transition-colors">
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">#${n.id}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                                    ${n.userId != null ? n.userId : '<span class="text-gray-400">N/A</span>'}
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                                    <span class="px-2 py-1 bg-purple-100 text-purple-800 rounded-full text-xs font-medium">
                                        <c:out value="${n.type != null && !empty n.type ? n.type : 'N/A'}"/>
                                    </span>
                                </td>
                                <td class="px-6 py-4 text-sm text-gray-700 max-w-xs truncate">
                                    <c:out value="${n.content != null && !empty n.content ? n.content : 'Không có nội dung'}"/>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <c:choose>
                                        <c:when test="${n.status == 'unread'}">
                                            <span class="px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs font-medium">Chưa đọc</span>
                                        </c:when>
                                        <c:when test="${n.status == 'read'}">
                                            <span class="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs font-medium">Đã đọc</span>
                                        </c:when>
                                        <c:when test="${n.status == 'archived'}">
                                            <span class="px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-xs font-medium">Đã lưu trữ</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-xs font-medium">N/A</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    ${n.sentAt != null ? n.sentAt : 'N/A'}
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                    <div class="flex items-center gap-2">
                                        <a href="notifications?action=view&id=${n.id}" 
                                           class="text-blue-600 hover:text-blue-900 flex items-center gap-1">
                                            <i class='bx bx-show'></i>
                                            <span>Xem</span>
                                        </a>
                                        <c:if test="${n.status != 'read'}">
                                            <form action="notifications" method="post" class="inline">
                                                <input type="hidden" name="action" value="updateStatus" />
                                                <input type="hidden" name="id" value="${n.id}" />
                                                <input type="hidden" name="status" value="read" />
                                                <button type="submit" 
                                                        class="text-green-600 hover:text-green-900 flex items-center gap-1">
                                                    <i class='bx bx-check'></i>
                                                    <span>Đã đọc</span>
                                                </button>
                                            </form>
                                        </c:if>
                                        <c:if test="${n.status != 'archived'}">
                                            <form action="notifications" method="post" class="inline">
                                                <input type="hidden" name="action" value="updateStatus" />
                                                <input type="hidden" name="id" value="${n.id}" />
                                                <input type="hidden" name="status" value="archived" />
                                                <button type="submit" 
                                                        class="text-gray-600 hover:text-gray-900 flex items-center gap-1">
                                                    <i class='bx bx-archive'></i>
                                                    <span>Lưu trữ</span>
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty notifications}">
                            <tr>
                                <td colspan="7" class="px-6 py-12 text-center">
                                    <div class="flex flex-col items-center gap-2">
                                        <i class='bx bx-bell-off text-4xl text-gray-400'></i>
                                        <span class="text-gray-500">Không có thông báo nào</span>
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
            <c:forEach var="n" items="${notifications}">
                <div class="bg-white rounded-lg shadow-sm p-4 border-l-4 ${n.status == 'unread' ? 'border-red-500' : 'border-gray-300'}">
                    <div class="flex justify-between items-start mb-2">
                        <div class="flex-1">
                            <span class="text-xs text-gray-500">#${n.id}</span>
                            <h3 class="font-semibold text-gray-900 mt-1">
                                <c:out value="${n.type != null && !empty n.type ? n.type : 'N/A'}"/>
                            </h3>
                        </div>
                        <c:choose>
                            <c:when test="${n.status == 'unread'}">
                                <span class="px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs font-medium">Chưa đọc</span>
                            </c:when>
                            <c:when test="${n.status == 'read'}">
                                <span class="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs font-medium">Đã đọc</span>
                            </c:when>
                            <c:otherwise>
                                <span class="px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-xs font-medium">Đã lưu trữ</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <p class="text-sm text-gray-700 mb-3">
                        <c:out value="${n.content != null && !empty n.content ? n.content : 'Không có nội dung'}"/>
                    </p>
                    <div class="flex justify-between items-center text-xs text-gray-500 mb-3">
                        <span>User: ${n.userId != null ? n.userId : 'N/A'}</span>
                        <span>${n.sentAt != null ? n.sentAt : 'N/A'}</span>
                    </div>
                    <div class="flex gap-2">
                        <a href="notifications?action=view&id=${n.id}" 
                           class="flex-1 px-3 py-2 bg-blue-50 text-blue-600 rounded-lg text-center text-sm font-medium hover:bg-blue-100">
                            <i class='bx bx-show'></i> Xem
                        </a>
                        <c:if test="${n.status != 'read'}">
                            <form action="notifications" method="post" class="flex-1">
                                <input type="hidden" name="action" value="updateStatus" />
                                <input type="hidden" name="id" value="${n.id}" />
                                <input type="hidden" name="status" value="read" />
                                <button type="submit" 
                                        class="w-full px-3 py-2 bg-green-50 text-green-600 rounded-lg text-sm font-medium hover:bg-green-100">
                                    <i class='bx bx-check'></i> Đã đọc
                                </button>
                            </form>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty notifications}">
                <div class="bg-white rounded-lg shadow-sm p-12 text-center">
                    <i class='bx bx-bell-off text-5xl text-gray-400 mb-4'></i>
                    <p class="text-gray-500">Không có thông báo nào</p>
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
                            <a href="notifications?action=list&page=${p}&type=${type}&status=${status}" 
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


