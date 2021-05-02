package qa.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserGetAnswersRequest {

    @JsonProperty("id")
    private final Long userId;
    private final Integer page;

    public UserGetAnswersRequest(Long userId,
                                 Integer page) {
        this.userId = userId;
        this.page = page;
    }

    private UserGetAnswersRequest() {
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
