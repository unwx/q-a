package qa.security.jwt.entity;

import org.jetbrains.annotations.Nullable;

public class JwtIntermediateDateTransport {
    private final JwtStatus status;
    private final @Nullable JwtType type;
    private final @Nullable JwtClaims claims;
    private final @Nullable JwtAuthenticationData data;

    public JwtIntermediateDateTransport(JwtStatus status,
                                        @Nullable JwtType type,
                                        @Nullable JwtClaims claims,
                                        @Nullable JwtAuthenticationData data) {
        this.status = status;
        this.type = type;
        this.claims = claims;
        this.data = data;
    }

    public JwtIntermediateDateTransport(JwtStatus status) {
        this.status = status;
        this.type = null;
        this.claims = null;
        this.data = null;
    }

    public JwtStatus getStatus() {
        return status;
    }

    public @Nullable JwtType getType() {
        return type;
    }

    public @Nullable JwtClaims getClaims() {
        return claims;
    }

    public @Nullable JwtAuthenticationData getData() {
        return data;
    }
}
