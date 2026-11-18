package vn.iotstar.controller;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.iotstar.model.User;
import vn.iotstar.model.Video;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.CsrfUtil;
import vn.iotstar.util.WebUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/video/detail")
public class VideoDetailController extends HttpServlet {
    private final VideoService service = new VideoServiceImpl();

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Video v = service.get(WebUtil.parseInt(req.getParameter("id"), -1));
        User acc = WebUtil.account(req);
        boolean canModify = service.canModify(acc, v);

        if (v == null || (!v.isActive() && !canModify)) { resp.sendError(404); return; }

        HttpSession session = req.getSession(true);
        Set<Integer> viewed = (Set<Integer>) session.getAttribute("viewedVideos");
        if (viewed == null) {
            viewed = ConcurrentHashMap.newKeySet();
            session.setAttribute("viewedVideos", viewed);
        }
        if (viewed.add(v.getId())) {
            service.increaseViews(v.getId());
            v.setViews(v.getViews() + 1);
        }

        req.setAttribute("video", v);
        req.setAttribute("canModify", canModify);
        req.setAttribute("csrf", CsrfUtil.token(session));
        req.getRequestDispatcher("/views/video-detail.jsp").forward(req, resp);
    }
}