package qa.dto.request;

public class RegistrationRequestDto {
    private final String username;
    private final String email;
    private final String password;

    public RegistrationRequestDto(String username,
                                  String email,
                                  String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public RegistrationRequestDto() {
        this.username = null;
        this.email = null;
        this.password = null;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
