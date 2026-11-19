package vn.iotstar.util;

import java.util.Map;
import java.util.regex.Pattern;

public class ValidationUtil {
    public static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{4,30}$");
    public static final Pattern EMAIL    = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    public static final Pattern PHONE    = Pattern.compile("^(0|\\+84)[35789][0-9]{8}$");
    public static final Pattern FULLNAME = Pattern.compile("^[\\p{L}\\p{M} .'-]{2,100}$");
    public static final Pattern PASSWORD = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,64}$");
    public static final Pattern OTP      = Pattern.compile("^\\d{6}$");

    public static String trim(String s) { return s == null ? "" : s.trim(); }

    public static void put(Map<String, String> errors, String field, String msg) {
        if (msg != null) errors.put(field, msg);
    }

    public static String checkUsername(String v) {
        if (v.isEmpty()) return "Vui lòng nhập tên tài khoản.";
        if (!USERNAME.matcher(v).matches())
            return "Tên tài khoản gồm 4–30 ký tự: chữ cái, số hoặc dấu gạch dưới.";
        return null;
    }

    public static String checkFullname(String v) {
        if (v.isEmpty()) return "Vui lòng nhập họ tên.";
        if (!FULLNAME.matcher(v).matches())
            return "Họ tên gồm 2–100 ký tự, chỉ chứa chữ cái và khoảng trắng.";
        return null;
    }

    public static String checkEmail(String v) {
        if (v.isEmpty()) return "Vui lòng nhập email.";
        if (v.length() > 100 || !EMAIL.matcher(v).matches()) return "Email không hợp lệ.";
        return null;
    }

    public static String checkPhone(String v) {
        if (v.isEmpty()) return "Vui lòng nhập số điện thoại.";
        if (!PHONE.matcher(v).matches())
            return "Số điện thoại không hợp lệ (ví dụ 0912345678 hoặc +84912345678).";
        return null;
    }

    public static String checkNewPassword(String v) {
        if (v == null || v.isEmpty()) return "Vui lòng nhập mật khẩu.";
        if (!PASSWORD.matcher(v).matches())
            return "Mật khẩu gồm 8–64 ký tự, có ít nhất 1 chữ cái và 1 chữ số.";
        return null;
    }

    public static String checkConfirm(String password, String confirm) {
        if (confirm == null || confirm.isEmpty()) return "Vui lòng nhập lại mật khẩu.";
        if (!confirm.equals(password)) return "Mật khẩu nhập lại không khớp.";
        return null;
    }

    public static String checkOtp(String v) {
        if (v.isEmpty()) return "Vui lòng nhập mã OTP.";
        if (!OTP.matcher(v).matches()) return "Mã OTP gồm đúng 6 chữ số.";
        return null;
    }

    public static String checkIdentifier(String v) {
        if (v.isEmpty()) return "Vui lòng nhập email hoặc tên tài khoản.";
        if (v.length() > 100) return "Giá trị nhập quá dài.";
        if (v.contains("@") ? !EMAIL.matcher(v).matches() : !USERNAME.matcher(v).matches())
            return "Nhập email hợp lệ hoặc tên tài khoản (4–30 ký tự).";
        return null;
    }
}