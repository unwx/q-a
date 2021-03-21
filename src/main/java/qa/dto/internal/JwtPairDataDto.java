package qa.dto.internal;

public class JwtPairDataDto {
    private final JwtDataDto access;
    private final JwtDataDto refresh;

    public JwtPairDataDto(JwtDataDto access,
                          JwtDataDto refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    public JwtDataDto getAccess() {
        return access;
    }

    public JwtDataDto getRefresh() {
        return refresh;
    }
}
