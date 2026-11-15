package vn.iotstar.test;

import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;

public class RegisterTest {
    public static void main(String[] args) {
        UserService service = new UserServiceImpl();

        System.out.println("Email trung   : " + service.checkExistEmail("khangnguyen@gmail.com"));  
        System.out.println("Username trung: " + service.checkExistUsername("admin"));            

        boolean ok = service.register("sv01", "123", "sv01@gmail.com", "Sinh Vien 01", "0911111111");
        System.out.println("Dang ky sv01  : " + ok);                                            
        System.out.println("Dang nhap sv01: " + (service.login("sv01", "123") != null));         

        System.exit(0);
    }
}