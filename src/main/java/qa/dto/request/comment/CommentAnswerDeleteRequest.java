package qa.dto.request.comment;

public class CommentAnswerDeleteRequest {

    private final Long id;

    public CommentAnswerDeleteRequest(Long id) {
        this.id = id;
    }

    public CommentAnswerDeleteRequest() {
        this.id = null;
    }

    public Long getId() {
        return id;
    }
}
