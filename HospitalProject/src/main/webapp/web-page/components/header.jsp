<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Bệnh viện Đa khoa Tâm Đức - Chăm sóc sức khỏe toàn diện</title>
    <link rel="stylesheet" href="css/style.css" />
    <!-- CSS riêng (nếu có) -->
    <%
        String pageCss = (String) request.getAttribute("pageCss");
        if (pageCss != null && !pageCss.isEmpty()) {
    %>
        <link rel="stylesheet" href="<%= pageCss %>" />
    <%
        }
    %>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        primary: {
                            50: '#eff6ff',
                            500: '#3b82f6',
                            600: '#2563eb',
                            700: '#1d4ed8',
                        }
                    }
                }
            }
        }
    </script>
    <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
    <style>
        /* Custom styles for dropdown if needed beyond Tailwind */
    </style>
</head>
<body>
    <header class="bg-white shadow-lg sticky top-0 z-50">
        <div class="container mx-auto px-0 py-2">
            <div class="flex items-center justify-between h-16">
                <!-- Logo -->
                <a href="index.jsp" class="flex items-center space-x-3 group">
                    <div class="p-2 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl shadow-md group-hover:shadow-lg transition-all duration-300 transform group-hover:scale-105">
                        <i class="bx bx-heart text-white text-xl"></i>
                    </div>
                    <span class="text-xl font-bold text-gray-900">Bệnh viện Tâm Đức</span>
                </a>

                <!-- Desktop Navigation -->
                <nav class="hidden md:flex items-center space-x-8">
                    <a href="index.jsp" class="flex items-center space-x-1 text-gray-700 hover:text-blue-600 font-medium transition duration-200 ${"index".equals(request.getAttribute("activePage")) ? 'text-blue-600 border-b-2 border-blue-600' : ''}">
                        <i class="bx bx-home-alt"></i>
                        <span>Trang chủ</span>
                    </a>
                    <a href="specializations" class="flex items-center space-x-1 text-gray-700 hover:text-blue-600 font-medium transition duration-200 ${"departments".equals(request.getAttribute("activePage")) ? 'text-blue-600 border-b-2 border-blue-600' : ''}">
                        <i class="bx bx-clinic"></i>
                        <span>Chuyên khoa</span>
                    </a>
                    <a href="doctors.jsp" class="flex items-center space-x-1 text-gray-700 hover:text-blue-600 font-medium transition duration-200">
                        <i class="bx bx-user-plus"></i>
                        <span>Bác sĩ</span>
                    </a>
                    <a href="blog.jsp" class="flex items-center space-x-1 text-gray-700 hover:text-blue-600 font-medium transition duration-200">
                        <i class="bx bx-news"></i>
                        <span>Tin tức</span>
                    </a>
                    <a href="appointments.jsp" class="flex items-center space-x-1 text-gray-700 hover:text-blue-600 font-medium transition duration-200">
                        <i class="bx bx-calendar-check"></i>
                        <span>Lịch hẹn</span>
                    </a>
                </nav>

                <!-- Desktop Actions -->
                <div class="hidden md:flex items-center space-x-4">
                    <!-- User Menu -->
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <div class="relative">
                                <button onclick="toggleDropdown()" class="flex items-center space-x-2 bg-gray-100 hover:bg-gray-200 px-4 py-2 rounded-full transition duration-200">
                                    <i class="bx bx-user text-blue-600"></i>
                                    <span class="text-sm font-medium text-gray-700">${sessionScope.user.fullName}</span>
                                    <i class="bx bx-chevron-down text-gray-500 transition-transform duration-200"></i>
                                </button>
                                <div id="userDropdown" class="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-100 opacity-0 invisible transform scale-95 transition-all duration-200 origin-top-right z-50">
                                    <a href="${pageContext.request.contextPath}/ViewProfileServlet" class="flex items-center px-4 py-3 text-gray-700 hover:bg-gray-50 rounded-t-xl transition duration-200">
                                        <i class="bx bx-user-circle mr-3 text-blue-600"></i>
                                        Hồ sơ
                                    </a>
                                    <a href="appointments" class="flex items-center px-4 py-3 text-gray-700 hover:bg-gray-50 transition duration-200">
                                        <i class="bx bx-calendar-check mr-3 text-blue-600"></i>
                                        Lịch hẹn của tôi
                                    </a>
                                    <a href="auth?action=logout" class="flex items-center px-4 py-3 text-gray-700 hover:bg-gray-50 rounded-b-xl transition duration-200">
                                        <i class="bx bx-log-out mr-3 text-red-600"></i>
                                        Đăng xuất
                                    </a>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <a href="./auth" class="inline-flex items-center px-6 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white font-semibold rounded-full shadow-md hover:shadow-lg hover:from-green-600 hover:to-green-700 transition-all duration-300 transform hover:-translate-y-0.5">
                                <i class="bx bx-log-in mr-2"></i>
                                Đăng nhập
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Mobile Menu Toggle -->
                <button class="md:hidden flex items-center p-2 rounded-full bg-gray-100 hover:bg-gray-200 transition duration-200" id="menuToggle" aria-label="Toggle menu">
                    <i class="bx bx-menu text-gray-600 text-xl"></i>
                </button>
            </div>
        </div>

        <!-- Mobile Navigation -->
        <div id="mobileNav" class="md:hidden hidden bg-white border-t border-gray-100 shadow-md">
            <div class="px-4 py-4 space-y-2">
                <a href="index.jsp" class="flex items-center space-x-3 text-gray-700 hover:text-blue-600 font-medium transition duration-200 ${"index".equals(request.getAttribute("activePage")) ? 'text-blue-600' : ''}">
                    <i class="bx bx-home-alt text-blue-600"></i>
                    <span>Trang chủ</span>
                </a>
                <a href="departments.jsp" class="flex items-center space-x-3 text-gray-700 hover:text-blue-600 font-medium transition duration-200 ${"departments".equals(request.getAttribute("activePage")) ? 'text-blue-600' : ''}">
                    <i class="bx bx-clinic text-blue-600"></i>
                    <span>Chuyên khoa</span>
                </a>
                <a href="doctors.jsp" class="flex items-center space-x-3 text-gray-700 hover:text-blue-600 font-medium transition duration-200">
                    <i class="bx bx-user-plus"></i>
                    <span>Bác sĩ</span>
                </a>
                <a href="blog.jsp" class="flex items-center space-x-3 text-gray-700 hover:text-blue-600 font-medium transition duration-200">
                    <i class="bx bx-news"></i>
                    <span>Tin tức</span>
                </a>
                <a href="appointments.jsp" class="flex items-center space-x-3 text-gray-700 hover:text-blue-600 font-medium transition duration-200">
                    <i class="bx bx-calendar-check"></i>
                    <span>Lịch hẹn</span>
                </a>
            </div>
            <div class="px-4 pb-4 border-t border-gray-100">
                <a href="booking.jsp" class="block w-full text-center px-6 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-semibold rounded-full shadow-md hover:shadow-lg hover:from-blue-700 hover:to-indigo-700 transition-all duration-300 mb-3">
                    <i class="bx bx-calendar-plus mr-2"></i>
                    Đặt lịch khám
                </a>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <div class="text-center text-sm text-gray-600 mb-2">Xin chào, ${sessionScope.user.fullName}</div>
                        <a href="profile.jsp" class="block w-full text-left px-4 py-2 text-gray-700 hover:bg-gray-50 rounded-lg transition duration-200">
                            <i class="bx bx-user-circle mr-3 text-blue-600 inline"></i> Hồ sơ
                        </a>
                        <a href="my-appointments.jsp" class="block w-full text-left px-4 py-2 text-gray-700 hover:bg-gray-50 rounded-lg transition duration-200">
                            <i class="bx bx-calendar-check mr-3 text-blue-600 inline"></i> Lịch hẹn của tôi
                        </a>
                        <a href="auth?action=logout" class="block w-full text-left px-4 py-2 text-gray-700 hover:bg-gray-50 rounded-lg transition duration-200">
                            <i class="bx bx-log-out mr-3 text-red-600 inline"></i> Đăng xuất
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="auth" class="block w-full text-center px-6 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white font-semibold rounded-full shadow-md hover:shadow-lg hover:from-green-600 hover:to-green-700 transition-all duration-300">
                            <i class="bx bx-log-in mr-2"></i>
                            Đăng nhập
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </header>

    
