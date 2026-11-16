package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {

    private static final int REMEMBER_DAYS = 7;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("account") != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Constant.COOKIE_REMEMBER)) {
                    User user = new UserServiceImpl().getByRememberToken(cookie.getValue());
                    if (user != null && user.getStatus() == 1) {
                        session = req.getSession(true);
                        session.setAttribute("account", user);
                        resp.sendRedirect(req.getContextPath() + "/waiting");
                        return;
                    }
                    deleteRememberCookie(req, resp);
                }
            }
        }
        req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        boolean isRememberMe = "on".equals(req.getParameter("remember"));

        if (username == null || password == null || username.trim().isEmpty() || password.isEmpty()) {
            req.setAttribute("alert", "Tài khoản hoặc mật khẩu không được rỗng");
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
            return;
        }

        UserService service = new UserServiceImpl();
        User user = service.login(username.trim(), password);

        if (user == null) {
            req.setAttribute("alert", "Tài khoản hoặc mật khẩu không đúng");
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
            return;
        }

        if (user.getStatus() == 0) {
            HttpSession pending = req.getSession(true);
            pending.setAttribute("pendingUsername", user.getUserName());
            pending.setAttribute("otpAttempts", 0);
            pending.setAttribute("flash", "Tài khoản chưa được kích hoạt. Nhập mã OTP đã gửi qua email, hoặc bấm \"Gửi lại mã\".");
            resp.sendRedirect(req.getContextPath() + "/verify");
            return;
        }

        HttpSession session = req.getSession(true);
        req.changeSessionId();
        session.setAttribute("account", user);

        if (isRememberMe) {
            String token = service.createRememberToken(user.getUserName());
            if (token != null) {
                addRememberCookie(req, resp, token);
            }
        }
        resp.sendRedirect(req.getContextPath() + "/waiting");
    }

    private void addRememberCookie(HttpServletRequest req, HttpServletResponse resp, String token) {
        Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, token);
        cookie.setMaxAge(REMEMBER_DAYS * 24 * 60 * 60);
        cookie.setHttpOnly(true);
        cookie.setSecure(req.isSecure());
        cookie.setPath(cookiePath(req));
        resp.addCookie(cookie);
    }

    private void deleteRememberCookie(HttpServletRequest req, HttpServletResponse resp) {
        Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath(cookiePath(req));
        resp.addCookie(cookie);
    }

    private String cookiePath(HttpServletRequest req) {
        String p = req.getContextPath();
        return (p == null || p.isEmpty()) ? "/" : p;
    }
}