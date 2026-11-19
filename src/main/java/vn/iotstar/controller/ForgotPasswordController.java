package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.iotstar.service.impl.UserServiceImpl;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/forgot-password")
public class ForgotPasswordController extends HttpServlet {
    private static final long COOLDOWN_MS = 60 * 1000L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String identifier = vn.iotstar.util.ValidationUtil.trim(req.getParameter("identifier"));
        String err = vn.iotstar.util.ValidationUtil.checkIdentifier(identifier);
        if (err != null) {
            java.util.Map<String, String> errors = new java.util.LinkedHashMap<>();
            errors.put("identifier", err);
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);

        long last = session.getAttribute("lastResetSent") == null
                ? 0 : (Long) session.getAttribute("lastResetSent");
        long wait = COOLDOWN_MS - (System.currentTimeMillis() - last);
        if (wait > 0) {
            req.setAttribute("alert", "Vui lòng đợi " + (wait / 1000 + 1) + " giây rồi thử lại.");
            req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
            return;
        }

        new UserServiceImpl().sendResetOtp(identifier);
        session.setAttribute("lastResetSent", System.currentTimeMillis());
        session.setAttribute("resetIdentifier", identifier);
        session.setAttribute("flash", "Nếu tài khoản tồn tại, mã OTP đã được gửi tới email đăng ký. "
                + "Vui lòng kiểm tra hộp thư (cả mục Spam).");
        resp.sendRedirect(req.getContextPath() + "/reset-password");
    }
}