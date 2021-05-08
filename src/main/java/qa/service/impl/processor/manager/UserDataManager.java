package qa.service.impl.processor.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.UserDao;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.dto.response.user.UserAnswersResponse;
import qa.dto.response.user.UserFullResponse;
import qa.dto.response.user.UserQuestionsResponse;
import qa.service.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDataManager {

    private final UserDao userDao;

    private static final String ERR_USER_NOT_EXIST_ID           = "user not exist. user id: %s";
    private static final String ERR_USER_NOT_EXIST_USERNAME     = "user not exist. username: %s";

    @Autowired
    public UserDataManager(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserFullResponse getUserResponse(String username) {
        final User user = this.getFullUserFromDatabase(username);
        return this.convertUserToResponseDto(user);
    }

    public List<UserQuestionsResponse> getUserQuestionsResponse(long userId, int page) {
        final List<Question> questions = this.getUserQuestionsFromDatabase(userId, page);
        return this.convertQuestionsToResponseDto(questions);
    }

    public List<UserAnswersResponse> getUserAnswersResponse(long userId, int page) {
        final List<Answer> answers = this.getUserAnswersFromDatabase(userId, page);
        return this.convertAnswersToResponseDto(answers);
    }

    /**
     *
     * @throws qa.exceptions.rest.ResourceNotFoundException:
     * if result is null
     */
    private User getFullUserFromDatabase(String username) {
        final User user = this.userDao.readFullUser(username);
        return ResourceUtil.throwResourceNFExceptionIfNull(user, ERR_USER_NOT_EXIST_USERNAME.formatted(username));
    }

    /**
     *
     * @throws qa.exceptions.rest.ResourceNotFoundException:
     * if result is null
     */
    private List<Question> getUserQuestionsFromDatabase(long userId, int page) {
        final List<Question> questions = this.userDao.readUserQuestions(userId, page - 1);
        return ResourceUtil.throwResourceNFExceptionIfNull(questions, ERR_USER_NOT_EXIST_ID.formatted(userId));
    }

    /**
     *
     * @throws qa.exceptions.rest.ResourceNotFoundException:
     * if result is null
     */
    private List<Answer> getUserAnswersFromDatabase(long userId, int page) {
        final List<Answer> answers = this.userDao.readUserAnswers(userId, page - 1);
        return ResourceUtil.throwResourceNFExceptionIfNull(answers, ERR_USER_NOT_EXIST_ID.formatted(userId));
    }

    private UserFullResponse convertUserToResponseDto(User user) {
        return new UserFullResponse(
                user.getId(),
                user.getUsername(),
                user.getAbout(),
                user.getQuestions(),
                user.getAnswers()
        );
    }

    private List<UserQuestionsResponse> convertQuestionsToResponseDto(List<Question> questions) {
        final List<UserQuestionsResponse> response = new ArrayList<>(questions.size());
        questions.forEach((q) -> response.add(
                new UserQuestionsResponse(
                        q.getId(),
                        q.getTitle()
                )
        ));
        return response;
    }

    private List<UserAnswersResponse> convertAnswersToResponseDto(List<Answer> answers) {
        final List<UserAnswersResponse> response = new ArrayList<>(answers.size());
        answers.forEach((a) -> response.add(
                new UserAnswersResponse(
                        a.getId(),
                        a.getText()
                )
        ));
        return response;
    }
}
