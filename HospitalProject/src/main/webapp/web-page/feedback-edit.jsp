<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<div class="container" style="max-width:700px;margin:24px auto;padding:0 16px;">
    <h2>Edit Feedback #${feedback.id}</h2>

    <c:if test="${not empty param.error}">
        <div style="padding:10px;background:#fff1f0;border:1px solid #ffa39e;margin:12px 0;">${param.error}</div>
    </c:if>

    <form action="feedback" method="post" style="display:grid;gap:12px;">
        <input type="hidden" name="action" value="edit" />
        <input type="hidden" name="id" value="${feedback.id}" />

        <label>Rating (1-5)
            <input type="number" name="rating" min="1" max="5" value="${feedback.rating}" required />
        </label>

        <label>Comment
            <textarea name="comment" rows="5" style="width:100%;">${feedback.comment}</textarea>
        </label>

        <c:if test="${sessionScope.user != null && sessionScope.user.roleId == 1}">
            <label>Status
                <select name="status">
                    <option value="Active" ${feedback.status == 'Active' ? 'selected' : ''}>Active</option>
                    <option value="Hidden" ${feedback.status == 'Hidden' ? 'selected' : ''}>Hidden</option>
                </select>
            </label>
        </c:if>

        <div style="display:flex;gap:8px;">
            <button type="submit">Save</button>
            <a href="feedback?action=view&id=${feedback.id}">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="./components/footer.jsp" />


