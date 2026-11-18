<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<style>
.vgrid{display:grid;grid-template-columns:repeat(auto-fill,minmax(240px,1fr));gap:16px}
.vcard{border:1px solid #ddd;border-radius:8px;overflow:hidden;background:#fff}
.vthumb{width:100%;aspect-ratio:16/9;background:#222;object-fit:cover;display:block}
.vbody{padding:10px}
.vbody h3{margin:0 0 6px;font-size:16px}
.vmeta{color:#666;font-size:13px}
.vbadge{background:#eee;border-radius:4px;padding:1px 6px;font-size:12px}
</style>
<div class="vgrid">
<c:forEach var="v" items="${videos}">
  <div class="vcard">
    <a href="${ctx}/video/detail?id=${v.id}">
      <c:choose>
        <c:when test="${not empty v.poster}">
          <img class="vthumb" src="${ctx}/media/posters/${v.poster}" alt="">
        </c:when>
        <c:otherwise><div class="vthumb"></div></c:otherwise>
      </c:choose>
    </a>
    <div class="vbody">
      <h3><a href="${ctx}/video/detail?id=${v.id}"><c:out value="${v.title}"/></a></h3>
      <div class="vmeta">
        <c:out value="${v.user.fullName}"/> · ${v.views} lượt xem ·
        <fmt:formatDate value="${v.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
      </div>
      <c:if test="${showActions}">
        <p>
          <c:if test="${!v.active}"><span class="vbadge">Đang ẩn</span></c:if>
          <a href="${ctx}/video/edit?id=${v.id}">Sửa</a>
          <form action="${ctx}/video/delete" method="post" style="display:inline"
                onsubmit="return confirm('Xóa video này?')">
            <input type="hidden" name="id" value="${v.id}">
            <input type="hidden" name="csrf" value="${csrf}">
            <button type="submit">Xóa</button>
          </form>
        </p>
      </c:if>
    </div>
  </div>
</c:forEach>
</div>