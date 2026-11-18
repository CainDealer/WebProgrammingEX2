<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Trang chủ</title></head>
<body>
<jsp:include page="/common/topbar.jsp"/>
<h1>Trang chủ</h1>
<h2>10 video mới nhất</h2>
<c:choose>
  <c:when test="${empty videos}"><p>Chưa có video nào.</p></c:when>
  <c:otherwise><jsp:include page="/common/video-grid.jsp"/></c:otherwise>
</c:choose>
</body>
</html>