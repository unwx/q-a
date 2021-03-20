package qa.dto.request;

public class QuestionCreateRequest {

    private final String title;
    private final String text;
    private final String[] tags;

    public QuestionCreateRequest(String title,
                                 String text,
                                 String[] tags) {
        this.title = title;
        this.text = text;
        this.tags = tags;
    }

    public QuestionCreateRequest() {
        this.title = null;
        this.text = null;
        this.tags = null;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String[] getTags() {
        return tags;
    }
}
