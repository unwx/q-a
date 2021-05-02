package qa.dto.response.user;

public class UserShortResponse {

    private final String username;

    public UserShortResponse(String username) {
        this.username = username;
    }

    private UserShortResponse() {
        this.username = null;
    }

    public String getUsername() {
        return username;
    }
}
