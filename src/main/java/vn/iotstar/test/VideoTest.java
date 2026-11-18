package vn.iotstar.test;

import vn.iotstar.model.User;
import vn.iotstar.model.Video;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.service.impl.VideoServiceImpl;

public class VideoTest {
    public static void main(String[] args) {
        VideoService service = new VideoServiceImpl();
        User u = new UserServiceImpl().get("khang");

        Video v = new Video();
        v.setTitle("Video test");
        v.setDescription("mo ta");
        v.setFilename("00000000-0000-0000-0000-000000000000.mp4");
        v.setUser(u);
        service.insert(v);

        service.latest(10).forEach(x ->
            System.out.println(x.getId() + " | " + x.getTitle() + " | " + x.getUser().getFullName()));
        System.out.println("Tong video cua user: " + service.countByUser(u.getId()));
        System.exit(0);
    }
}