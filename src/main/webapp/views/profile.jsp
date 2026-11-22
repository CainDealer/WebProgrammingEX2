<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><title>Hồ sơ cá nhân</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-lg-9">
    <h1 class="h3 mb-4">Hồ sơ cá nhân</h1>

    <c:if test="${param.msg == 'saved'}"><div class="alert alert-success">Đã lưu thông tin.</div></c:if>
    <c:if test="${param.msg == 'email-updated'}"><div class="alert alert-success">Đổi email thành công.</div></c:if>
    <c:if test="${param.msg == 'cancelled'}"><div class="alert alert-info">Đã hủy yêu cầu đổi email.</div></c:if>
    <c:if test="${param.msg == 'conflict'}"><div class="alert alert-warning">Email mới vừa được tài khoản khác sử dụng. Vui lòng chọn email khác.</div></c:if>
    <c:if test="${not empty alert}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>

    <c:if test="${not empty pendingEmail}">
      <div class="alert alert-warning">
        Bạn có yêu cầu đổi email sang <strong><c:out value="${pendingEmail}"/></strong> đang chờ xác nhận.
        <a href="${ctx}/profile/verify-email">Nhập mã OTP</a>
      </div>
    </c:if>

    <div class="card shadow-sm"><div class="card-body p-4">
      <form action="${ctx}/profile" method="post" enctype="multipart/form-data"
            class="needs-validation" novalidate>
        <input type="hidden" name="csrf" value="${csrf}">
        <div class="row g-4">

          <div class="col-md-4 text-center">
            <c:choose>
              <c:when test="${not empty me.avatar}">
                <img id="avatarPreview" class="rounded-circle border avatar-lg mb-3"
                     src="<c:out value='${me.avatar}'/>" alt="Avatar">
                <div id="avatarPlaceholder" class="avatar-lg rounded-circle bg-secondary-subtle d-none align-items-center justify-content-center mx-auto mb-3">
                  <i class="bi bi-person-fill fs-1 text-secondary"></i></div>
              </c:when>
              <c:otherwise>
                <img id="avatarPreview" class="rounded-circle border avatar-lg mb-3 d-none" src="" alt="Avatar">
                <div id="avatarPlaceholder" class="avatar-lg rounded-circle bg-secondary-subtle d-flex align-items-center justify-content-center mx-auto mb-3">
                  <i class="bi bi-person-fill fs-1 text-secondary"></i></div>
              </c:otherwise>
            </c:choose>

            <div class="mb-3 text-start">
              <label for="f-avatar" class="form-label">Ảnh đại diện</label>
              <input id="f-avatar" name="avatar" type="file" accept="image/jpeg,image/png,image/webp"
                     class="form-control${not empty errors.avatar ? ' is-invalid' : ''}"
                     data-ext="jpg,jpeg,png,webp" data-max-mb="2">
              <div class="invalid-feedback"><c:out value="${errors.avatar}"/></div>
              <div class="form-text">JPG, PNG, WEBP, tối đa 2 MB. Để trống nếu giữ ảnh hiện tại.</div>
            </div>
          </div>

          <div class="col-md-8">
            <div class="mb-3">
              <label class="form-label">Tên tài khoản</label>
              <input type="text" class="form-control" value="<c:out value='${me.userName}'/>" disabled>
            </div>

            <t:field name="fullname" label="Họ tên" value="${form.fullname}" error="${errors.fullname}"
                     required="true" minlength="2" maxlength="100" autocomplete="name"
                     msg="Họ tên gồm 2–100 ký tự, chỉ chứa chữ cái."/>
            <t:field name="phone" label="Số điện thoại" type="tel" value="${form.phone}" error="${errors.phone}"
                     required="true" pattern="(0|[+]84)[35789][0-9]{8}" autocomplete="tel"
                     msg="Số điện thoại không hợp lệ (ví dụ 0912345678)."/>
            <t:field name="email" label="Email" type="email" value="${form.email}" error="${errors.email}"
                     required="true" pattern="[^@ ]+@[^@ ]+[.][A-Za-z]{2,}" maxlength="100" autocomplete="email"
                     msg="Email không hợp lệ."/>
            <div class="form-text mb-3" style="margin-top:-.5rem">
              Nếu đổi email, hệ thống gửi mã OTP tới <strong>email mới</strong>.
              Email chỉ được thay khi bạn nhập đúng mã.
            </div>

            <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
          </div>
        </div>
      </form>
    </div></div>
  </div>
</div>

<script>
  document.getElementById('f-avatar').addEventListener('change', function () {
    var f = this.files && this.files[0];
    var img = document.getElementById('avatarPreview');
    var ph = document.getElementById('avatarPlaceholder');
    if (!f || !f.type.startsWith('image/')) return;
    img.src = URL.createObjectURL(f);
    img.classList.remove('d-none');
    ph.classList.add('d-none');
    ph.classList.remove('d-flex');
  });
</script>
</body>
</html>