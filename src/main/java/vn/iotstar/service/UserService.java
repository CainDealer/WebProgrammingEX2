package vn.iotstar.service;

import vn.iotstar.model.User;
import vn.iotstar.util.OtpResult;

public interface UserService {
    User login(String username, String password);
    User get(String username);
    void insert(User user);
    boolean register(String username, String password, String email, String fullname, String phone);
    boolean checkExistEmail(String email);
    boolean checkExistUsername(String username);
    boolean checkExistPhone(String phone);

    boolean sendOtp(String username);
    OtpResult activate(String username, String otp);

    String createRememberToken(String username);
    User getByRememberToken(String token);
    void clearRememberToken(String username);

    boolean sendResetOtp(String identifier);
    OtpResult resetPassword(String identifier, String otp, String newPassword);
}