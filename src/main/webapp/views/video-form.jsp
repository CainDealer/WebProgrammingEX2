<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><title>${edit ? 'Sửa video' : 'Thêm video'}</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-lg-8">
    <div class="card shadow-sm"><div class="card-body p-4">
      <h1 class="h4 mb-4">${edit ? 'Sửa video' : 'Thêm video'}</h1>

      <c:if test="${not empty alert}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>

      <c:url var="actionUrl" value="${edit ? '/video/edit' : '/video/add'}">
        <c:if test="${edit}"><c:param name="id" value="${video.id}"/></c:if>
      </c:url>

      <form action="${actionUrl}" method="post" enctype="multipart/form-data" class="needs-validation" novalidate>
        <input type="hidden" name="csrf" value="${csrf}">

        <t:field name="title" label="Tiêu đề" value="${video.title}" error="${errors.title}"
                 required="true" maxlength="200" msg="Vui lòng nhập tiêu đề (tối đa 200 ký tự)."/>

        <div class="mb-3">
          <label for="f-description" class="form-label">Mô tả</label>
          <textarea id="f-description" name="description" rows="5" maxlength="4000"
                    class="form-control${not empty errors.description ? ' is-invalid' : ''}"
                    data-msg="Mô tả tối đa 4000 ký tự."><c:out value="${video.description}"/></textarea>
          <div class="invalid-feedback"><c:out value="${errors.description}"/></div>
        </div>

        <div class="mb-3">
          <label for="f-video" class="form-label">File video (mp4, webm, ogg; tối đa 200 MB)
            <c:choose>
              <c:when test="${edit}"><span class="text-muted">- để trống nếu giữ video hiện tại</span></c:when>
              <c:otherwise><span class="text-danger">*</span></c:otherwise>
            </c:choose>
          </label>
          <input id="f-video" name="video" type="file" accept="video/mp4,video/webm,video/ogg"
                 class="form-control${not empty errors.video ? ' is-invalid' : ''}"
                 data-ext="mp4,webm,ogg" data-max-mb="200" ${edit ? '' : 'required'}
                 data-msg="Vui lòng chọn file video.">
          <div class="invalid-feedback"><c:out value="${errors.video}"/></div>
        </div>

        <div class="mb-3">
          <label for="f-poster" class="form-label">Ảnh bìa (jpg, png, webp; tối đa 5 MB, không bắt buộc)</label>
          <input id="f-poster" name="poster" type="file" accept="image/jpeg,image/png,image/webp"
                 class="form-control${not empty errors.poster ? ' is-invalid' : ''}"
                 data-ext="jpg,jpeg,png,webp" data-max-mb="5">
          <div class="invalid-feedback"><c:out value="${errors.poster}"/></div>
        </div>

        <div class="form-check mb-4">
          <input class="form-check-input" type="checkbox" name="active" id="active" ${video.active ? 'checked' : ''}>
          <label class="form-check-label" for="active">Hiển thị công khai</label>
        </div>

        <button type="submit" class="btn btn-primary">Lưu</button>
        <a href="${ctx}/video" class="btn btn-outline-secondary">Hủy</a>
      </form>
    </div></div>
  </div>
</div>
</body>
</html>