<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<div class="container" style="max-width:1000px;margin:24px auto;padding:0 16px;">
    <h2>Manage Notifications</h2>

    <c:if test="${not empty param.success}">
        <div style="padding:10px;background:#e6ffed;border:1px solid #b7eb8f;margin:12px 0;">${param.success}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div style="padding:10px;background:#fff1f0;border:1px solid #ffa39e;margin:12px 0;">${param.error}</div>
    </c:if>

    <table border="1" width="100%" cellspacing="0" cellpadding="8">
        <thead>
            <tr>
                <th>ID</th>
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
                    <td><c:out value="${n.type != null && !empty n.type ? n.type : 'Not available'}"/></td>
                    <td><c:out value="${n.content != null && !empty n.content ? n.content : 'No content'}"/></td>
                    <td><c:out value="${n.status != null ? n.status : 'Not available'}"/></td>
                    <td>${n.sentAt != null ? n.sentAt : 'Not available'}</td>
                    <td>
                        <c:if test="${n.status != 'read'}">
                            <form action="notifications" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="updateStatus" />
                                <input type="hidden" name="id" value="${n.id}" />
                                <input type="hidden" name="status" value="read" />
                                <button type="submit">Mark read</button>
                            </form>
                        </c:if>
                        <c:if test="${n.status != 'unread'}">
                            <form action="notifications" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="updateStatus" />
                                <input type="hidden" name="id" value="${n.id}" />
                                <input type="hidden" name="status" value="unread" />
                                <button type="submit">Mark unread</button>
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
                <tr><td colspan="6" style="text-align:center;">No notifications</td></tr>
            </c:if>
        </tbody>
    </table>

    <div style="margin-top:16px;">
        <a href="notifications?action=list">Back to notifications</a>
    </div>
</div>
<jsp:include page="./components/footer.jsp" />


