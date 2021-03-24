package qa.dto.request.comment;

public class CommentQuestionDeleteRequest {

    private final Long id;

    public CommentQuestionDeleteRequest(Long id) {
        this.id = id;
    }

    public CommentQuestionDeleteRequest() {
        this.id = null;
    }

    public Long getId() {
        return id;
    }
}
