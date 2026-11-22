package vn.iotstar.controller;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.CloudinaryUtil;
import vn.iotstar.util.CsrfUtil;
import vn.iotstar.util.EmailChangeResult;
import vn.iotstar.util.UploadUtil;
import vn.iotstar.util.ValidationUtil;
import vn.iotstar.util.WebUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/profile")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
                 maxFileSize = 3L * 1024 * 1024,
                 maxRequestSize = 4L * 1024 * 1024)
public class ProfileController extends HttpServlet {
    private static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_AVATAR = 2L * 1024 * 1024;   // 2 MB

    private final UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User me = service.get(WebUtil.account(req).getUserName());
        if (me == null) { resp.sendRedirect(req.getContextPath() + "/logout"); return; }
        show(req, resp, me, formOf(me), new LinkedHashMap<>());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User me = service.get(WebUtil.account(req).getUserName());
        if (me == null) { resp.sendRedirect(req.getContextPath() + "/logout"); return; }

        try {
            handlePost(req, resp, me);
        } catch (IllegalStateException e) {
            Map<String, String> errors = new LinkedHashMap<>();
            errors.put("avatar", "Ảnh quá lớn (tối đa 2 MB).");
            show(req, resp, me, formOf(me), errors);
        }
    }

    private void handlePost(HttpServletRequest req, HttpServletResponse resp, User me)
            throws ServletException, IOException {
        if (!CsrfUtil.valid(req)) { resp.sendError(403, "CSRF token không hợp lệ."); return; }

        String fullname = ValidationUtil.trim(req.getParameter("fullname"));
        String phone = ValidationUtil.trim(req.getParameter("phone"));
        String email = ValidationUtil.trim(req.getParameter("email")).toLowerCase();

        Map<String, String> form = new LinkedHashMap<>();
        form.put("fullname", fullname);
        form.put("phone", phone);
        form.put("email", email);

        Map<String, String> errors = new LinkedHashMap<>();
        ValidationUtil.put(errors, "fullname", ValidationUtil.checkFullname(fullname));
        ValidationUtil.put(errors, "phone", ValidationUtil.checkPhone(phone));
        ValidationUtil.put(errors, "email", ValidationUtil.checkEmail(email));

        if (!errors.containsKey("phone")) {
            User o = service.getByPhone(phone);
            if (o != null && o.getId() != me.getId()) errors.put("phone", "Số điện thoại đã được sử dụng.");
        }
        boolean emailChanged = !email.equalsIgnoreCase(me.getEmail());
        if (!errors.containsKey("email") && emailChanged) {
            User o = service.getByEmail(email);
            if (o != null && o.getId() != me.getId()) errors.put("email", "Email đã được sử dụng.");
        }

        Part avatar = req.getPart("avatar");
        boolean hasAvatar = avatar != null && avatar.getSize() > 0;
        if (hasAvatar) {
            String e = UploadUtil.check(avatar, IMAGE_EXT, "image/", MAX_AVATAR);
            if (e != null) errors.put("avatar", e);
        }
        if (!errors.isEmpty()) { show(req, resp, me, form, errors); return; }

        CloudinaryUtil.Uploaded up = null;
        if (hasAvatar) {
            try {
                up = CloudinaryUtil.uploadAvatar(avatar);
            } catch (IllegalArgumentException e) {
                errors.put("avatar", e.getMessage());
                show(req, resp, me, form, errors);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                errors.put("avatar", "Không tải được ảnh lên Cloudinary. Kiểm tra cấu hình và thử lại.");
                show(req, resp, me, form, errors);
                return;
            }
        }

        String oldPublicId = me.getAvatarPublicId();
        User updated = service.updateProfile(me.getUserName(), fullname, phone,
                up == null ? null : up.url, up == null ? null : up.publicId);
        if (up != null) CloudinaryUtil.delete(oldPublicId);
        HttpSession session = req.getSession();
        session.setAttribute("account", updated);

        if (emailChanged) {
            EmailChangeResult r = service.requestEmailChange(me.getUserName(), email);
            switch (r) {
            case OK:
                session.setAttribute("lastEmailOtpSent", System.currentTimeMillis());
                session.setAttribute("flash", "Mã OTP đã được gửi tới " + email
                        + ". Nhập mã để hoàn tất đổi email (hiệu lực 5 phút).");
                resp.sendRedirect(req.getContextPath() + "/profile/verify-email");
                return;
            case EXISTS:
                errors.put("email", "Email đã được sử dụng.");
                break;
            default:
                req.setAttribute("alert", "Đã lưu thông tin khác, nhưng không gửi được OTP tới email mới. Vui lòng thử lại.");
            }
            show(req, resp, updated, form, errors);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/profile?msg=saved");
    }

    private Map<String, String> formOf(User u) {
        Map<String, String> f = new LinkedHashMap<>();
        f.put("fullname", u.getFullName());
        f.put("phone", u.getPhone());
        f.put("email", u.getEmail());
        return f;
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, User me,
                      Map<String, String> form, Map<String, String> errors)
            throws ServletException, IOException {
        req.setAttribute("me", me);
        req.setAttribute("form", form);
        req.setAttribute("errors", errors);
        req.setAttribute("csrf", CsrfUtil.token(req.getSession()));
        if (me.getNewEmail() != null && me.getEmailExpired() != null
                && me.getEmailExpired().after(new Date())) {
            req.setAttribute("pendingEmail", me.getNewEmail());
        }
        req.getRequestDispatcher("/views/profile.jsp").forward(req, resp);
    }
}