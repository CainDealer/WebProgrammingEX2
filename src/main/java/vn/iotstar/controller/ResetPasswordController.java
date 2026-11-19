package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.OtpResult;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/reset-password")
public class ResetPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("resetIdentifier") == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }
        Object flash = session.getAttribute("flash");
        if (flash != null) {
            req.setAttribute("info", flash);
            session.removeAttribute("flash");
        }
        req.getRequestDispatcher("/views/reset-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("resetIdentifier") == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }
        String identifier = (String) session.getAttribute("resetIdentifier");
        String otp = vn.iotstar.util.ValidationUtil.trim(req.getParameter("otp"));
        String newPassword = req.getParameter("newPassword");
        String confirm = req.getParameter("confirmPassword");

        java.util.Map<String, String> errors = new java.util.LinkedHashMap<>();
        vn.iotstar.util.ValidationUtil.put(errors, "otp", vn.iotstar.util.ValidationUtil.checkOtp(otp));
        vn.iotstar.util.ValidationUtil.put(errors, "newPassword", vn.iotstar.util.ValidationUtil.checkNewPassword(newPassword));
        if (!errors.containsKey("newPassword")) {
            vn.iotstar.util.ValidationUtil.put(errors, "confirmPassword",
                    vn.iotstar.util.ValidationUtil.checkConfirm(newPassword, confirm));
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/views/reset-password.jsp").forward(req, resp);
            return;
        }

        UserService service = new UserServiceImpl();
        OtpResult result = service.resetPassword(identifier, otp, newPassword);

        switch (result) {
        case OK:
            session.removeAttribute("resetIdentifier");
            session.removeAttribute("lastResetSent");
            resp.sendRedirect(req.getContextPath() + "/login?reset=1");
            return;
        case INVALID:
            errors.put("otp", "Mã OTP không đúng.");
            break;
        case LOCKED:
            req.setAttribute("alert", "Bạn đã nhập sai quá nhiều lần. Hãy yêu cầu mã mới.");
            break;
        default:
            req.setAttribute("alert", "Mã OTP không hợp lệ hoặc đã hết hạn. Hãy yêu cầu mã mới.");
        }
        req.setAttribute("errors", errors);
        req.getRequestDispatcher("/views/reset-password.jsp").forward(req, resp);
    }
}