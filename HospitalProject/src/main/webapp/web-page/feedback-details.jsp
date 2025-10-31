<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<div class="container" style="max-width:800px;margin:24px auto;padding:0 16px;">
    <h2>Feedback #${feedback.id}</h2>

    <c:if test="${not empty param.success}">
        <div style="padding:10px;background:#e6ffed;border:1px solid #b7eb8f;margin:12px 0;">${param.success}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div style="padding:10px;background:#fff1f0;border:1px solid #ffa39e;margin:12px 0;">${param.error}</div>
    </c:if>

    <div style="border:1px solid #ddd;padding:16px;border-radius:6px;">
        <p><strong>User:</strong> <c:choose><c:when test="${feedback.user != null && feedback.user.fullName != null}"><c:out value="${feedback.user.fullName}"/></c:when><c:when test="${feedback.userId != null}">User #${feedback.userId}</c:when><c:otherwise>Not available</c:otherwise></c:choose></p>
        <p><strong>Rating:</strong> ${feedback.rating != null ? feedback.rating : 'Not available'} / 5</p>
        <p><strong>Status:</strong> <c:out value="${feedback.status != null ? feedback.status : 'Not available'}"/></p>
        <p><strong>Comment:</strong></p>
        <div style="white-space:pre-wrap;border:1px dashed #ccc;padding:10px;border-radius:4px;">
            <c:out value="${feedback.comment != null && !empty feedback.comment ? feedback.comment : 'No comment available'}"/>
        </div>
    </div>

    <div style="margin-top:16px;display:flex;gap:8px;">
        <a href="feedback?action=edit&id=${feedback.id}">Edit</a>
        <a href="feedback?action=list">Back to list</a>
    </div>
</div>
<jsp:include page="./components/footer.jsp" />


