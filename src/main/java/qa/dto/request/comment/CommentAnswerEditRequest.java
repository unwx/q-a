package qa.dto.request.comment;

public class CommentAnswerEditRequest {

    private final Long id;
    private final String text;

    public CommentAnswerEditRequest(Long id,
                                    String text) {
        this.id = id;
        this.text = text;
    }

    public CommentAnswerEditRequest() {
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
