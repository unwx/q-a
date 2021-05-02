package qa.dto.request.answer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerGetFullRequest {

    @JsonProperty("id")
    private final Long questionId;
    private final Integer page;

    public AnswerGetFullRequest(Long questionId,
                                Integer page) {
        this.questionId = questionId;
        this.page = page;
    }

    private AnswerGetFullRequest() {
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
