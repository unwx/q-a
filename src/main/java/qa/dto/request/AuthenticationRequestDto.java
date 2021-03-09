package qa.dto.request;

public class AuthenticationRequestDto {
    private final String email; //login
    private final String password;

    public AuthenticationRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
