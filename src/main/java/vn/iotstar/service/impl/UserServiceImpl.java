package vn.iotstar.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.util.EmailUtil;
import vn.iotstar.util.OtpResult;
import vn.iotstar.util.OtpUtil;
import vn.iotstar.util.PasswordUtil;
import vn.iotstar.util.TokenUtil;
import vn.iotstar.util.EmailChangeResult;

public class UserServiceImpl implements UserService {
    private static final long OTP_LIFETIME_MS = 5 * 60 * 1000L;   // 5 mins
    private static final int MAX_RESET_ATTEMPTS = 5;

    UserDao userDao = new UserDaoImpl();

    @Override
    public User login(String username, String password) {
        User user = userDao.get(username);
        if (user == null || password == null) {
            return null;
        }
        String stored = user.getPassWord();

        if (PasswordUtil.isHashed(stored)) {
            return PasswordUtil.verify(password, stored) ? user : null;
        }

        if (stored != null && MessageDigest.isEqual(
                stored.getBytes(StandardCharsets.UTF_8),
                password.getBytes(StandardCharsets.UTF_8))) {
            user.setPassWord(PasswordUtil.hash(password));
            userDao.update(user);
            return user;
        }
        return null;
    }

    @Override
    public User get(String username) {
        return userDao.get(username);
    }

    @Override
    public void insert(User user) {
        userDao.insert(user);
    }

    @Override
    public boolean register(String username, String password, String email, String fullname, String phone) {
        if (userDao.checkExistUsername(username)) {
            return false;
        }
        User user = new User(email, username, fullname, PasswordUtil.hash(password), null, 5, phone, new Date());
        user.setStatus(0);
        userDao.insert(user);
        return true;
    }

    @Override
    public boolean checkExistEmail(String email) { return userDao.checkExistEmail(email); }

    @Override
    public boolean checkExistUsername(String username) { return userDao.checkExistUsername(username); }

    @Override
    public boolean checkExistPhone(String phone) { return userDao.checkExistPhone(phone); }

    @Override
    public boolean sendOtp(String username) {
        User user = userDao.get(username);
        if (user == null || user.getStatus() == 1) {
            return false;
        }
        String otp = OtpUtil.generate();
        user.setCode(otp);
        user.setOtpExpired(new Date(System.currentTimeMillis() + OTP_LIFETIME_MS));
        userDao.update(user);

        String body = "Xin chào " + user.getFullName() + ",\n\n"
                + "Mã OTP kích hoạt tài khoản của bạn là: " + otp + "\n"
                + "Mã có hiệu lực trong 5 phút. Không chia sẻ mã này cho bất kỳ ai.\n\n"
                + "Nếu bạn không đăng ký tài khoản, hãy bỏ qua email này.";
        return EmailUtil.send(user.getEmail(), "Mã kích hoạt tài khoản", body);
    }

    @Override
    public OtpResult activate(String username, String otp) {
        User user = userDao.get(username);
        if (user == null) return OtpResult.NOT_FOUND;
        if (user.getStatus() == 1) return OtpResult.OK;
        if (user.getCode() == null || user.getOtpExpired() == null
                || new Date().after(user.getOtpExpired())) {
            return OtpResult.EXPIRED;
        }
        if (!sameText(user.getCode(), otp)) {
            return OtpResult.INVALID;
        }
        user.setStatus(1);
        user.setCode(null);
        user.setOtpExpired(null);
        userDao.update(user);
        return OtpResult.OK;
    }

    @Override
    public String createRememberToken(String username) {
        User user = userDao.get(username);
        if (user == null) return null;
        String token = TokenUtil.generate();
        user.setRememberToken(TokenUtil.sha256(token));
        userDao.update(user);
        return token;
    }

    @Override
    public User getByRememberToken(String token) {
        if (token == null || token.isEmpty()) return null;
        return userDao.getByRememberToken(TokenUtil.sha256(token));
    }

    @Override
    public void clearRememberToken(String username) {
        User user = userDao.get(username);
        if (user != null && user.getRememberToken() != null) {
            user.setRememberToken(null);
            userDao.update(user);
        }
    }

    @Override
    public boolean sendResetOtp(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) return false;
        String id = identifier.trim();
        User user = id.contains("@") ? userDao.getByEmail(id) : userDao.get(id);

        if (user == null || user.getStatus() != 1) return false;

        String otp = OtpUtil.generate();
        user.setResetCode(otp);
        user.setResetExpired(new Date(System.currentTimeMillis() + OTP_LIFETIME_MS));
        user.setResetAttempts(0);
        userDao.update(user);

        String body = "Xin chào " + user.getFullName() + ",\n\n"
                + "Mã OTP đặt lại mật khẩu của bạn là: " + otp + "\n"
                + "Mã có hiệu lực trong 5 phút. Không chia sẻ mã này cho bất kỳ ai.\n\n"
                + "Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.";
        return EmailUtil.send(user.getEmail(), "Mã đặt lại mật khẩu", body);
    }

    @Override
    public OtpResult resetPassword(String identifier, String otp, String newPassword) {
        if (identifier == null) return OtpResult.NOT_FOUND;
        String id = identifier.trim();
        User user = id.contains("@") ? userDao.getByEmail(id) : userDao.get(id);
        if (user == null) return OtpResult.NOT_FOUND;

        if (user.getResetCode() == null || user.getResetExpired() == null
                || new Date().after(user.getResetExpired())) {
            return OtpResult.EXPIRED;
        }
        if (user.getResetAttempts() >= MAX_RESET_ATTEMPTS) {
            return OtpResult.LOCKED;
        }
        if (!sameText(user.getResetCode(), otp)) {
            user.setResetAttempts(user.getResetAttempts() + 1);
            userDao.update(user);
            return OtpResult.INVALID;
        }

        user.setPassWord(PasswordUtil.hash(newPassword));
        user.setResetCode(null);
        user.setResetExpired(null);
        user.setResetAttempts(0);
        user.setRememberToken(null);
        userDao.update(user);
        return OtpResult.OK;
    }

    private boolean sameText(String a, String b) {
        String input = b == null ? "" : b.trim();
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                input.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public User getByEmail(String email) { return userDao.getByEmail(email); }

    @Override
    public User getByPhone(String phone) { return userDao.getByPhone(phone); }

    
    @Override
    public User updateProfile(String username, String fullname, String phone,
                              String avatarUrl, String avatarPublicId) {
        User u = userDao.get(username);
        if (u == null) return null;
        u.setFullName(fullname);
        u.setPhone(phone);
        if (avatarUrl != null) {
            u.setAvatar(avatarUrl);
            u.setAvatarPublicId(avatarPublicId);
        }
        userDao.update(u);
        return u;
    }

    @Override
    public EmailChangeResult requestEmailChange(String username, String newEmail) {
        User u = userDao.get(username);
        if (u == null) return EmailChangeResult.NOT_FOUND;
        String e = newEmail.trim().toLowerCase();
        if (e.equalsIgnoreCase(u.getEmail())) return EmailChangeResult.SAME;
        User other = userDao.getByEmail(e);
        if (other != null && other.getId() != u.getId()) return EmailChangeResult.EXISTS;
        return issueEmailOtp(u, e) ? EmailChangeResult.OK : EmailChangeResult.SEND_FAILED;
    }

    @Override
    public boolean resendEmailOtp(String username) {
        User u = userDao.get(username);
        if (u == null || u.getNewEmail() == null) return false;
        return issueEmailOtp(u, u.getNewEmail());
    }

    private boolean issueEmailOtp(User u, String newEmail) {
        String otp = OtpUtil.generate();
        u.setNewEmail(newEmail);
        u.setEmailCode(otp);
        u.setEmailExpired(new Date(System.currentTimeMillis() + OTP_LIFETIME_MS));
        u.setEmailAttempts(0);
        userDao.update(u);

        String body = "Xin chào " + u.getFullName() + ",\n\n"
                + "Mã OTP xác nhận đổi email của bạn là: " + otp + "\n"
                + "Mã có hiệu lực trong 5 phút. Không chia sẻ mã này cho bất kỳ ai.\n\n"
                + "Nếu bạn không yêu cầu đổi email, hãy bỏ qua email này.";
        return EmailUtil.send(newEmail, "Xác nhận email mới", body);
    }

    @Override
    public OtpResult confirmEmailChange(String username, String otp) {
        User u = userDao.get(username);
        if (u == null) return OtpResult.NOT_FOUND;

        if (u.getNewEmail() == null || u.getEmailCode() == null || u.getEmailExpired() == null
                || new Date().after(u.getEmailExpired())) {
            return OtpResult.EXPIRED;
        }
        if (u.getEmailAttempts() >= MAX_RESET_ATTEMPTS) {
            return OtpResult.LOCKED;
        }
        if (!sameText(u.getEmailCode(), otp)) {
            u.setEmailAttempts(u.getEmailAttempts() + 1);
            userDao.update(u);
            return OtpResult.INVALID;
        }

        User other = userDao.getByEmail(u.getNewEmail());
        if (other != null && other.getId() != u.getId()) {
            clearPendingEmail(u);
            userDao.update(u);
            return OtpResult.CONFLICT;
        }

        String oldEmail = u.getEmail();
        String newEmail = u.getNewEmail();
        u.setEmail(newEmail);
        clearPendingEmail(u);
        userDao.update(u);

        if (oldEmail != null && !oldEmail.equalsIgnoreCase(newEmail)) {
            EmailUtil.send(oldEmail, "Email tài khoản đã được thay đổi",
                    "Email của tài khoản " + u.getUserName() + " vừa được đổi sang " + newEmail + ".\n"
                    + "Nếu không phải bạn thực hiện, hãy đặt lại mật khẩu ngay.");
        }
        return OtpResult.OK;
    }

    @Override
    public void cancelEmailChange(String username) {
        User u = userDao.get(username);
        if (u != null && u.getNewEmail() != null) {
            clearPendingEmail(u);
            userDao.update(u);
        }
    }

    private void clearPendingEmail(User u) {
        u.setNewEmail(null);
        u.setEmailCode(null);
        u.setEmailExpired(null);
        u.setEmailAttempts(0);
    }
}