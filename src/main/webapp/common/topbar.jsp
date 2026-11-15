<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:choose>
    <c:when test="${sessionScope.account == null}">
        <div>
            <a href="${pageContext.request.contextPath}/login">Đăng nhập</a> |
            <a href="${pageContext.request.contextPath}/register">Đăng ký</a>
        </div>
    </c:when>
    <c:otherwise>
        <div>
            <a href="#">${sessionScope.account.fullName}</a> |
            <a href="${pageContext.request.contextPath}/logout">Đăng Xuất</a>
        </div>
    </c:otherwise>
</c:choose>