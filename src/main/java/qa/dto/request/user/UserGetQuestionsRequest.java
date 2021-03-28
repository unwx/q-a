package qa.dto.request.user;

public class UserGetQuestionsRequest {

    private final Long id;
    private final Integer page;

    public UserGetQuestionsRequest(Long id,
                                   Integer page) {
        this.id = id;
        this.page = page;
    }

    protected UserGetQuestionsRequest() {
        this.id = null;
        this.page = null;
    }

    public Long getId() {
        return id;
    }

    public Integer getPage() {
        return page;
    }
}
