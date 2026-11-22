<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="sitemesh" uri="http://www.sitemesh.org/sitemesh-ext"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><sitemesh:write property="title"/> | Shopping</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
  <link href="${ctx}/assets/css/site.css" rel="stylesheet">
  <sitemesh:write property="head"/>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">

  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
      <a class="navbar-brand" href="${ctx}/home"><i class="bi bi-play-circle-fill text-danger"></i> Shopping</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="mainNav">
        <ul class="navbar-nav me-auto">
          <li class="nav-item"><a class="nav-link" href="${ctx}/home">Trang chủ</a></li>
          <c:if test="${sessionScope.account != null}">
            <li class="nav-item"><a class="nav-link" href="${ctx}/video">Video của tôi</a></li>
          </c:if>
        </ul>
        <ul class="navbar-nav">
          <c:choose>
            <c:when test="${sessionScope.account == null}">
              <li class="nav-item"><a class="nav-link" href="${ctx}/login">Đăng nhập</a></li>
              <li class="nav-item"><a class="nav-link" href="${ctx}/register">Đăng ký</a></li>
            </c:when>
            <c:otherwise>
              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle d-flex align-items-center gap-1" href="#" data-bs-toggle="dropdown">
                  <c:choose>
                    <c:when test="${not empty sessionScope.account.avatar}">
                      <img class="rounded-circle avatar-sm" src="<c:out value='${sessionScope.account.avatar}'/>" alt="">
                    </c:when>
                    <c:otherwise><i class="bi bi-person-circle"></i></c:otherwise>
                  </c:choose>
                  <c:out value="${sessionScope.account.fullName}"/>
                </a>
                <ul class="dropdown-menu dropdown-menu-end">
                  <li><a class="dropdown-item" href="${ctx}/profile">Hồ sơ cá nhân</a></li>
                  <li><a class="dropdown-item" href="${ctx}/video">Video của tôi</a></li>
                  <li><a class="dropdown-item" href="${ctx}/video/add">Thêm video</a></li>
                  <li><hr class="dropdown-divider"></li>
                  <li><a class="dropdown-item" href="${ctx}/logout">Đăng xuất</a></li>
                </ul>
              </li>
            </c:otherwise>
          </c:choose>
        </ul>
      </div>
    </div>
  </nav>

  <main class="container flex-grow-1 py-4">
    <sitemesh:write property="body"/>
  </main>

  <footer class="bg-dark text-white-50 text-center py-3 mt-auto">
    <small>&copy; Shopping, HCMUTE</small>
  </footer>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="${ctx}/assets/js/validate.js"></script>
</body>
</html>