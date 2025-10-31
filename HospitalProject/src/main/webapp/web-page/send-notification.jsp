<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />

<div class="min-h-screen bg-gray-50 py-8">
    <div class="container mx-auto px-4 max-w-2xl">
        <!-- Header -->
        <div class="mb-6">
            <div class="flex items-center gap-3 mb-2">
                <a href="notifications?action=list" class="text-gray-500 hover:text-gray-700 transition duration-200">
                    <i class='bx bx-arrow-back text-2xl'></i>
                </a>
                <h1 class="text-3xl font-bold text-gray-900">Gửi Thông báo</h1>
            </div>
            <p class="text-gray-600">Tạo và gửi thông báo đến người dùng</p>
        </div>

        <!-- Alert Messages -->
        <c:if test="${not empty param.error}">
            <div class="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg flex items-center gap-2">
                <i class='bx bx-error-circle text-red-600 text-xl'></i>
                <span class="text-red-800">${param.error}</span>
            </div>
        </c:if>

        <!-- Form Card -->
        <div class="bg-white rounded-lg shadow-sm p-6 md:p-8">
            <form action="notifications" method="post" class="space-y-6">
                <input type="hidden" name="action" value="send" />

                <!-- Recipient Field -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i class='bx bx-user mr-2 text-blue-600'></i>
                        Người nhận <span class="text-red-500">*</span>
                    </label>
                    <select name="userId" required
                            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 bg-white">
                        <option value="" disabled selected>-- Chọn người dùng --</option>
                        <c:forEach var="u" items="${users}">
                            <option value="${u.id}">${u.fullName} <c:if test="${u.email != null}">(${u.email})</c:if></option>
                        </c:forEach>
                    </select>
                    <p class="mt-1 text-xs text-gray-500">Chọn người dùng sẽ nhận thông báo</p>
                </div>

                <!-- Type Field -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i class='bx bx-category mr-2 text-blue-600'></i>
                        Loại thông báo <span class="text-red-500">*</span>
                    </label>
                    <input type="text" name="type" placeholder="VD: system, reminder, appointment..." required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
                           value="${param.type != null ? param.type : ''}" />
                    <p class="mt-1 text-xs text-gray-500">Nhập loại thông báo (VD: system, reminder, appointment)</p>
                </div>

                <!-- Content Field -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i class='bx bx-message-dots mr-2 text-blue-600'></i>
                        Nội dung <span class="text-red-500">*</span>
                    </label>
                    <textarea name="content" rows="6" required
                              placeholder="Nhập nội dung thông báo chi tiết..."
                              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 resize-y"
                    >${param.content != null ? param.content : ''}</textarea>
                    <p class="mt-1 text-xs text-gray-500">Nội dung thông báo sẽ được hiển thị cho người nhận</p>
                </div>

                <!-- Send Email Checkbox -->
                <div class="flex items-start gap-3 p-4 bg-gray-50 rounded-lg border border-gray-200">
                    <input type="checkbox" name="sendEmail" value="yes" id="sendEmail"
                           class="mt-1 w-5 h-5 text-blue-600 border-gray-300 rounded focus:ring-blue-500 focus:ring-2" />
                    <label for="sendEmail" class="flex-1 cursor-pointer">
                        <div class="flex items-center gap-2">
                            <i class='bx bx-envelope text-indigo-600'></i>
                            <span class="text-sm font-medium text-gray-700">Gửi kèm email</span>
                        </div>
                        <p class="mt-1 text-xs text-gray-500">Thông báo sẽ được gửi qua email nếu người dùng có địa chỉ email hợp lệ</p>
                    </label>
                </div>

                <!-- Action Buttons -->
                <div class="flex flex-col sm:flex-row gap-3 pt-4 border-t border-gray-200">
                    <button type="submit"
                            class="flex-1 px-6 py-3 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition duration-200 flex items-center justify-center gap-2 shadow-md hover:shadow-lg">
                        <i class='bx bx-send text-lg'></i>
                        <span>Gửi thông báo</span>
                    </button>
                    <a href="notifications?action=list"
                       class="flex-1 px-6 py-3 bg-gray-100 text-gray-700 rounded-lg font-semibold hover:bg-gray-200 transition duration-200 flex items-center justify-center gap-2 text-center">
                        <i class='bx bx-x text-lg'></i>
                        <span>Hủy</span>
                    </a>
                </div>
            </form>
        </div>

        <!-- Info Box -->
        <div class="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
            <div class="flex items-start gap-3">
                <i class='bx bx-info-circle text-blue-600 text-xl mt-0.5'></i>
                <div class="flex-1">
                    <h3 class="text-sm font-semibold text-blue-900 mb-1">Lưu ý</h3>
                    <ul class="text-xs text-blue-800 space-y-1 list-disc list-inside">
                        <li>Thông báo sẽ được lưu vào hệ thống và có thể xem trong phần "Thông báo"</li>
                        <li>Nếu chọn "Gửi kèm email", thông báo sẽ được gửi qua email nếu người dùng có địa chỉ email hợp lệ</li>
                        <li>Chỉ Admin mới có quyền gửi thông báo</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="./components/footer.jsp" />


