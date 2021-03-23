package qa.dto.request.answer;

public class AnswerDeleteRequest {

    private final Long id;

    public AnswerDeleteRequest(Long id) {
        this.id = id;
    }

    public AnswerDeleteRequest() {
        this.id = null;
    }

    public Long getId() {
        return id;
    }
}
