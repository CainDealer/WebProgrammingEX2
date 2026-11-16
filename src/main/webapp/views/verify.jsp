<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Kích hoạt tài khoản</title>
</head>
<body>
    <h2>Kích hoạt tài khoản</h2>

    <c:if test="${info != null}">
        <p style="color:green">${info}</p>
    </c:if>
    <c:if test="${alert != null}">
        <p style="color:red">${alert}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/verify" method="post">
        <input type="text" name="otp" placeholder="Nhập mã OTP 6 số"
               maxlength="6" pattern="[0-9]{6}" inputmode="numeric" autocomplete="one-time-code" required>
        <button type="submit">Xác nhận</button>
    </form>

    <form action="${pageContext.request.contextPath}/resend-otp" method="post" style="margin-top:10px">
        <button type="submit">Gửi lại mã</button>
    </form>

    <p><a href="${pageContext.request.contextPath}/login">Quay về đăng nhập</a></p>
</body>
</html>