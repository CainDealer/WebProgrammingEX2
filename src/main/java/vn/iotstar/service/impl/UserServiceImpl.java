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

public class UserServiceImpl implements UserService {
    private static final long OTP_LIFETIME_MS = 5 * 60 * 1000L;   // 5 mins

    UserDao userDao = new UserDaoImpl();

    @Override
    public User login(String username, String password) {
        User user = this.get(username);
        if (user != null && password.equals(user.getPassWord())) {
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
        User user = new User(email, username, fullname, password, null, 5, phone, new Date());
        user.setStatus(0);
        userDao.insert(user);
        return true;
    }

    @Override
    public boolean checkExistEmail(String email) {
        return userDao.checkExistEmail(email);
    }

    @Override
    public boolean checkExistUsername(String username) {
        return userDao.checkExistUsername(username);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return userDao.checkExistPhone(phone);
    }

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
        if (user == null) {
            return OtpResult.NOT_FOUND;
        }
        if (user.getStatus() == 1) {
            return OtpResult.OK;
        }
        if (user.getCode() == null || user.getOtpExpired() == null
                || new Date().after(user.getOtpExpired())) {
            return OtpResult.EXPIRED;
        }
        String input = otp == null ? "" : otp.trim();
        boolean match = MessageDigest.isEqual(
                user.getCode().getBytes(StandardCharsets.UTF_8),
                input.getBytes(StandardCharsets.UTF_8));
        if (!match) {
            return OtpResult.INVALID;
        }
        user.setStatus(1);
        user.setCode(null);
        user.setOtpExpired(null);
        userDao.update(user);
        return OtpResult.OK;
    }
}