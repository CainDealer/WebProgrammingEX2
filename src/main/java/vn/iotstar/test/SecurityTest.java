package vn.iotstar.test;

import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.PasswordUtil;

public class SecurityTest {
    public static void main(String[] args) {
        String h1 = PasswordUtil.hash("123456");
        String h2 = PasswordUtil.hash("123456");
        System.out.println("Hash 1: " + h1);
        System.out.println("Hash 2: " + h2);
        System.out.println("Hai hash khac nhau (salt): " + !h1.equals(h2));
        System.out.println("Verify dung: " + PasswordUtil.verify("123456", h1));
        System.out.println("Verify sai : " + PasswordUtil.verify("sai", h1));

        UserService s = new UserServiceImpl();
        System.out.println("Login khang : " + (s.login("khang", "123456") != null));
        System.out.println("Login lan 2   : " + (s.login("khang", "123456") != null));
        System.out.println("Login sai mk  : " + (s.login("khang", "sai") == null));
        System.exit(0);
    }
}