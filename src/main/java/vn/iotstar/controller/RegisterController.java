package vn.iotstar.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.ValidationUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("account") != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String username = ValidationUtil.trim(req.getParameter("username"));
        String fullname = ValidationUtil.trim(req.getParameter("fullname"));
        String email = ValidationUtil.trim(req.getParameter("email")).toLowerCase();
        String phone = ValidationUtil.trim(req.getParameter("phone"));
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");

        Map<String, String> errors = new LinkedHashMap<>();
        ValidationUtil.put(errors, "username", ValidationUtil.checkUsername(username));
        ValidationUtil.put(errors, "fullname", ValidationUtil.checkFullname(fullname));
        ValidationUtil.put(errors, "email", ValidationUtil.checkEmail(email));
        ValidationUtil.put(errors, "phone", ValidationUtil.checkPhone(phone));
        ValidationUtil.put(errors, "password", ValidationUtil.checkNewPassword(password));
        if (!errors.containsKey("password")) {
            ValidationUtil.put(errors, "confirmPassword", ValidationUtil.checkConfirm(password, confirm));
        }

        UserService service = new UserServiceImpl();
        if (!errors.containsKey("username") && service.checkExistUsername(username))
            errors.put("username", "Tên tài khoản đã được sử dụng.");
        if (!errors.containsKey("email") && service.checkExistEmail(email))
            errors.put("email", "Email đã được đăng ký.");
        if (!errors.containsKey("phone") && service.checkExistPhone(phone))
            errors.put("phone", "Số điện thoại đã được đăng ký.");

        if (errors.isEmpty() && !service.register(username, password, email, fullname, phone)) {
            errors.put("username", "Tên tài khoản đã được sử dụng."); 
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("pendingUsername", username);
        session.setAttribute("otpAttempts", 0);
        session.setAttribute("lastOtpSent", System.currentTimeMillis());

        boolean sent = service.sendOtp(username);
        session.setAttribute("flash", sent
                ? "Mã OTP đã được gửi tới email " + email + ". Vui lòng kiểm tra hộp thư (cả mục Spam)."
                : "Không gửi được email. Hãy bấm \"Gửi lại mã\".");
        resp.sendRedirect(req.getContextPath() + "/verify");
    }
}