package qa.dto.request.question;

public class QuestionGetViewsRequest {

    private final Integer page;

    public QuestionGetViewsRequest(Integer page) {
        this.page = page;
    }

    private QuestionGetViewsRequest() {
        this.page = null;
    }

    public Integer getPage() {
        return page;
    }
}
