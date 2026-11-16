<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Đăng nhập</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/login" method="post">
    <h2>Đăng Nhập Vào Hệ Thống</h2>

    <c:if test="${param.activated == '1'}">
        <p style="color:green">Kích hoạt thành công! Bạn có thể đăng nhập.</p>
    </c:if>
    <c:if test="${param.reset == '1'}">
        <p style="color:green">Đặt lại mật khẩu thành công! Hãy đăng nhập bằng mật khẩu mới.</p>
    </c:if>
    <c:if test="${param.logout == '1'}">
        <p style="color:green">Bạn đã đăng xuất.</p>
    </c:if>
    <c:if test="${alert != null}">
        <h3 style="color:red">${alert}</h3>
    </c:if>

    <input type="text" name="username" placeholder="Tài khoản"><br><br>
    <input type="password" name="password" placeholder="Mật khẩu"><br><br>
    <label><input type="checkbox" name="remember"> Nhớ tôi</label>
    &nbsp;|&nbsp;
    <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a><br><br>
    <button type="submit">Đăng nhập</button>
</form>
<p>Nếu bạn chưa có tài khoản, hãy <a href="${pageContext.request.contextPath}/register">Đăng ký</a></p>
</body>
</html>