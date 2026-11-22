<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><title>Xác nhận email mới</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-8 col-lg-5">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h2 class="h4 text-center mb-3">Xác nhận email mới</h2>
      <p class="text-muted text-center">Mã OTP đã gửi tới <strong><c:out value="${newEmail}"/></strong></p>

      <c:if test="${not empty info}"><div class="alert alert-success"><c:out value="${info}"/></div></c:if>
      <c:if test="${not empty alert}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>

      <form action="${ctx}/profile/verify-email" method="post" class="needs-validation" novalidate>
        <input type="hidden" name="csrf" value="${csrf}">
        <t:field name="otp" label="Mã OTP" error="${errors.otp}" required="true" pattern="[0-9]{6}"
                 maxlength="6" autocomplete="one-time-code" placeholder="Nhập mã 6 số"
                 msg="Mã OTP gồm đúng 6 chữ số."/>
        <button type="submit" class="btn btn-primary w-100">Xác nhận</button>
      </form>

      <form action="${ctx}/profile/resend-email" method="post" class="mt-2">
        <input type="hidden" name="csrf" value="${csrf}">
        <button type="submit" class="btn btn-outline-secondary w-100">Gửi lại mã</button>
      </form>

      <form action="${ctx}/profile/cancel-email" method="post" class="mt-2"
            onsubmit="return confirm('Hủy việc đổi email?')">
        <input type="hidden" name="csrf" value="${csrf}">
        <button type="submit" class="btn btn-link text-danger w-100">Hủy đổi email</button>
      </form>
      <p class="text-center mt-2 mb-0"><a href="${ctx}/profile">« Quay lại hồ sơ</a></p>
    </div></div>
  </div>
</div>
</body>
</html>