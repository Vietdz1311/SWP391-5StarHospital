<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
  <style>
    body { background:#f5f6fa; }
    .sidebar {
      position: fixed; top:0; left:0; bottom:0;
      width:240px; background:#111827; color:#fff; z-index:1030;
    }
    .sidebar .brand { padding:16px 20px; border-bottom:1px solid rgba(255,255,255,.08); }
    .sidebar a {
      display:block; padding:12px 20px; color:#ddd; text-decoration:none;
    }
    .sidebar a:hover, .sidebar a.active { background:#2563eb; color:#fff; }
    .topbar {
      position: fixed; top:0; left:240px; right:0; height:56px;
      background:#1f2937; color:#fff; display:flex; align-items:center; padding:0 16px; z-index:1020;
    }
    .content {
      margin-left:240px; padding:20px; margin-top:72px;
    }
  </style>
</head>
<body>

  <!-- ===== Sidebar ===== -->
  <aside class="sidebar">
    <div class="brand">
      <h5 class="m-0 fw-bold">Admin Panel</h5>
      <small class="text-secondary">5 Star Hospital</small>
    </div>
    <a href="${pageContext.request.contextPath}/admin/dashboard.jsp"><i class="bi bi-speedometer2 me-2"></i> Dashboard</a>
    <a href="${pageContext.request.contextPath}/web-page/admin/userList.jsp" class="active"><i class="bi bi-people me-2"></i> Users</a>
    <a href="#"><i class="bi bi-calendar-check me-2"></i> Appointments</a>
    <a href="#"><i class="bi bi-hospital me-2"></i> Hospitals</a>
    <a href="#"><i class="bi bi-gear me-2"></i> Settings</a>
  </aside>

  <!-- ===== Topbar ===== -->
  <nav class="topbar">
    <div class="d-flex align-items-center gap-3">
      <h6 class="mb-0">Quản lý người dùng</h6>
    </div>
    <div class="ms-auto d-flex align-items-center gap-3">
      <a class="text-white text-decoration-none" href="#"><i class="bi bi-bell"></i></a>
      <a class="text-white text-decoration-none" href="#"><i class="bi bi-person-circle"></i></a>
    </div>
  </nav>

</body>
</html>
