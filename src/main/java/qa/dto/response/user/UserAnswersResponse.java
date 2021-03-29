package qa.dto.response.user;

public class UserAnswersResponse {

    private final Long id;
    private final String text;

    public UserAnswersResponse(Long id,
                               String text) {
        this.id = id;
        this.text = text;
    }

    protected UserAnswersResponse() {
        this.id = null;
        this.text = null;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
