package qa.dto.response;

public class JwtPairResponseDto {
    private final String access;
    private final String refresh;

    public JwtPairResponseDto(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    public String getAccess() {
        return access;
    }

    public String getRefresh() {
        return refresh;
    }
}
