package vn.iotstar.service;

import vn.iotstar.model.User;
import vn.iotstar.util.OtpResult;
import vn.iotstar.util.EmailChangeResult;

public interface UserService {
    User login(String username, String password);
    User get(String username);
    User getByEmail(String email);
    User getByPhone(String phone);

    User updateProfile(String username, String fullname, String phone, String avatarUrl, String avatarPublicId);
    EmailChangeResult requestEmailChange(String username, String newEmail);
    boolean resendEmailOtp(String username);
    OtpResult confirmEmailChange(String username, String otp);
    void cancelEmailChange(String username);
    
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