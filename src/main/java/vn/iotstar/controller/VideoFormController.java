package vn.iotstar.controller;

import java.io.IOException;
import java.util.Set;

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
        showForm(req, resp, v, edit, null);
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

        String error = null;
        if (title.isEmpty() || title.length() > 200) error = "Tiêu đề bắt buộc, tối đa 200 ký tự.";
        else if (desc.length() > 4000) error = "Mô tả tối đa 4000 ký tự.";
        else if (!edit && !hasVideo) error = "Vui lòng chọn file video.";

        String newVideo = null, newPoster = null;
        if (error == null) {
            try {
                if (hasVideo) newVideo = UploadUtil.save(videoPart, "videos", VIDEO_EXT, "video/");
                if (hasPoster) {
                    if (posterPart.getSize() > MAX_POSTER) throw new IllegalArgumentException("Ảnh bìa tối đa 5 MB.");
                    newPoster = UploadUtil.save(posterPart, "posters", IMAGE_EXT, "image/");
                }
            } catch (IllegalArgumentException e) {
                error = e.getMessage();
                UploadUtil.delete("videos", newVideo);
                newVideo = null;
            }
        }
        if (error != null) { showForm(req, resp, v, edit, error); return; }

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
            showForm(req, resp, v, edit, "Lỗi hệ thống, vui lòng thử lại.");
            return;
        }
        if (edit) {
            if (newVideo != null) UploadUtil.delete("videos", oldVideo);
            if (newPoster != null) UploadUtil.delete("posters", oldPoster);
        }
        resp.sendRedirect(req.getContextPath() + "/video?msg=saved");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Video v, boolean edit, String error)
            throws ServletException, IOException {
        req.setAttribute("video", v);
        req.setAttribute("edit", edit);
        req.setAttribute("alert", error);
        req.setAttribute("csrf", CsrfUtil.token(req.getSession()));
        req.getRequestDispatcher("/views/video-form.jsp").forward(req, resp);
    }
}