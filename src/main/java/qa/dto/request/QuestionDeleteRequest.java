package qa.dto.request;

public class QuestionDeleteRequest {

    private final Long id;

    public QuestionDeleteRequest(Long id) {
        this.id = id;
    }

    public QuestionDeleteRequest() {
        this.id = null;
    }

    public Long getId() {
        return id;
    }
}
