package qa.dto.response.user;

public class UserShortResponse {

    private final String username;

    public UserShortResponse(String username) {
        this.username = username;
    }

    public UserShortResponse() {
        this.username = null;
    }

    public String getUsername() {
        return username;
    }
}
