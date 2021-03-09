package qa.security.jwt.entity;

public class JwtData {

    private String token;
    private long expirationAtMillis;

    public JwtData(String token, long expirationAtMillis) {
        this.token = token;
        this.expirationAtMillis = expirationAtMillis;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpirationAtMillis() {
        return expirationAtMillis;
    }

    public void setExpirationAtMillis(long expirationAtMillis) {
        this.expirationAtMillis = expirationAtMillis;
    }
}