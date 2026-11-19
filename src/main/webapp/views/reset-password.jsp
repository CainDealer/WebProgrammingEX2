<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><title>Đặt lại mật khẩu</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-8 col-lg-5">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h2 class="h4 text-center mb-3">Đặt lại mật khẩu</h2>

      <c:if test="${not empty info}"><div class="alert alert-success"><c:out value="${info}"/></div></c:if>
      <c:if test="${not empty alert}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>

      <form action="${ctx}/reset-password" method="post" class="needs-validation" novalidate>
        <t:field name="otp" label="Mã OTP" value="${param.otp}" error="${errors.otp}" required="true"
                 pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" msg="Mã OTP gồm đúng 6 chữ số."/>
        <t:field name="newPassword" label="Mật khẩu mới" type="password" error="${errors.newPassword}"
                 required="true" pattern="(?=.*[A-Za-z])(?=.*[0-9]).{8,64}" autocomplete="new-password"
                 msg="Mật khẩu gồm 8–64 ký tự, có ít nhất 1 chữ cái và 1 chữ số."/>
        <t:field name="confirmPassword" label="Nhập lại mật khẩu mới" type="password"
                 error="${errors.confirmPassword}" required="true" match="newPassword"
                 autocomplete="new-password" msg="Mật khẩu nhập lại không khớp."/>
        <button type="submit" class="btn btn-primary w-100">Đổi mật khẩu</button>
      </form>
      <p class="text-center mt-3 mb-0">Chưa nhận được mã? <a href="${ctx}/forgot-password">Gửi lại mã</a></p>
    </div></div>
  </div>
</div>
</body>
</html>