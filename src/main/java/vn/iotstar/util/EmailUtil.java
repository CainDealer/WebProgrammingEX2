package vn.iotstar.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {

    private static final Properties CONFIG = loadConfig();

    private static Properties loadConfig() {
        Properties p = new Properties();
        try (InputStream in = EmailUtil.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (in != null) {
                p.load(in);
            } else {
                System.err.println("Khong tim thay mail.properties trong src/main/resources");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static boolean send(String to, String subject, String body) {
        final String username = CONFIG.getProperty("mail.username");
        final String password = CONFIG.getProperty("mail.password");
        String host = CONFIG.getProperty("mail.host", "smtp.gmail.com");
        String port = CONFIG.getProperty("mail.port", "587");
        String senderName = CONFIG.getProperty("mail.sender.name", "Shopping");

        if (username == null || password == null) {
            System.err.println("Chua cau hinh mail.username / mail.password");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", host);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username, senderName, "UTF-8"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}