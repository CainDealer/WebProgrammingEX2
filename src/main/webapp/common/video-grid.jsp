<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="row row-cols-1 row-cols-sm-2 row-cols-lg-3 g-4">
<c:forEach var="v" items="${videos}">
  <div class="col">
    <div class="card h-100 vcard">
      <a href="${ctx}/video/detail?id=${v.id}">
        <c:choose>
          <c:when test="${not empty v.poster}"><img class="vthumb card-img-top" src="${ctx}/media/posters/${v.poster}" alt=""></c:when>
          <c:otherwise><div class="vthumb card-img-top d-flex align-items-center justify-content-center text-white-50"><i class="bi bi-play-circle fs-1"></i></div></c:otherwise>
        </c:choose>
      </a>
      <div class="card-body">
        <h3 class="card-title h6"><a href="${ctx}/video/detail?id=${v.id}"><c:out value="${v.title}"/></a></h3>
        <div class="text-muted small">
          <c:out value="${v.user.fullName}"/> · ${v.views} lượt xem ·
          <fmt:formatDate value="${v.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
        </div>
      </div>
      <c:if test="${showActions}">
        <div class="card-footer bg-white d-flex gap-2 align-items-center">
          <c:if test="${!v.active}"><span class="badge text-bg-secondary">Đang ẩn</span></c:if>
          <a class="btn btn-sm btn-outline-primary ms-auto" href="${ctx}/video/edit?id=${v.id}">Sửa</a>
          <form action="${ctx}/video/delete" method="post" onsubmit="return confirm('Xóa video này?')">
            <input type="hidden" name="id" value="${v.id}">
            <input type="hidden" name="csrf" value="${csrf}">
            <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
          </form>
        </div>
      </c:if>
    </div>
  </div>
</c:forEach>
</div>