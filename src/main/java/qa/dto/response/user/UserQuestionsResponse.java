package qa.dto.response.user;

public class UserQuestionsResponse {

    private final Long id;
    private final String title;

    public UserQuestionsResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    protected UserQuestionsResponse() {
        this.id = null;
        this.title = null;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
