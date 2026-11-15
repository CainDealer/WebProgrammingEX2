package vn.iotstar.test;

import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;

public class JPATest {
    public static void main(String[] args) {
        UserService service = new UserServiceImpl();

        User ok = service.login("khang", "123456");
        System.out.println("Dang nhap dung: " + (ok != null ? ok.getFullName() + " - role " + ok.getRoleid() : "THAT BAI"));

        User fail = service.login("khang", "sai_mat_khau");
        System.out.println("Dang nhap sai : " + (fail == null ? "OK bi tu choi" : "LOI"));

        System.exit(0);
    }
}