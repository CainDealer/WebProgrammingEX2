package vn.iotstar.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import jakarta.servlet.http.Part;

public class UploadUtil {
    public static final Path ROOT = Paths.get(System.getProperty("user.home"), "ex2-uploads");
    private static final Pattern SAFE_NAME = Pattern.compile("[0-9a-f\\-]{36}\\.[a-z0-9]{2,5}");

    public static Path dir(String sub) throws IOException {
        Path p = ROOT.resolve(sub);
        Files.createDirectories(p);
        return p;
    }

    public static String ext(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return i < 0 ? "" : filename.substring(i + 1).toLowerCase(Locale.ROOT);
    }

    public static String save(Part part, String sub, Set<String> allowedExt, String mimePrefix) throws IOException {
        String ext = ext(part.getSubmittedFileName());
        if (!allowedExt.contains(ext)) {
            throw new IllegalArgumentException("Định dạng không được hỗ trợ. Chỉ chấp nhận: " + allowedExt);
        }
        String ct = part.getContentType();
        if (ct == null || !ct.toLowerCase(Locale.ROOT).startsWith(mimePrefix)) {
            throw new IllegalArgumentException("File không đúng loại yêu cầu.");
        }
        String name = UUID.randomUUID() + "." + ext;
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, dir(sub).resolve(name));
        }
        return name;
    }

    public static void delete(String sub, String name) {
        if (name == null || !SAFE_NAME.matcher(name).matches()) return;
        try {
            Files.deleteIfExists(dir(sub).resolve(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}