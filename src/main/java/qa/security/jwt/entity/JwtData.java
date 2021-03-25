package qa.security.jwt.entity;

public class JwtData {

    private final String token;
    private final long expirationAtMillis;

    public JwtData(String token, long expirationAtMillis) {
        this.token = token;
        this.expirationAtMillis = expirationAtMillis;
    }

    public String getToken() {
        return token;
    }

    public long getExpirationAtMillis() {
        return expirationAtMillis;
    }
}