package vn.iotstar.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }

    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith("$2");
    }

    public static boolean verify(String plain, String hashed) {
        try {
            return plain != null && hashed != null && BCrypt.checkpw(plain, hashed);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}