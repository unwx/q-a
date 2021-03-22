package qa.dto.request.answer;

public class AnswerEditRequest {

    private final Long id;
    private final String text;

    public AnswerEditRequest(Long id,
                             String text) {
        this.id = id;
        this.text = text;
    }

    public AnswerEditRequest() {
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
