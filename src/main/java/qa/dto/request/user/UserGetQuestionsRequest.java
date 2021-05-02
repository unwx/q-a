package qa.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserGetQuestionsRequest {

    @JsonProperty("id")
    private final Long userId;
    private final Integer page;

    public UserGetQuestionsRequest(Long userId,
                                   Integer page) {
        this.userId = userId;
        this.page = page;
    }

    private UserGetQuestionsRequest() {
        this.userId = null;
        this.page = null;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getPage() {
        return page;
    }
}
