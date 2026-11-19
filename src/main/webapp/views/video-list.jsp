<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><title>Video của tôi</title></head>
<body>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 mb-0">Video của tôi (${total})</h1>
  <a class="btn btn-primary" href="${ctx}/video/add"><i class="bi bi-plus-lg"></i> Thêm video</a>
</div>

<c:if test="${param.msg == 'saved'}"><div class="alert alert-success">Đã lưu video.</div></c:if>
<c:if test="${param.msg == 'deleted'}"><div class="alert alert-success">Đã xóa video.</div></c:if>

<c:choose>
  <c:when test="${empty videos}"><div class="alert alert-info">Bạn chưa có video nào.</div></c:when>
  <c:otherwise>
    <c:set var="showActions" value="true" scope="request"/>
    <jsp:include page="/common/video-grid.jsp"/>

    <nav class="mt-4"><ul class="pagination justify-content-center">
      <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
        <a class="page-link" href="?page=${currentPage - 1}">«</a></li>
      <c:forEach begin="1" end="${totalPages}" var="i">
        <li class="page-item ${i == currentPage ? 'active' : ''}"><a class="page-link" href="?page=${i}">${i}</a></li>
      </c:forEach>
      <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
        <a class="page-link" href="?page=${currentPage + 1}">»</a></li>
    </ul></nav>
  </c:otherwise>
</c:choose>
</body>
</html>