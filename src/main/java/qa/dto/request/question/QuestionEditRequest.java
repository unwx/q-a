package qa.dto.request.question;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionEditRequest {

    @JsonProperty("id")
    private final Long questionId;
    private final String text;
    private final String[] tags;

    public QuestionEditRequest(Long questionId,
                               String text,
                               String[] tags) {
        this.questionId = questionId;
        this.text = text;
        this.tags = tags;
    }

    protected QuestionEditRequest() {
        this.questionId = null;
        this.text = null;
        this.tags = null;
    }

    public String getText() {
        return text;
    }

    public String[] getTags() {
        return tags;
    }

    public Long getQuestionId() {
        return questionId;
    }
}
