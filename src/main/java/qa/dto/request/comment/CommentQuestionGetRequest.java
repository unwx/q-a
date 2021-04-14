package qa.dto.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentQuestionGetRequest {

    @JsonProperty("id")
    private final Long questionId;
    private final Integer page;

    public CommentQuestionGetRequest(Long questionId,
                                     Integer page) {
        this.questionId = questionId;
        this.page = page;
    }

    protected CommentQuestionGetRequest() {
        this.questionId = null;
        this.page = null;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Integer getPage() {
        return page;
    }
}
