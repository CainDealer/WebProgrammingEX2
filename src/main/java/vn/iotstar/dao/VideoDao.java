package vn.iotstar.dao;

import java.util.List;

import vn.iotstar.model.Video;

public interface VideoDao {
    void insert(Video video);
    void update(Video video);
    void delete(int id);
    Video findById(int id);
    List<Video> findLatest(int limit);
    List<Video> findByUser(int userId, int page, int size);
    long countByUser(int userId);
    void increaseViews(int id);
}