package vn.iotstar.service;

import java.util.List;

import vn.iotstar.model.User;
import vn.iotstar.model.Video;

public interface VideoService {
    Video get(int id);
    List<Video> latest(int limit);
    List<Video> getByUser(int userId, int page, int size);
    long countByUser(int userId);
    void insert(Video video);
    void update(Video video);
    void delete(int id);
    void increaseViews(int id);
    boolean canModify(User user, Video video);
}