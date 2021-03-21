package qa.dto.request.question;

public class QuestionEditRequest {

    private final Long id;
    private final String text;
    private final String[] tags;

    public QuestionEditRequest(Long id,
                               String text,
                               String[] tags) {
        this.id = id;
        this.text = text;
        this.tags = tags;
    }

    public QuestionEditRequest() {
        this.id = null;
        this.text = null;
        this.tags = null;
    }

    public String getText() {
        return text;
    }

    public String[] getTags() {
        return tags;
    }

    public Long getId() {
        return id;
    }
}
