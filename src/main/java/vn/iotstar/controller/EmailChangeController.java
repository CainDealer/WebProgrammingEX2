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

import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.CsrfUtil;
import vn.iotstar.util.OtpResult;
import vn.iotstar.util.ValidationUtil;
import vn.iotstar.util.WebUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/profile/verify-email", "/profile/resend-email", "/profile/cancel-email" })
public class EmailChangeController extends HttpServlet {
    private static final long COOLDOWN_MS = 60 * 1000L;
    private final UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!req.getServletPath().equals("/profile/verify-email")) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }
        User me = service.get(WebUtil.account(req).getUserName());
        if (me == null || me.getNewEmail() == null) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }
        HttpSession session = req.getSession();
        Object flash = session.getAttribute("flash");
        if (flash != null) {
            req.setAttribute("info", flash);
            session.removeAttribute("flash");
        }
        show(req, resp, me, new LinkedHashMap<>());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        if (!CsrfUtil.valid(req)) { resp.sendError(403, "CSRF token không hợp lệ."); return; }

        User acc = WebUtil.account(req);
        User me = service.get(acc.getUserName());
        if (me == null) { resp.sendRedirect(req.getContextPath() + "/logout"); return; }

        HttpSession session = req.getSession();
        String path = req.getServletPath();

        if (path.equals("/profile/cancel-email")) {
            service.cancelEmailChange(me.getUserName());
            resp.sendRedirect(req.getContextPath() + "/profile?msg=cancelled");
            return;
        }

        if (path.equals("/profile/resend-email")) {
            long last = session.getAttribute("lastEmailOtpSent") == null
                    ? 0 : (Long) session.getAttribute("lastEmailOtpSent");
            long wait = COOLDOWN_MS - (System.currentTimeMillis() - last);
            if (wait > 0) {
                session.setAttribute("flash", "Vui lòng đợi " + (wait / 1000 + 1) + " giây rồi gửi lại.");
            } else {
                boolean sent = service.resendEmailOtp(me.getUserName());
                session.setAttribute("lastEmailOtpSent", System.currentTimeMillis());
                session.setAttribute("flash", sent
                        ? "Đã gửi mã OTP mới. Mã cũ không còn hiệu lực."
                        : "Không gửi được email. Vui lòng thử lại sau.");
            }
            resp.sendRedirect(req.getContextPath() + "/profile/verify-email");
            return;
        }

        String otp = ValidationUtil.trim(req.getParameter("otp"));
        Map<String, String> errors = new LinkedHashMap<>();
        String err = ValidationUtil.checkOtp(otp);
        if (err != null) {
            errors.put("otp", err);
            show(req, resp, me, errors);
            return;
        }

        OtpResult r = service.confirmEmailChange(me.getUserName(), otp);
        switch (r) {
        case OK:
            session.setAttribute("account", service.get(me.getUserName()));
            session.removeAttribute("lastEmailOtpSent");
            resp.sendRedirect(req.getContextPath() + "/profile?msg=email-updated");
            return;
        case CONFLICT:
            resp.sendRedirect(req.getContextPath() + "/profile?msg=conflict");
            return;
        case INVALID:
            errors.put("otp", "Mã OTP không đúng.");
            break;
        case LOCKED:
            req.setAttribute("alert", "Bạn đã nhập sai quá nhiều lần. Hãy bấm \"Gửi lại mã\" để nhận mã mới.");
            break;
        default:
            req.setAttribute("alert", "Mã OTP đã hết hạn hoặc không hợp lệ. Hãy bấm \"Gửi lại mã\".");
        }
        show(req, resp, service.get(me.getUserName()), errors);
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, User me, Map<String, String> errors)
            throws ServletException, IOException {
        req.setAttribute("newEmail", me.getNewEmail());
        req.setAttribute("errors", errors);
        req.setAttribute("csrf", CsrfUtil.token(req.getSession()));
        req.getRequestDispatcher("/views/profile-verify-email.jsp").forward(req, resp);
    }
}