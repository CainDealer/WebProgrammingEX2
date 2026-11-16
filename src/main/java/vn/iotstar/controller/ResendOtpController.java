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

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/resend-otp")
public class ResendOtpController extends HttpServlet {
    private static final long COOLDOWN_MS = 60 * 1000L;   // 60s 

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("pendingUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String username = (String) session.getAttribute("pendingUsername");

        long last = session.getAttribute("lastOtpSent") == null
                ? 0 : (Long) session.getAttribute("lastOtpSent");
        long wait = COOLDOWN_MS - (System.currentTimeMillis() - last);
        if (wait > 0) {
            session.setAttribute("flash", "Vui lòng đợi " + (wait / 1000 + 1) + " giây rồi gửi lại.");
            resp.sendRedirect(req.getContextPath() + "/verify");
            return;
        }

        boolean sent = new UserServiceImpl().sendOtp(username);
        session.setAttribute("lastOtpSent", System.currentTimeMillis());
        session.setAttribute("otpAttempts", 0);
        session.setAttribute("flash", sent
                ? "Đã gửi mã OTP mới. Mã cũ không còn hiệu lực."
                : "Không gửi được email. Vui lòng thử lại sau.");
        resp.sendRedirect(req.getContextPath() + "/verify");
    }
}