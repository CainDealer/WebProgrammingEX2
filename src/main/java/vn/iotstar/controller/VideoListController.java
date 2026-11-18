package vn.iotstar.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.iotstar.model.User;
import vn.iotstar.model.Video;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.CsrfUtil;
import vn.iotstar.util.WebUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/video")
public class VideoListController extends HttpServlet {
    private static final int PAGE_SIZE = 6;
    private final VideoService service = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User acc = WebUtil.account(req);

        long total = service.countByUser(acc.getId());
        int totalPages = (int) Math.max(1, Math.ceil(total / (double) PAGE_SIZE));
        int currentPage = Math.min(Math.max(1, WebUtil.parseInt(req.getParameter("page"), 1)), totalPages);

        List<Video> videos = service.getByUser(acc.getId(), currentPage, PAGE_SIZE);

        req.setAttribute("videos", videos);
        req.setAttribute("total", total);
        req.setAttribute("currentPage", currentPage);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("csrf", CsrfUtil.token(req.getSession()));
        req.getRequestDispatcher("/views/video-list.jsp").forward(req, resp);
    }
}