<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="./components/header.jsp" />

<div class="min-h-screen bg-gray-50 py-8">
    <div class="container mx-auto px-4 max-w-4xl">
        <!-- Header -->
        <div class="mb-6">
            <div class="flex items-center gap-3 mb-2">
                <a href="notifications?action=list" class="text-gray-500 hover:text-gray-700 transition duration-200">
                    <i class='bx bx-arrow-back text-2xl'></i>
                </a>
                <div class="flex items-center gap-3">
                    <div class="p-3 bg-blue-100 rounded-lg">
                        <i class='bx bx-bell text-2xl text-blue-600'></i>
                    </div>
                    <div>
                        <h1 class="text-3xl font-bold text-gray-900">Thông báo #${notification.id}</h1>
                        <p class="text-gray-600 mt-1">Chi tiết thông báo</p>
                    </div>
                </div>
            </div>
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

        <!-- Main Card -->
        <div class="bg-white rounded-lg shadow-sm overflow-hidden">
            <!-- Card Header -->
            <div class="bg-gradient-to-r from-blue-600 to-blue-700 px-6 py-4">
                <div class="flex items-center justify-between">
                    <div class="flex items-center gap-3">
                        <i class='bx bx-notification text-white text-2xl'></i>
                        <div>
                            <h2 class="text-xl font-semibold text-white">Thông tin thông báo</h2>
                            <p class="text-blue-100 text-sm">ID: #${notification.id}</p>
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${notification.status == 'sent'}">
                            <span class="px-3 py-1 bg-blue-500 text-white text-sm font-medium rounded-full flex items-center gap-1">
                                <i class='bx bx-time-five'></i>
                                Đã gửi
                            </span>
                        </c:when>
                        <c:when test="${notification.status == 'read'}">
                            <span class="px-3 py-1 bg-green-500 text-white text-sm font-medium rounded-full flex items-center gap-1">
                                <i class='bx bx-check'></i>
                                Đã đọc
                            </span>
                        </c:when>
                        <c:when test="${notification.status == 'failed'}">
                            <span class="px-3 py-1 bg-red-500 text-white text-sm font-medium rounded-full flex items-center gap-1">
                                <i class='bx bx-x'></i>
                                Thất bại
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="px-3 py-1 bg-gray-500 text-white text-sm font-medium rounded-full">
                                ${notification.status != null ? notification.status : 'N/A'}
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Card Body -->
            <div class="p-6 space-y-6">
                <!-- Information Grid -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Type -->
                    <div class="bg-gray-50 rounded-lg p-4 border border-gray-200">
                        <div class="flex items-center gap-2 mb-2">
                            <i class='bx bx-category text-blue-600 text-xl'></i>
                            <label class="text-sm font-semibold text-gray-700">Loại thông báo</label>
                        </div>
                        <div class="mt-2">
                            <c:choose>
                                <c:when test="${notification.type == 'general'}">
                                    <span class="inline-flex items-center gap-1 px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
                                        <i class='bx bx-info-circle'></i>
                                        Thông báo chung
                                    </span>
                                </c:when>
                                <c:when test="${notification.type == 'appointment_reminder'}">
                                    <span class="inline-flex items-center gap-1 px-3 py-1 bg-purple-100 text-purple-800 rounded-full text-sm font-medium">
                                        <i class='bx bx-calendar'></i>
                                        Nhắc nhở lịch hẹn
                                    </span>
                                </c:when>
                                <c:when test="${notification.type == 're_examination'}">
                                    <span class="inline-flex items-center gap-1 px-3 py-1 bg-orange-100 text-orange-800 rounded-full text-sm font-medium">
                                        <i class='bx bx-refresh'></i>
                                        Tái khám
                                    </span>
                                </c:when>
                                <c:when test="${notification.type == 'otp'}">
                                    <span class="inline-flex items-center gap-1 px-3 py-1 bg-yellow-100 text-yellow-800 rounded-full text-sm font-medium">
                                        <i class='bx bx-key'></i>
                                        Mã OTP
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-gray-700 font-medium">
                                        <c:out value="${notification.type != null && !empty notification.type ? notification.type : 'Not available'}"/>
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Sent At -->
                    <div class="bg-gray-50 rounded-lg p-4 border border-gray-200">
                        <div class="flex items-center gap-2 mb-2">
                            <i class='bx bx-time text-blue-600 text-xl'></i>
                            <label class="text-sm font-semibold text-gray-700">Thời gian gửi</label>
                        </div>
                        <div class="mt-2">
                            <c:choose>
                                <c:when test="${notification.sentAt != null}">
                                    <p class="text-gray-800 font-medium text-lg">
                                        ${notification.sentAt}
                                    </p>
                                    <p class="text-xs text-gray-500 mt-1">
                                        <i class='bx bx-calendar'></i>
                                        Thời gian đã gửi
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-gray-500">Not available</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- User ID -->
                    <div class="bg-gray-50 rounded-lg p-4 border border-gray-200">
                        <div class="flex items-center gap-2 mb-2">
                            <i class='bx bx-user text-blue-600 text-xl'></i>
                            <label class="text-sm font-semibold text-gray-700">Người nhận</label>
                        </div>
                        <div class="mt-2">
                            <p class="text-gray-800 font-medium">
                                ${notification.userId != null ? 'User ID: ' : ''}${notification.userId != null ? notification.userId : 'Not available'}
                            </p>
                        </div>
                    </div>

                    <!-- Status (Duplicate but with more details) -->
                    <div class="bg-gray-50 rounded-lg p-4 border border-gray-200">
                        <div class="flex items-center gap-2 mb-2">
                            <i class='bx bx-info-circle text-blue-600 text-xl'></i>
                            <label class="text-sm font-semibold text-gray-700">Trạng thái</label>
                        </div>
                        <div class="mt-2">
                            <c:choose>
                                <c:when test="${notification.status == 'sent'}">
                                    <span class="inline-flex items-center gap-1 px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
                                        <i class='bx bx-time-five'></i>
                                        Đã gửi
                                    </span>
                                </c:when>
                                <c:when test="${notification.status == 'read'}">
                                    <span class="inline-flex items-center gap-1 px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-medium">
                                        <i class='bx bx-check'></i>
                                        Đã đọc
                                    </span>
                                </c:when>
                                <c:when test="${notification.status == 'failed'}">
                                    <span class="inline-flex items-center gap-1 px-3 py-1 bg-red-100 text-red-800 rounded-full text-sm font-medium">
                                        <i class='bx bx-x'></i>
                                        Thất bại
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-gray-500">
                                        <c:out value="${notification.status != null ? notification.status : 'Not available'}"/>
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- Content Section -->
                <div class="border-t border-gray-200 pt-6">
                    <div class="flex items-center gap-2 mb-4">
                        <i class='bx bx-message-dots text-blue-600 text-xl'></i>
                        <label class="text-lg font-semibold text-gray-700">Nội dung thông báo</label>
                    </div>
                    <div class="bg-gray-50 rounded-lg p-6 border border-gray-200">
                        <div class="prose max-w-none">
                            <p class="text-gray-800 whitespace-pre-wrap leading-relaxed">
                                <c:choose>
                                    <c:when test="${notification.content != null && !empty notification.content}">
                                        <c:out value="${notification.content}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-gray-400 italic">No content available</span>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Card Footer -->
            <div class="bg-gray-50 px-6 py-4 border-t border-gray-200">
                <div class="flex flex-col sm:flex-row gap-3 justify-end">
                    <a href="notifications?action=list"
                       class="inline-flex items-center justify-center gap-2 px-6 py-3 bg-gray-600 text-white rounded-lg font-semibold hover:bg-gray-700 transition duration-200 shadow-md hover:shadow-lg">
                        <i class='bx bx-arrow-back text-lg'></i>
                        <span>Quay lại danh sách</span>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="./components/footer.jsp" />


