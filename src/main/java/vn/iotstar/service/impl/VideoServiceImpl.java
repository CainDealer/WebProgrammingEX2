package vn.iotstar.service.impl;

import java.util.List;

import vn.iotstar.dao.VideoDao;
import vn.iotstar.dao.impl.VideoDaoImpl;
import vn.iotstar.model.User;
import vn.iotstar.model.Video;
import vn.iotstar.service.VideoService;
import vn.iotstar.util.UploadUtil;

public class VideoServiceImpl implements VideoService {
    private final VideoDao dao = new VideoDaoImpl();

    @Override public Video get(int id) { return dao.findById(id); }
    @Override public List<Video> latest(int limit) { return dao.findLatest(limit); }
    @Override public List<Video> getByUser(int userId, int page, int size) { return dao.findByUser(userId, page, size); }
    @Override public long countByUser(int userId) { return dao.countByUser(userId); }
    @Override public void insert(Video video) { dao.insert(video); }
    @Override public void update(Video video) { dao.update(video); }
    @Override public void increaseViews(int id) { dao.increaseViews(id); }

    @Override
    public void delete(int id) {
        Video v = dao.findById(id);
        if (v == null) return;
        dao.delete(id);
        UploadUtil.delete("videos", v.getFilename());
        UploadUtil.delete("posters", v.getPoster());
    }

    @Override
    public boolean canModify(User user, Video video) {
        return user != null && video != null
                && (user.getRoleid() == 1 || video.getUser().getId() == user.getId());
    }
}