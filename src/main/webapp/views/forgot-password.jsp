<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><title>Quên mật khẩu</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-8 col-lg-5">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h2 class="h4 text-center mb-3">Quên mật khẩu</h2>
      <p class="text-muted">Nhập email hoặc tên tài khoản. Chúng tôi sẽ gửi mã OTP tới email đã đăng ký.</p>

      <c:if test="${not empty alert}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>

      <form action="${ctx}/forgot-password" method="post" class="needs-validation" novalidate>
        <t:field name="identifier" label="Email hoặc tên tài khoản" value="${param.identifier}"
                 error="${errors.identifier}" required="true" maxlength="100"
                 msg="Vui lòng nhập email hoặc tên tài khoản."/>
        <button type="submit" class="btn btn-primary w-100">Gửi mã OTP</button>
      </form>
      <p class="text-center mt-3 mb-0"><a href="${ctx}/login">Quay về đăng nhập</a></p>
    </div></div>
  </div>
</div>
</body>
</html>