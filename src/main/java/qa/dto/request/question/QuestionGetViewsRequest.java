package qa.dto.request.question;

public class QuestionGetViewsRequest {

    private final Integer page;

    public QuestionGetViewsRequest(Integer page) {
        this.page = page;
    }

    protected QuestionGetViewsRequest() {
        this.page = null;
    }

    public Integer getPage() {
        return page;
    }
}
