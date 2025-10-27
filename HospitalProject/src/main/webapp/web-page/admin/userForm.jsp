<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="Model.User" %>
<%
    User u = (User) request.getAttribute("user");
    boolean editing = (u != null && u.getId() > 0);
    String ctx = request.getContextPath();
%>
<!DOCTYPE html><html lang="vi"><head>
        <meta charset="UTF-8"><title><%= editing ? "Sửa" : "Tạo"%> người dùng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head><body class="bg-light">
        <div class="container py-4">
            <div class="card shadow-sm">
                <div class="card-header"><b><%= editing ? "Sửa" : "Tạo mới"%> người dùng</b></div>
                <div class="card-body">
                    <form method="post" action="<%=ctx%>/admin/users">
                        <input type="hidden" name="action" value="save"/>
                        <% if (editing) {%><input type="hidden" name="id" value="<%=u.getId()%>"/><% }%>

                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label">Họ tên</label>
                                <input class="form-control" name="fullName" value="<%= editing ? u.getFullName() : ""%>" required>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Giới tính</label>
                                <select class="form-select" name="gender">
                                    <option value="">--</option>
                                    <option value="Male"   <%= editing && "Male".equals(u.getGender()) ? "selected" : ""%>>Male</option>
                                    <option value="Female" <%= editing && "Female".equals(u.getGender()) ? "selected" : ""%>>Female</option>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Ngày sinh</label>
                                <input type="date" class="form-control" name="birthDate"
                                       value="<%= editing && u.getBirthDate() != null ? u.getBirthDate().toString() : ""%>">
                            </div>

                            <div class="col-md-4">
                                <label class="form-label">Username</label>
                                <input class="form-control" name="username" value="<%= editing ? u.getUsername() : ""%>" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Email</label>
                                <input type="email" class="form-control" name="email" value="<%= editing ? u.getEmail() : ""%>" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">SĐT</label>
                                <input class="form-control" name="phoneNumber" value="<%= editing ? u.getPhoneNumber() : ""%>">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Địa chỉ</label>
                                <input class="form-control" name="address" value="<%= editing ? u.getAddress() : ""%>">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">CMND/CCCD</label>
                                <input class="form-control" name="idCardNumber" value="<%= editing ? u.getIdCardNumber() : ""%>">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Trạng thái</label>
                                <select class="form-select" name="status">
                                    <%
                                        String st = editing ? u.getStatus() : "pending";
                                        String[] sts = {"pending", "active", "inactive", "suspended"};
                                        for (String s : sts) {
                                    %>
                                    <option value="<%=s%>" <%= s.equals(st) ? "selected" : ""%>><%=s%></option>
                                    <% }%>
                                </select>
                            </div>

                            <div class="col-md-4">
                                <label class="form-label">Vai trò</label>
                                <select class="form-select" name="roleId" required>
                                    <!-- Gợi ý: load động từ bảng Roles; tạm map ID theo seed -->
                                    <option value="1" <%= editing && u.getRoleId() == 1 ? "selected" : ""%>>patient</option>
                                    <option value="2" <%= editing && u.getRoleId() == 2 ? "selected" : ""%>>doctor</option>
                                    <option value="3" <%= editing && u.getRoleId() == 3 ? "selected" : ""%>>admin</option>
                                    <option value="4" <%= editing && u.getRoleId() == 4 ? "selected" : ""%>>manager</option>
                                </select>
                            </div>

                            <% if (!editing) { %>
                            <div class="col-md-4">
                                <label class="form-label">Mật khẩu</label>
                                <input type="password" name="password" class="form-control" minlength="8" required>
                            </div>
                            <% } else { %>
                            <div class="col-md-4">
                                <label class="form-label">Mật khẩu mới (để trống nếu giữ nguyên)</label>
                                <input type="password" name="password" class="form-control" minlength="8">
                            </div>
                            <% }%>

                            <div class="col-12 d-flex gap-2">
                                <button class="btn btn-primary">Lưu</button>
                                <a class="btn btn-secondary" href="<%=ctx%>/admin/users?action=list">Hủy</a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </body></html>
