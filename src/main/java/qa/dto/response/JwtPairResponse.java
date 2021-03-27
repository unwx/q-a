package qa.dto.response;

public class JwtPairResponse {
    private final String access;
    private final String refresh;

    public JwtPairResponse(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    protected JwtPairResponse() {
        this.access = null;
        this.refresh = null;
    }

    public String getAccess() {
        return access;
    }

    public String getRefresh() {
        return refresh;
    }
}
