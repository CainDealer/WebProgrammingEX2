<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><title>Đăng ký</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-8 col-lg-6">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h2 class="h4 text-center mb-4">Tạo tài khoản mới</h2>

      <form action="${ctx}/register" method="post" class="needs-validation" novalidate>
        <t:field name="username" label="Tài khoản" value="${param.username}" error="${errors.username}"
                 required="true" pattern="[A-Za-z0-9_]{4,30}" maxlength="30" autocomplete="username"
                 msg="Tên tài khoản gồm 4–30 ký tự: chữ cái, số hoặc dấu gạch dưới."/>
        <t:field name="fullname" label="Họ tên" value="${param.fullname}" error="${errors.fullname}"
                 required="true" minlength="2" maxlength="100" autocomplete="name"
                 msg="Họ tên gồm 2–100 ký tự, chỉ chứa chữ cái."/>
        <t:field name="email" label="Email" type="email" value="${param.email}" error="${errors.email}"
                 required="true" pattern="[^@ ]+@[^@ ]+[.][A-Za-z]{2,}" maxlength="100" autocomplete="email"
                 msg="Email không hợp lệ."/>
        <t:field name="phone" label="Số điện thoại" type="tel" value="${param.phone}" error="${errors.phone}"
                 required="true" pattern="(0|[+]84)[35789][0-9]{8}" autocomplete="tel"
                 msg="Số điện thoại không hợp lệ (ví dụ 0912345678)."/>
        <t:field name="password" label="Mật khẩu" type="password" error="${errors.password}"
                 required="true" pattern="(?=.*[A-Za-z])(?=.*[0-9]).{8,64}" autocomplete="new-password"
                 msg="Mật khẩu gồm 8–64 ký tự, có ít nhất 1 chữ cái và 1 chữ số."/>
        <t:field name="confirmPassword" label="Nhập lại mật khẩu" type="password" error="${errors.confirmPassword}"
                 required="true" match="password" autocomplete="new-password"
                 msg="Mật khẩu nhập lại không khớp."/>

        <button type="submit" class="btn btn-primary w-100">Tạo tài khoản</button>
      </form>
      <p class="text-center mt-3 mb-0">Đã có tài khoản? <a href="${ctx}/login">Đăng nhập</a></p>
    </div></div>
  </div>
</div>
</body>
</html>