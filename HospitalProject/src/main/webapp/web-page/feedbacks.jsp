<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<div class="container" style="max-width:1000px;margin:24px auto;padding:0 16px;">
    <h2>Feedbacks</h2>

    <c:if test="${not empty param.success}">
        <div style="padding:10px;background:#e6ffed;border:1px solid #b7eb8f;margin:12px 0;">${param.success}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div style="padding:10px;background:#fff1f0;border:1px solid #ffa39e;margin:12px 0;">${param.error}</div>
    </c:if>

    <form method="get" action="feedback" style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
        <input type="hidden" name="action" value="list" />
        <label>Status:
            <select name="status">
                <option value="" ${empty status ? 'selected' : ''}>All</option>
                <option value="Active" ${status == 'Active' ? 'selected' : ''}>Active</option>
                <option value="Hidden" ${status == 'Hidden' ? 'selected' : ''}>Hidden</option>
            </select>
        </label>
        <button type="submit">Filter</button>
    </form>

    <table border="1" width="100%" cellspacing="0" cellpadding="8">
        <thead>
            <tr>
                <th>ID</th>
                <th>User</th>
                <th>Rating</th>
                <th>Comment</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="f" items="${feedbacks}">
                <tr>
                    <td>${f.id}</td>
                    <td><c:choose><c:when test="${f.user != null && f.user.fullName != null}"><c:out value="${f.user.fullName}"/></c:when><c:when test="${f.userId != null}">User #${f.userId}</c:when><c:otherwise>Not available</c:otherwise></c:choose></td>
                    <td>${f.rating != null ? f.rating : 'Not available'}</td>
                    <td><c:out value="${f.comment != null && !empty f.comment ? f.comment : 'No comment'}"/></td>
                    <td><c:out value="${f.status != null ? f.status : 'Not available'}"/></td>
                    <td>
                        <a href="feedback?action=view&id=${f.id}">View</a>
                        <a href="feedback?action=edit&id=${f.id}">Edit</a>
                        <form action="feedback" method="post" style="display:inline;" onsubmit="return confirm('Delete this feedback?');">
                            <input type="hidden" name="action" value="delete" />
                            <input type="hidden" name="id" value="${f.id}" />
                            <button type="submit">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty feedbacks}">
                <tr><td colspan="6" style="text-align:center;">No feedbacks</td></tr>
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
                    <a style="padding:6px 10px;border:1px solid #999;" href="feedback?action=list&page=${p}&status=${status}">${p}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
</div>
<jsp:include page="./components/footer.jsp" />


