package vn.iotstar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.iotstar.util.UploadUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/media/*")
public class MediaController extends HttpServlet {
    private static final Pattern SAFE = Pattern.compile("^/(videos|posters)/([0-9a-f\\-]{36}\\.[a-z0-9]{2,5})$");
    private static final Map<String, String> TYPES = Map.of(
            "mp4", "video/mp4", "webm", "video/webm", "ogg", "video/ogg",
            "jpg", "image/jpeg", "jpeg", "image/jpeg", "png", "image/png", "webp", "image/webp");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String info = req.getPathInfo();
        Matcher m = info == null ? null : SAFE.matcher(info);
        if (m == null || !m.matches()) { resp.sendError(404); return; }

        Path file = UploadUtil.dir(m.group(1)).resolve(m.group(2));
        String type = TYPES.get(UploadUtil.ext(m.group(2)));
        if (type == null || !Files.isRegularFile(file)) { resp.sendError(404); return; }

        long length = Files.size(file), start = 0, end = length - 1;
        boolean partial = false;

        String range = req.getHeader("Range");
        if (range != null && range.matches("bytes=\\d*-\\d*") && !range.equals("bytes=-")) {
            String[] p = range.substring(6).split("-", -1);
            try {
                if (p[0].isEmpty()) {
                    start = Math.max(0, length - Long.parseLong(p[1]));
                } else {
                    start = Long.parseLong(p[0]);
                    if (!p[1].isEmpty()) end = Math.min(end, Long.parseLong(p[1]));
                }
                if (start > end) {
                    resp.setHeader("Content-Range", "bytes */" + length);
                    resp.sendError(416);
                    return;
                }
                partial = true;
            } catch (NumberFormatException e) {
                start = 0;
                end = length - 1;
            }
        }

        resp.setContentType(type);
        resp.setHeader("Accept-Ranges", "bytes");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Cache-Control", "public, max-age=86400");
        resp.setContentLengthLong(end - start + 1);
        if (partial) {
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + length);
        }

        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r");
             OutputStream out = resp.getOutputStream()) {
            raf.seek(start);
            byte[] buf = new byte[8192];
            long remaining = end - start + 1;
            while (remaining > 0) {
                int n = raf.read(buf, 0, (int) Math.min(buf.length, remaining));
                if (n < 0) break;
                out.write(buf, 0, n);
                remaining -= n;
            }
        } catch (IOException e) {
        }
    }
}