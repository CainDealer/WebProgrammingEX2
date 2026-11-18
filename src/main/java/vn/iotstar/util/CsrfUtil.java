package vn.iotstar.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class CsrfUtil {
    private static final String KEY = "csrfToken";

    public static String token(HttpSession session) {
        String t = (String) session.getAttribute(KEY);
        if (t == null) {
            t = TokenUtil.generate();
            session.setAttribute(KEY, t);
        }
        return t;
    }

    public static boolean valid(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return false;
        String expected = (String) s.getAttribute(KEY);
        String actual = req.getParameter("csrf");
        return expected != null && actual != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }
}