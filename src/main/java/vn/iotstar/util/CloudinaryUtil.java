package vn.iotstar.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.Part;

public class CloudinaryUtil {

    public static class Uploaded {
        public final String url;
        public final String publicId;
        Uploaded(String url, String publicId) { this.url = url; this.publicId = publicId; }
    }

    private static Cloudinary cloudinary;

    private static synchronized Cloudinary client() {
        if (cloudinary == null) {
            Properties p = new Properties();
            try (InputStream in = CloudinaryUtil.class.getClassLoader()
                    .getResourceAsStream("cloudinary.properties")) {
                if (in == null) throw new IllegalStateException("Thiếu src/main/resources/cloudinary.properties");
                p.load(in);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", p.getProperty("cloud_name"),
                    "api_key", p.getProperty("api_key"),
                    "api_secret", p.getProperty("api_secret"),
                    "secure", true));
        }
        return cloudinary;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Uploaded uploadAvatar(Part part) throws IOException {
        byte[] data;
        try (InputStream in = part.getInputStream()) {
            data = in.readAllBytes();
        }
        if (!looksLikeImage(data)) {
            throw new IllegalArgumentException("File không phải ảnh JPG/PNG/WEBP hợp lệ.");
        }
        Map res = client().uploader().upload(data, ObjectUtils.asMap(
                "folder", "ex2/avatars",
                "resource_type", "image",
                "transformation", new Transformation().width(400).height(400).crop("fill").gravity("auto")));
        return new Uploaded((String) res.get("secure_url"), (String) res.get("public_id"));
    }

    public static void delete(String publicId) {
        if (publicId == null || publicId.isEmpty()) return;
        try {
            client().uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static boolean looksLikeImage(byte[] b) {
        if (b.length < 12) return false;
        boolean jpeg = (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
        boolean png = (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G';
        boolean webp = b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
        return jpeg || png || webp;
    }
}