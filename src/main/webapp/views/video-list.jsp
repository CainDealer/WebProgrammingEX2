<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Video của tôi</title></head>
<body>
<jsp:include page="/common/topbar.jsp"/>
<h1>Video của tôi (${total})</h1>
<p><a href="${pageContext.request.contextPath}/video/add">+ Thêm video</a></p>

<c:if test="${param.msg == 'saved'}"><p style="color:green">Đã lưu video.</p></c:if>
<c:if test="${param.msg == 'deleted'}"><p style="color:green">Đã xóa video.</p></c:if>

<c:choose>
  <c:when test="${empty videos}"><p>Bạn chưa có video nào.</p></c:when>
  <c:otherwise>
    <c:set var="showActions" value="true" scope="request"/>
    <jsp:include page="/common/video-grid.jsp"/>

    <p style="margin-top:16px">
      <c:if test="${currentPage > 1}"><a href="?page=${currentPage - 1}">« Trước</a></c:if>
      <c:forEach begin="1" end="${totalPages}" var="i">
        <c:choose>
          <c:when test="${i == currentPage}"><strong>[${i}]</strong></c:when>
          <c:otherwise><a href="?page=${i}">${i}</a></c:otherwise>
        </c:choose>
      </c:forEach>
      <c:if test="${currentPage < totalPages}"><a href="?page=${currentPage + 1}">Sau »</a></c:if>
    </p>
  </c:otherwise>
</c:choose>
</body>
</html>