package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.iotstar.model.Video;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.CsrfUtil;
import vn.iotstar.util.WebUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/video/delete")
public class VideoDeleteController extends HttpServlet {
    private final VideoService service = new VideoServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!CsrfUtil.valid(req)) { resp.sendError(403, "CSRF token không hợp lệ."); return; }

        Video v = service.get(WebUtil.parseInt(req.getParameter("id"), -1));
        if (v == null) { resp.sendError(404); return; }
        if (!service.canModify(WebUtil.account(req), v)) { resp.sendError(403); return; }

        service.delete(v.getId());
        resp.sendRedirect(req.getContextPath() + "/video?msg=deleted");
    }
}