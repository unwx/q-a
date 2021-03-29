package qa.dto.internal.hibernate.user;

public class UserAnswerDto {

    private Long answerId;
    private String text;

    public Long getAnswerId() {
        return answerId;
    }

    public String getText() {
        return text;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public void setText(String text) {
        this.text = text;
    }
}
