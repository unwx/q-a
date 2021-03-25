package qa.dto.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentQuestionCreateRequest {

    @JsonProperty("question_id")
    private final Long questionId;
    private final String text;

    public CommentQuestionCreateRequest(Long questionId,
                                        String text) {
        this.questionId = questionId;
        this.text = text;
    }

    /* jackson default constructor */
    protected CommentQuestionCreateRequest() {
        this.questionId = null;
        this.text = null;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getText() {
        return text;
    }
}
