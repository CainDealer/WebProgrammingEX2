<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><title><c:out value="${video.title}"/></title></head>
<body>
<div class="row justify-content-center">
  <div class="col-lg-9">
    <div class="ratio ratio-16x9 bg-black rounded overflow-hidden mb-3">
      <video controls preload="metadata"
             <c:if test="${not empty video.poster}">poster="${ctx}/media/posters/${video.poster}"</c:if>
             src="${ctx}/media/videos/${video.filename}">
        Trình duyệt của bạn không hỗ trợ phát video.
      </video>
    </div>

    <h1 class="h4"><c:out value="${video.title}"/></h1>
    <p class="text-muted">
      Đăng bởi <strong><c:out value="${video.user.fullName}"/></strong> ·
      ${video.views} lượt xem ·
      <fmt:formatDate value="${video.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
      <c:if test="${!video.active}"> · <span class="badge text-bg-secondary">Đang ẩn</span></c:if>
    </p>
    <div class="card card-body mb-3" style="white-space:pre-wrap"><c:out value="${video.description}"/></div>

    <div class="d-flex gap-2">
      <a class="btn btn-outline-secondary" href="${ctx}/home">« Trang chủ</a>
      <c:if test="${canModify}">
        <a class="btn btn-outline-primary" href="${ctx}/video/edit?id=${video.id}">Sửa</a>
        <form action="${ctx}/video/delete" method="post" onsubmit="return confirm('Xóa video này?')">
          <input type="hidden" name="id" value="${video.id}">
          <input type="hidden" name="csrf" value="${csrf}">
          <button type="submit" class="btn btn-outline-danger">Xóa</button>
        </form>
      </c:if>
    </div>
  </div>
</div>
</body>
</html>