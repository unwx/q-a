package qa.dto.request.user;

public class UserGetFullRequest {

    private final String username;

    public UserGetFullRequest(String username) {
        this.username = username;
    }

    public UserGetFullRequest() {
        this.username = null;
    }

    public String getUsername() {
        return username;
    }
}
