<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<div class="container" style="max-width:1000px;margin:24px auto;padding:0 16px;">
    <h2>Notifications</h2>

    <c:if test="${not empty param.success}">
        <div style="padding:10px;background:#e6ffed;border:1px solid #b7eb8f;margin:12px 0;">${param.success}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div style="padding:10px;background:#fff1f0;border:1px solid #ffa39e;margin:12px 0;">${param.error}</div>
    </c:if>

    <div style="margin-bottom:12px;display:flex;gap:8px;flex-wrap:wrap;align-items:center;">
        <form method="get" action="notifications" style="display:flex;gap:8px;align-items:center;">
            <input type="hidden" name="action" value="list" />
            <label>Type:
                <input type="text" name="type" value="${type}" placeholder="e.g. system, reminder" />
            </label>
            <label>Status:
                <select name="status">
                    <option value="" ${empty status ? 'selected' : ''}>All</option>
                    <option value="unread" ${status == 'unread' ? 'selected' : ''}>Unread</option>
                    <option value="read" ${status == 'read' ? 'selected' : ''}>Read</option>
                    <option value="archived" ${status == 'archived' ? 'selected' : ''}>Archived</option>
                </select>
            </label>
            <button type="submit">Filter</button>
        </form>
        <c:if test="${sessionScope.user != null}">
            <form method="post" action="notifications" style="display:inline;">
                <input type="hidden" name="action" value="markAllRead" />
                <button type="submit">Mark all as read</button>
            </form>
        </c:if>
        <c:if test="${sessionScope.user != null && sessionScope.user.roleId == 1}">
            <a href="notifications?action=send">Send notification</a>
        </c:if>
    </div>

    <table border="1" width="100%" cellspacing="0" cellpadding="8">
        <thead>
            <tr>
                <th>ID</th>
                <th>User</th>
                <th>Type</th>
                <th>Content</th>
                <th>Status</th>
                <th>Sent At</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="n" items="${notifications}">
                <tr>
                    <td>${n.id}</td>
                    <td>${n.userId != null ? n.userId : 'Not available'}</td>
                    <td><c:out value="${n.type != null && !empty n.type ? n.type : 'Not available'}"/></td>
                    <td><c:out value="${n.content != null && !empty n.content ? n.content : 'No content'}"/></td>
                    <td><c:out value="${n.status != null ? n.status : 'Not available'}"/></td>
                    <td>${n.sentAt != null ? n.sentAt : 'Not available'}</td>
                    <td>
                        <a href="notifications?action=view&id=${n.id}">View</a>
                        <c:if test="${n.status != 'read'}">
                            <form action="notifications" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="updateStatus" />
                                <input type="hidden" name="id" value="${n.id}" />
                                <input type="hidden" name="status" value="read" />
                                <button type="submit">Mark read</button>
                            </form>
                        </c:if>
                        <c:if test="${n.status != 'archived'}">
                            <form action="notifications" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="updateStatus" />
                                <input type="hidden" name="id" value="${n.id}" />
                                <input type="hidden" name="status" value="archived" />
                                <button type="submit">Archive</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty notifications}">
                <tr><td colspan="7" style="text-align:center;">No notifications</td></tr>
            </c:if>
        </tbody>
    </table>

    <div style="margin-top:12px;display:flex;gap:8px;justify-content:center;">
        <c:forEach var="p" begin="1" end="${totalPages}">
            <c:choose>
                <c:when test="${p == currentPage}">
                    <span style="padding:6px 10px;border:1px solid #999;">${p}</span>
                </c:when>
                <c:otherwise>
                    <a style="padding:6px 10px;border:1px solid #999;" href="notifications?action=list&page=${p}&type=${type}&status=${status}">${p}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
</div>
<jsp:include page="./components/footer.jsp" />


