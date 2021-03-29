package qa.dto.response.user;

public class UserAnswersResponse {

    private final Long answerId;
    private final String text;

    public UserAnswersResponse(Long answerId,
                               String text) {
        this.answerId = answerId;
        this.text = text;
    }

    protected UserAnswersResponse() {
        this.answerId = null;
        this.text = null;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public String getText() {
        return text;
    }
}
