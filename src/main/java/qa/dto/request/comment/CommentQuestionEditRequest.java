package qa.dto.request.comment;

public class CommentQuestionEditRequest {

    private final Long id;
    private final String text;

    public CommentQuestionEditRequest(Long id,
                                      String text) {
        this.id = id;
        this.text = text;
    }

    public CommentQuestionEditRequest() {
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
