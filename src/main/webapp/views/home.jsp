<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head><title>Trang chủ</title></head>
<body>
<h1 class="h3 mb-4">10 video mới nhất</h1>
<c:choose>
  <c:when test="${empty videos}"><div class="alert alert-info">Chưa có video nào.</div></c:when>
  <c:otherwise><jsp:include page="/common/video-grid.jsp"/></c:otherwise>
</c:choose>
</body>
</html>