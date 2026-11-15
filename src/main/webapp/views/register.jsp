<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Đăng ký</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/register" method="post">
    <h2>Tạo tài khoản mới</h2>

    <c:if test="${alert != null}">
        <h3 style="color:red">${alert}</h3>
    </c:if>

    <input type="text" name="username" placeholder="Tài khoản"><br><br>
    <input type="text" name="fullname" placeholder="Họ tên"><br><br>
    <input type="text" name="email" placeholder="Nhập Email"><br><br>
    <input type="text" name="phone" placeholder="Số điện thoại"><br><br>
    <input type="password" name="password" placeholder="Mật khẩu"><br><br>
    <button type="submit">Tạo tài khoản</button>
</form>
<p>Nếu bạn đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập</a></p>
</body>
</html>