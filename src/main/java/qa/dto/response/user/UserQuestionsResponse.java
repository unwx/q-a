package qa.dto.response.user;

public class UserQuestionsResponse {

    private final Long questionId;
    private final String title;

    public UserQuestionsResponse(Long questionId, String title) {
        this.questionId = questionId;
        this.title = title;
    }

    protected UserQuestionsResponse() {
        this.questionId = null;
        this.title = null;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getTitle() {
        return title;
    }
}
