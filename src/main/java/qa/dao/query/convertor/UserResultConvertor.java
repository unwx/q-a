package qa.dao.query.convertor;

import qa.domain.User;

public abstract class UserResultConvertor {
    protected User usernameToAuthor(String username) {
        return new User.Builder().username(username).build();
    }
}
