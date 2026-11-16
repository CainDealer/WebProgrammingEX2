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
@WebServlet(urlPatterns = "/verify")
public class VerifyController extends HttpServlet {
    private static final int MAX_ATTEMPTS = 5;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("pendingUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        Object flash = session.getAttribute("flash");
        if (flash != null) {
            req.setAttribute("info", flash);
            session.removeAttribute("flash");
        }
        req.getRequestDispatcher("/views/verify.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("pendingUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String username = (String) session.getAttribute("pendingUsername");
        String otp = req.getParameter("otp");

        int attempts = session.getAttribute("otpAttempts") == null
                ? 0 : (Integer) session.getAttribute("otpAttempts");
        if (attempts >= MAX_ATTEMPTS) {
            req.setAttribute("alert", "Bạn đã nhập sai quá " + MAX_ATTEMPTS + " lần. Hãy bấm \"Gửi lại mã\" để nhận mã mới.");
            req.getRequestDispatcher("/views/verify.jsp").forward(req, resp);
            return;
        }

        UserService service = new UserServiceImpl();
        OtpResult result = service.activate(username, otp);

        switch (result) {
        case OK:
            session.removeAttribute("pendingUsername");
            session.removeAttribute("otpAttempts");
            session.removeAttribute("lastOtpSent");
            resp.sendRedirect(req.getContextPath() + "/login?activated=1");
            return;
        case EXPIRED:
            req.setAttribute("alert", "Mã OTP đã hết hạn. Hãy bấm \"Gửi lại mã\".");
            break;
        case INVALID:
            session.setAttribute("otpAttempts", attempts + 1);
            req.setAttribute("alert", "Mã OTP không đúng. Còn " + (MAX_ATTEMPTS - attempts - 1) + " lần thử.");
            break;
        default:
            req.setAttribute("alert", "Không tìm thấy tài khoản.");
        }
        req.getRequestDispatcher("/views/verify.jsp").forward(req, resp);
    }
}