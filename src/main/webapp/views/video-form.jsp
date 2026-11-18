<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>${edit ? 'Sửa video' : 'Thêm video'}</title></head>
<body>
<jsp:include page="/common/topbar.jsp"/>
<h1>${edit ? 'Sửa video' : 'Thêm video'}</h1>

<c:if test="${alert != null}"><p style="color:red"><c:out value="${alert}"/></p></c:if>

<c:url var="actionUrl" value="${edit ? '/video/edit' : '/video/add'}">
  <c:if test="${edit}"><c:param name="id" value="${video.id}"/></c:if>
</c:url>

<form action="${actionUrl}" method="post" enctype="multipart/form-data">
  <input type="hidden" name="csrf" value="${csrf}">

  <p>Tiêu đề:<br>
    <input type="text" name="title" maxlength="200" size="60" required
           value="<c:out value='${video.title}'/>"></p>

  <p>Mô tả:<br>
    <textarea name="description" rows="5" cols="62" maxlength="4000"><c:out value="${video.description}"/></textarea></p>

  <p>File video (mp4, webm, ogg; tối đa 200 MB)
    <c:if test="${edit}"> - để trống nếu giữ video hiện tại</c:if>:<br>
    <input type="file" name="video" accept="video/mp4,video/webm,video/ogg" ${edit ? '' : 'required'}></p>

  <p>Ảnh bìa (jpg, png, webp; tối đa 5 MB, không bắt buộc):<br>
    <input type="file" name="poster" accept="image/jpeg,image/png,image/webp"></p>

  <p><label><input type="checkbox" name="active" ${video.active ? 'checked' : ''}> Hiển thị công khai</label></p>

  <button type="submit">Lưu</button>
  <a href="${pageContext.request.contextPath}/video">Hủy</a>
</form>
</body>
</html>