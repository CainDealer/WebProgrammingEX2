<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div style="padding:8px 0;border-bottom:1px solid #ddd;margin-bottom:12px">
  <a href="${ctx}/home">Trang chủ</a> |
  <c:choose>
    <c:when test="${sessionScope.account == null}">
      <a href="${ctx}/login">Đăng nhập</a> |
      <a href="${ctx}/register">Đăng ký</a>
    </c:when>
    <c:otherwise>
      <a href="${ctx}/video">Video của tôi</a> |
      <c:out value="${sessionScope.account.fullName}"/> |
      <a href="${ctx}/logout">Đăng Xuất</a>
    </c:otherwise>
  </c:choose>
</div>