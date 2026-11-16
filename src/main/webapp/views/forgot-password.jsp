<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Quên mật khẩu</title>
</head>
<body>
    <h2>Quên mật khẩu</h2>
    <p>Nhập email hoặc tên tài khoản. Chúng tôi sẽ gửi mã OTP tới email đã đăng ký.</p>

    <c:if test="${alert != null}">
        <p style="color:red">${alert}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/forgot-password" method="post">
        <input type="text" name="identifier" placeholder="Email hoặc tên tài khoản" required>
        <button type="submit">Gửi mã OTP</button>
    </form>

    <p><a href="${pageContext.request.contextPath}/login">Quay về đăng nhập</a></p>
</body>
</html>