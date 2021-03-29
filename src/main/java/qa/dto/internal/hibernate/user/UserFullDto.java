package qa.dto.internal.hibernate.user;

public class UserFullDto {

    private Long userId;
    private String about;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
