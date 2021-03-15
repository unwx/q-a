package qa.security.jwt.entity;

import com.auth0.jwt.interfaces.Claim;

import java.util.Map;

public class JwtClaims {

    private final Long exp;
    private final String sub;

    public JwtClaims(Map<String, Claim> claims) {
        this.exp = Long.parseLong(claims.get("expm").asString());
        this.sub = claims.get("sub").asString();
    }

    public Long getExp() {
        return exp;
    }

    public String getSub() {
        return sub;
    }
}
