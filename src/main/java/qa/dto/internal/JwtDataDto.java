package qa.dto.internal;

public class JwtDataDto {
    private final String token;
    private final long exp;

    public JwtDataDto(String token,
                      long exp) {
        this.token = token;
        this.exp = exp;
    }

    public String getToken() {
        return token;
    }

    public long getExp() {
        return exp;
    }
}
