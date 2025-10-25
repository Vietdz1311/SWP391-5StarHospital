<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="Model.User" %>

<%
    User user = (User) request.getAttribute("user");
    String ctx = request.getContextPath();
    String success = request.getParameter("success");
    if (user == null) {
        response.sendRedirect(ctx + "/auth");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">

    <head>
        <meta charset="UTF-8">
        <title>Hồ sơ cá nhân</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">
        <jsp:include page="/web-page/components/header.jsp" />
        <div class="container py-4">
            <div class="col-lg-10 mx-auto">
                <div class="card shadow-sm border-0">
                    <div class="card-header bg-primary text-white">Hồ sơ cá nhân</div>
                    <div class="card-body">

                        <% if ("1".equals(success)) { %>
                        <div class="alert alert-success">Cập nhật thành công.</div>
                        <% } %>
                        <% if (request.getAttribute("error") != null) {%>
                        <div class="alert alert-danger"><%= request.getAttribute("error")%></div>
                        <% }%>

                        <div class="d-flex gap-3 align-items-center mb-4">
                            <img id="avatarPreview"
                                 src="<%= ctx + "/" + ((user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) ? user.getProfilePicture() : "assets/default-avatar.png")%>"
                                 alt="avatar" class="rounded-circle border"
                                 style="width:96px;height:96px;object-fit:cover;">
                            <div>
                                <div class="fw-semibold fs-5"><%= nn(user.getFullName(), "Chưa có tên")%></div>
                                <div class="text-muted">Username: <%= nn(user.getUsername(), "")%></div>
                                <div class="text-muted">Email: <%= nn(user.getEmail(), "")%></div>
                            </div>
                        </div>

                        <form action="<%= ctx%>/profile/update" method="post" enctype="multipart/form-data" class="row g-3 needs-validation" novalidate>
                            <div class="col-md-6">
                                <label class="form-label">Họ và tên</label>
                                <input type="text" name="full_name" class="form-control" value="<%= nz(user.getFullName())%>" required>
                                <div class="invalid-feedback">Vui lòng nhập họ tên.</div>
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Số điện thoại</label>
                                <input type="text" name="phone_number" class="form-control" value="<%= nz(user.getPhoneNumber())%>">
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Ngày sinh</label>
                                <input type="date" name="birth_date" class="form-control" value="<%= d(user.getBirthDate())%>">
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Giới tính</label>
                                <select name="gender" class="form-select">
                                    <option value="" <%= sel(user.getGender(), null)%> >--</option>
                                    <option value="Male" <%= sel(user.getGender(), "Male")%> >Nam</option>
                                    <option value="Female" <%= sel(user.getGender(), "Female")%> >Nữ</option>
                                </select>
                            </div>

                            <div class="col-md-9">
                                <label class="form-label">Địa chỉ</label>
                                <input type="text" name="address" class="form-control" value="<%= nz(user.getAddress())%>">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Email</label>
                                <input type="email" name="email" class="form-control" value="<%= nz(user.getEmail())%>">
                                <div class="invalid-feedback">Email không hợp lệ.</div>
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Ảnh đại diện</label>
                                <input type="file" id="profile_picture" name="profile_picture" accept="image/*" class="form-control">
                            </div>

                            <div class="col-12 d-flex justify-content-end gap-2">
                                <a href="<%= ctx%>/" class="btn btn-outline-secondary">Hủy</a>
                                <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                            </div>
                        </form>

                    </div>
                </div>
            </div>
        </div>

        <script>
            (() => {
                const forms = document.querySelectorAll('.needs-validation');
                Array.from(forms).forEach(form => {
                    form.addEventListener('submit', e => {
                        if (!form.checkValidity()) {
                            e.preventDefault();
                            e.stopPropagation();
                        }
                        form.classList.add('was-validated');
                    }, false);
                });
                const file = document.getElementById('profile_picture');
                const preview = document.getElementById('avatarPreview');
                if (file) {
                    file.addEventListener('change', () => {
                        const f = file.files && file.files[0];
                        if (!f)
                            return;
                        const reader = new FileReader();
                        reader.onload = ev => {
                            preview.src = ev.target.result;
                        };
                        reader.readAsDataURL(f);
                    });
                }
            })();
        </script>

        <%!
            private String nz(Object o) {
                return o == null ? "" : o.toString();
            }

            private String nn(Object o, String d) {
                return o == null ? d : o.toString();
            }

            private String d(java.time.LocalDate x) {
                return x == null ? "" : x.toString();
            }

            private String sel(String v, String exp) {
                if (v == null && exp == null) {
                    return "selected";
                }
                if (v == null || exp == null) {
                    return "";
                }
                return v.equals(exp) ? "selected" : "";
            }
        %>
        <jsp:include page="/web-page/components/footer.jsp" />
    </body>

</html>