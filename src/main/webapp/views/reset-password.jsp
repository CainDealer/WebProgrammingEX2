<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Đặt lại mật khẩu</title>
</head>
<body>
    <h2>Đặt lại mật khẩu</h2>

    <c:if test="${info != null}">
        <p style="color:green">${info}</p>
    </c:if>
    <c:if test="${alert != null}">
        <p style="color:red">${alert}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/reset-password" method="post">
        <input type="text" name="otp" placeholder="Mã OTP 6 số" maxlength="6"
               pattern="[0-9]{6}" inputmode="numeric" autocomplete="one-time-code" required><br><br>
        <input type="password" name="newPassword" placeholder="Mật khẩu mới (tối thiểu 6 ký tự)"
               minlength="6" required><br><br>
        <input type="password" name="confirmPassword" placeholder="Nhập lại mật khẩu mới"
               minlength="6" required><br><br>
        <button type="submit">Đổi mật khẩu</button>
    </form>

    <p>Chưa nhận được mã? <a href="${pageContext.request.contextPath}/forgot-password">Gửi lại mã</a></p>
    <p><a href="${pageContext.request.contextPath}/login">Quay về đăng nhập</a></p>
</body>
</html>