package vn.iotstar.model;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "`User`")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String userName;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "password")
    private String passWord;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "roleid")
    private int roleid;

    @Column(name = "phone")
    private String phone;

    @Temporal(TemporalType.DATE)
    @Column(name = "createddate")
    private Date createdDate;

    @Column(name = "status")
    private int status;

    @Column(name = "code")
    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "otpexpired")
    private Date otpExpired;

    @Column(name = "resetcode")
    private String resetCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "resetexpired")
    private Date resetExpired;

    @Column(name = "resetattempts")
    private int resetAttempts;

    @Column(name = "remembertoken")
    private String rememberToken;

    @OneToMany(mappedBy = "user")
    private List<Video> videos = new ArrayList<>();

    @Column(name = "newemail")
    private String newEmail;

    @Column(name = "emailcode")
    private String emailCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "emailexpired")
    private Date emailExpired;

    @Column(name = "emailattempts")
    private int emailAttempts;

    @Column(name = "avatarpublicid")
    private String avatarPublicId;

    public User() {}

    public User(String email, String userName, String fullName, String passWord,
                String avatar, int roleid, String phone, Date createdDate) {
        this.email = email;
        this.userName = userName;
        this.fullName = fullName;
        this.passWord = passWord;
        this.avatar = avatar;
        this.roleid = roleid;
        this.phone = phone;
        this.createdDate = createdDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassWord() { return passWord; }
    public void setPassWord(String passWord) { this.passWord = passWord; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public int getRoleid() { return roleid; }
    public void setRoleid(int roleid) { this.roleid = roleid; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Date getOtpExpired() { return otpExpired; }
    public void setOtpExpired(Date otpExpired) { this.otpExpired = otpExpired; }

    public String getResetCode() { return resetCode; }
    public void setResetCode(String resetCode) { this.resetCode = resetCode; }

    public Date getResetExpired() { return resetExpired; }
    public void setResetExpired(Date resetExpired) { this.resetExpired = resetExpired; }

    public int getResetAttempts() { return resetAttempts; }
    public void setResetAttempts(int resetAttempts) { this.resetAttempts = resetAttempts; }

    public String getRememberToken() { return rememberToken; }
    public void setRememberToken(String rememberToken) { this.rememberToken = rememberToken; }

    public List<Video> getVideos() { return videos; }
    public void setVideos(List<Video> videos) { this.videos = videos; }

    public String getNewEmail() { return newEmail; }
    public void setNewEmail(String newEmail) { this.newEmail = newEmail; }

    public String getEmailCode() { return emailCode; }
    public void setEmailCode(String emailCode) { this.emailCode = emailCode; }

    public Date getEmailExpired() { return emailExpired; }
    public void setEmailExpired(Date emailExpired) { this.emailExpired = emailExpired; }

    public int getEmailAttempts() { return emailAttempts; }
    public void setEmailAttempts(int emailAttempts) { this.emailAttempts = emailAttempts; }

    public String getAvatarPublicId() { return avatarPublicId; }
    public void setAvatarPublicId(String avatarPublicId) { this.avatarPublicId = avatarPublicId; }
}