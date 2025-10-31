<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<div class="container" style="max-width:800px;margin:24px auto;padding:0 16px;">
    <h2>Notification #${notification.id}</h2>

    <c:if test="${not empty param.success}">
        <div style="padding:10px;background:#e6ffed;border:1px solid #b7eb8f;margin:12px 0;">${param.success}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div style="padding:10px;background:#fff1f0;border:1px solid #ffa39e;margin:12px 0;">${param.error}</div>
    </c:if>

    <div style="border:1px solid #ddd;padding:16px;border-radius:6px;">
        <p><strong>User ID:</strong> ${notification.userId != null ? notification.userId : 'Not available'}</p>
        <p><strong>Type:</strong> <c:out value="${notification.type != null && !empty notification.type ? notification.type : 'Not available'}"/></p>
        <p><strong>Status:</strong> <c:out value="${notification.status != null ? notification.status : 'Not available'}"/></p>
        <p><strong>Sent at:</strong> ${notification.sentAt != null ? notification.sentAt : 'Not available'}</p>
        <p><strong>Content:</strong></p>
        <div style="white-space:pre-wrap;border:1px dashed #ccc;padding:10px;border-radius:4px;">
            <c:out value="${notification.content != null && !empty notification.content ? notification.content : 'No content available'}"/>
        </div>
    </div>

    <div style="margin-top:16px;display:flex;gap:8px;">
        <a href="notifications?action=list">Back to list</a>
    </div>
</div>
<jsp:include page="./components/footer.jsp" />


