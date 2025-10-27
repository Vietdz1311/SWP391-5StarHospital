<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, Model.User" %>
<%
    List<User> users = (List<User>) request.getAttribute("users");
    int total = (request.getAttribute("total") == null) ? 0 : (Integer) request.getAttribute("total");
    int pageNo = (request.getAttribute("page") == null) ? 1 : (Integer) request.getAttribute("page");
    int size = (request.getAttribute("size") == null) ? 10 : (Integer) request.getAttribute("size");
    String q = (String) request.getAttribute("q");
    String roleName = (String) request.getAttribute("roleName");
    String status = (String) request.getAttribute("status");
    int pages = (int) Math.ceil(total * 1.0 / size);
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý người dùng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { background:#f5f6fa; }
        .content {
            margin-left:240px; /* Để chừa chỗ cho sidebar */
            margin-top:72px;   /* Chừa chỗ cho topbar */
            padding:20px;
        }
    </style>
</head>
<body>

    <%-- Include layout navbar/sidebar admin --%>
    <%@ include file="adminNavbar.jsp" %>

    <div class="content">
        <div class="container-fluid py-3">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h3 class="mb-0">Quản lý người dùng</h3>
                <a href="<%=ctx%>/admin/users?action=create" class="btn btn-success">
                    <i class="bi bi-plus-circle me-1"></i> Tạo mới
                </a>
            </div>

            <form class="row g-2 mb-3" method="get" action="<%=ctx%>/admin/users">
                <input type="hidden" name="action" value="list"/>
                <div class="col-md-4">
                    <input name="q" value="<%=q != null ? q : ""%>" class="form-control" placeholder="Tìm: tên, username, email, sđt">
                </div>
                <div class="col-md-3">
                    <select name="roleName" class="form-select">
                        <option value="">-- Tất cả vai trò --</option>
                        <option value="patient" <%= "patient".equals(roleName) ? "selected" : ""%>>Patient</option>
                        <option value="doctor"  <%= "doctor".equals(roleName) ? "selected" : ""%>>Doctor</option>
                        <option value="manager" <%= "manager".equals(roleName) ? "selected" : ""%>>Manager</option>
                        <option value="admin"   <%= "admin".equals(roleName) ? "selected" : ""%>>Admin</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <select name="status" class="form-select">
                        <option value="">-- Tất cả trạng thái --</option>
                        <option value="pending"   <%= "pending".equals(status) ? "selected" : ""%>>Pending</option>
                        <option value="active"    <%= "active".equals(status) ? "selected" : ""%>>Active</option>
                        <option value="inactive"  <%= "inactive".equals(status) ? "selected" : ""%>>Inactive</option>
                        <option value="suspended" <%= "suspended".equals(status) ? "selected" : ""%>>Suspended</option>
                    </select>
                </div>
                <div class="col-md-2 d-grid"><button class="btn btn-primary">Lọc</button></div>
            </form>

            <div class="card shadow-sm border-0">
                <div class="table-responsive">
                    <table class="table table-striped align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Họ tên</th>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (users == null || users.isEmpty()) {
                            %>
                            <tr><td colspan="7" class="text-center text-muted">Không có dữ liệu</td></tr>
                            <%
                            } else {
                                for (User u : users) {
                            %>
                            <tr>
                                <td><%=u.getId()%></td>
                                <td><%=u.getFullName()%></td>
                                <td><%=u.getUsername()%></td>
                                <td><%=u.getEmail()%></td>
                                <td><%=u.getRoleId()%></td>
                                <td>
                                    <span class="badge bg-<%= "active".equalsIgnoreCase(u.getStatus()) ? "success" : "secondary"%>">
                                        <%=u.getStatus()%>
                                    </span>
                                </td>
                                <td class="d-flex gap-1 flex-wrap">
                                    <a class="btn btn-sm btn-outline-primary" href="<%=ctx%>/admin/users?action=edit&id=<%=u.getId()%>">Sửa</a>
                                    <form method="post" action="<%=ctx%>/admin/users" class="d-inline">
                                        <input type="hidden" name="action" value="changeStatus"/>
                                        <input type="hidden" name="id" value="<%=u.getId()%>"/>
                                        <input type="hidden" name="newStatus" value="<%= "active".equalsIgnoreCase(u.getStatus()) ? "inactive" : "active"%>"/>
                                        <button class="btn btn-sm btn-outline-warning">Đổi trạng thái</button>
                                    </form>
                                    <button class="btn btn-sm btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#pw<%=u.getId()%>">Đặt lại mật khẩu</button>
                                    <form method="post" action="<%=ctx%>/admin/users" onsubmit="return confirm('Xác nhận xóa mềm?')" class="d-inline">
                                        <input type="hidden" name="action" value="delete"/>
                                        <input type="hidden" name="id" value="<%=u.getId()%>"/>
                                        <button class="btn btn-sm btn-outline-danger">Xóa</button>
                                    </form>
                                </td>
                            </tr>
                            <!-- Modal reset password -->
                            <div class="modal fade" id="pw<%=u.getId()%>" tabindex="-1">
                                <div class="modal-dialog"><div class="modal-content">
                                    <form method="post" action="<%=ctx%>/admin/users">
                                        <input type="hidden" name="action" value="resetPassword"/>
                                        <input type="hidden" name="id" value="<%=u.getId()%>"/>
                                        <div class="modal-header">
                                            <h5 class="modal-title">Đặt lại mật khẩu: <%=u.getUsername()%></h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body">
                                            <input class="form-control" name="newPassword" placeholder="Mật khẩu mới (8+ ký tự)" minlength="8" required/>
                                        </div>
                                        <div class="modal-footer">
                                            <button class="btn btn-primary">Xác nhận</button>
                                        </div>
                                    </form>
                                </div></div>
                            </div>
                            <%
                                }
                            }
                            %>
                        </tbody>
                    </table>
                </div>
                <div class="card-footer">
                    <nav>
                        <ul class="pagination mb-0">
                            <%
                                for (int p = 1; p <= Math.max(pages, 1); p++) {
                            %>
                            <li class="page-item <%= (p == pageNo) ? "active" : ""%>">
                                <a class="page-link"
                                   href="?action=list&q=<%=q != null ? q : ""%>&roleName=<%=roleName != null ? roleName : ""%>&status=<%=status != null ? status : ""%>&page=<%=p%>&size=<%=size%>"><%=p%></a>
                            </li>
                            <% }%>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
