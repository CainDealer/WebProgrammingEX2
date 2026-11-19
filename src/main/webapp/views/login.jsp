<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><title>Đăng nhập</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-8 col-lg-5">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h2 class="h4 text-center mb-4">Đăng nhập vào hệ thống</h2>

      <c:if test="${param.activated == '1'}"><div class="alert alert-success">Kích hoạt thành công! Bạn có thể đăng nhập.</div></c:if>
      <c:if test="${param.reset == '1'}"><div class="alert alert-success">Đặt lại mật khẩu thành công! Hãy đăng nhập bằng mật khẩu mới.</div></c:if>
      <c:if test="${param.logout == '1'}"><div class="alert alert-info">Bạn đã đăng xuất.</div></c:if>
      <c:if test="${not empty alert}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>

      <form action="${ctx}/login" method="post" class="needs-validation" novalidate>
        <t:field name="username" label="Tài khoản" value="${param.username}" error="${errors.username}"
                 required="true" maxlength="50" autocomplete="username" msg="Vui lòng nhập tài khoản."/>
        <t:field name="password" label="Mật khẩu" type="password" error="${errors.password}"
                 required="true" maxlength="64" autocomplete="current-password" msg="Vui lòng nhập mật khẩu."/>

        <div class="d-flex justify-content-between mb-3">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" name="remember" id="remember">
            <label class="form-check-label" for="remember">Nhớ tôi</label>
          </div>
          <a href="${ctx}/forgot-password">Quên mật khẩu?</a>
        </div>
        <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
      </form>
      <p class="text-center mt-3 mb-0">Chưa có tài khoản? <a href="${ctx}/register">Đăng ký</a></p>
    </div></div>
  </div>
</div>
</body>
</html>