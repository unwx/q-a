package qa.dto.request.authentication;

public class AuthenticationRequest {
    private final String email; //login
    private final String password;

    public AuthenticationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthenticationRequest() {
        this.email = null;
        this.password = null;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
