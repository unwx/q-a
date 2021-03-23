package qa.dto.request.answer;

public class AnswerAnsweredRequest {

    private final Long id;

    public AnswerAnsweredRequest(Long id) {
        this.id = id;
    }

    public AnswerAnsweredRequest() {
        this.id = null;
    }

    public Long getId() {
        return id;
    }
}
