package qa.dto.request.question;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionGetAnswersRequest {

    @JsonProperty("id")
    private final Long questionId;
    private final Integer page;

    public QuestionGetAnswersRequest(Long questionId,
                                     Integer page) {
        this.questionId = questionId;
        this.page = page;
    }

    protected QuestionGetAnswersRequest() {
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
