package vn.iotstar.controller;

import java.io.IOException;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import vn.iotstar.model.User;
import vn.iotstar.model.Video;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.CsrfUtil;
import vn.iotstar.util.UploadUtil;
import vn.iotstar.util.WebUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/video/add", "/video/edit" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
                 maxFileSize = 200L * 1024 * 1024,        // tối đa 200 MB
                 maxRequestSize = 210L * 1024 * 1024)
public class VideoFormController extends HttpServlet {
    private static final Set<String> VIDEO_EXT = Set.of("mp4", "webm", "ogg");
    private static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_POSTER = 5L * 1024 * 1024;

    private final VideoService service = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        boolean edit = req.getServletPath().equals("/video/edit");
        Video v = new Video();
        if (edit) {
            v = service.get(WebUtil.parseInt(req.getParameter("id"), -1));
            if (v == null) { resp.sendError(404); return; }
            if (!service.canModify(WebUtil.account(req), v)) { resp.sendError(403); return; }
        }
        showForm(req, resp, v, edit, new LinkedHashMap<>());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            handlePost(req, resp);
        } catch (IllegalStateException e) {
            resp.sendError(413, "File quá lớn (tối đa 200 MB).");
        }
    }

    private static final long MAX_VIDEO = 200L * 1024 * 1024;

    private void handlePost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!CsrfUtil.valid(req)) { resp.sendError(403, "CSRF token không hợp lệ."); return; }

        User acc = WebUtil.account(req);
        boolean edit = req.getServletPath().equals("/video/edit");
        Video v;
        if (edit) {
            v = service.get(WebUtil.parseInt(req.getParameter("id"), -1));
            if (v == null) { resp.sendError(404); return; }
            if (!service.canModify(acc, v)) { resp.sendError(403); return; }
        } else {
            v = new Video();
            v.setUser(acc);
        }

        String title = req.getParameter("title") == null ? "" : req.getParameter("title").trim();
        String desc = req.getParameter("description") == null ? "" : req.getParameter("description").trim();
        v.setTitle(title);
        v.setDescription(desc);
        v.setActive(req.getParameter("active") != null);

        Part videoPart = req.getPart("video");
        Part posterPart = req.getPart("poster");
        boolean hasVideo = videoPart != null && videoPart.getSize() > 0;
        boolean hasPoster = posterPart != null && posterPart.getSize() > 0;

        Map<String, String> errors = new LinkedHashMap<>();
        if (title.isEmpty()) errors.put("title", "Vui lòng nhập tiêu đề.");
        else if (title.length() > 200) errors.put("title", "Tiêu đề tối đa 200 ký tự.");

        if (desc.length() > 4000) errors.put("description", "Mô tả tối đa 4000 ký tự.");

        if (hasVideo) {
            String e = UploadUtil.check(videoPart, VIDEO_EXT, "video/", MAX_VIDEO);
            if (e != null) errors.put("video", e);
        } else if (!edit) {
            errors.put("video", "Vui lòng chọn file video.");
        }
        if (hasPoster) {
            String e = UploadUtil.check(posterPart, IMAGE_EXT, "image/", MAX_POSTER);
            if (e != null) errors.put("poster", e);
        }
        if (!errors.isEmpty()) { showForm(req, resp, v, edit, errors); return; }

        String newVideo = null, newPoster = null;
        try {
            if (hasVideo) newVideo = UploadUtil.save(videoPart, "videos", VIDEO_EXT, "video/");
            if (hasPoster) newPoster = UploadUtil.save(posterPart, "posters", IMAGE_EXT, "image/");
        } catch (IllegalArgumentException e) {
            UploadUtil.delete("videos", newVideo);
            errors.put("video", e.getMessage());
            showForm(req, resp, v, edit, errors);
            return;
        }

        String oldVideo = v.getFilename(), oldPoster = v.getPoster();
        if (newVideo != null) v.setFilename(newVideo);
        if (newPoster != null) v.setPoster(newPoster);

        try {
            if (edit) service.update(v); else service.insert(v);
        } catch (RuntimeException e) {
            e.printStackTrace();
            UploadUtil.delete("videos", newVideo);
            UploadUtil.delete("posters", newPoster);
            v.setFilename(oldVideo);
            v.setPoster(oldPoster);
            req.setAttribute("alert", "Lỗi hệ thống, vui lòng thử lại.");
            showForm(req, resp, v, edit, errors);
            return;
        }
        if (edit) {
            if (newVideo != null) UploadUtil.delete("videos", oldVideo);
            if (newPoster != null) UploadUtil.delete("posters", oldPoster);
        }
        resp.sendRedirect(req.getContextPath() + "/video?msg=saved");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Video v, boolean edit,
                          Map<String, String> errors) throws ServletException, IOException {
        req.setAttribute("video", v);
        req.setAttribute("edit", edit);
        req.setAttribute("errors", errors);
        req.setAttribute("csrf", CsrfUtil.token(req.getSession()));
        req.getRequestDispatcher("/views/video-form.jsp").forward(req, resp);
    }
}