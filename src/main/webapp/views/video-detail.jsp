<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title><c:out value="${video.title}"/></title></head>
<body>
<jsp:include page="/common/topbar.jsp"/>

<h1><c:out value="${video.title}"/></h1>

<video controls preload="metadata" style="width:100%;max-width:900px;background:#000"
       <c:if test="${not empty video.poster}">poster="${ctx}/media/posters/${video.poster}"</c:if>
       src="${ctx}/media/videos/${video.filename}">
  Trình duyệt của bạn không hỗ trợ phát video.
</video>

<p style="color:#666">
  Đăng bởi <strong><c:out value="${video.user.fullName}"/></strong> ·
  ${video.views} lượt xem ·
  <fmt:formatDate value="${video.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
  <c:if test="${!video.active}"> · <em>Đang ẩn</em></c:if>
</p>

<div style="white-space:pre-wrap;max-width:900px"><c:out value="${video.description}"/></div>

<c:if test="${canModify}">
  <p>
    <a href="${ctx}/video/edit?id=${video.id}">Sửa</a>
    <form action="${ctx}/video/delete" method="post" style="display:inline"
          onsubmit="return confirm('Xóa video này?')">
      <input type="hidden" name="id" value="${video.id}">
      <input type="hidden" name="csrf" value="${csrf}">
      <button type="submit">Xóa</button>
    </form>
  </p>
</c:if>

<p><a href="${ctx}/home">« Trang chủ</a>
   <c:if test="${sessionScope.account != null}"> | <a href="${ctx}/video">« Video của tôi</a></c:if></p>
</body>
</html>