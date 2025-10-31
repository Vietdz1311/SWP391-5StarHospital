<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="./components/header.jsp" />
<div class="container" style="max-width:700px;margin:24px auto;padding:0 16px;">
    <h2>Send Notification</h2>

    <c:if test="${not empty param.error}">
        <div style="padding:10px;background:#fff1f0;border:1px solid #ffa39e;margin:12px 0;">${param.error}</div>
    </c:if>

    <form action="notifications" method="post" style="display:grid;gap:12px;">
        <input type="hidden" name="action" value="send" />

        <label>Recipient
            <select name="userId" required>
                <option value="" disabled selected>Select user</option>
                <c:forEach var="u" items="${users}">
                    <option value="${u.id}">${u.fullName} (${u.email})</option>
                </c:forEach>
            </select>
        </label>

        <label>Type
            <input type="text" name="type" placeholder="e.g. system, reminder" required />
        </label>

        <label>Content
            <textarea name="content" rows="6" required style="width:100%;"></textarea>
        </label>

        <label>
            <input type="checkbox" name="sendEmail" value="yes" /> Send as email too
        </label>

        <div style="display:flex;gap:8px;">
            <button type="submit">Send</button>
            <a href="notifications?action=list">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="./components/footer.jsp" />


