package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.QuestionDao;
import qa.domain.Question;
import qa.domain.User;
import qa.dto.request.QuestionCreateRequest;
import qa.dto.validation.wrapper.QuestionCreateRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.util.PrincipalUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.Arrays;
import java.util.Date;

@Service
public class QuestionService {

    private final QuestionDao questionDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;

    public QuestionService(QuestionDao questionDao,
                           ValidationPropertyDataSource validationPropertyDataSource,
                           ValidationChainAdditional validationChain) {
        this.questionDao = questionDao;
        this.validationPropertyDataSource = validationPropertyDataSource;
        this.validationChain = validationChain;
    }

    public ResponseEntity<Long> createQuestion(QuestionCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createQuestionProcess(request, authentication), HttpStatus.OK);
    }

    private Long createQuestionProcess(QuestionCreateRequest request, Authentication authentication) {
        validationProcess(request);
        return saveNewQuestion(request, authentication);
    }

    private Long saveNewQuestion(QuestionCreateRequest request, Authentication authentication) {
        Question question = new Question.Builder()
                .creationDate(new Date())
                .lastActivity(new Date())
                .tags(tagsArrayToString(request.getTags()))
                .text(request.getText())
                .title(request.getTitle())
                .author(new User(PrincipalUtil.getUserIdFromAuthentication(authentication)))
                .build();
        return questionDao.create(question);
    }

    private String tagsArrayToString(String[] tags) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(tags).forEach((t) -> sb.append(t).append(","));
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private void validationProcess(QuestionCreateRequest request) {
        QuestionCreateRequestValidationWrapper requestValidationWrapper = new QuestionCreateRequestValidationWrapper(request, validationPropertyDataSource);
        try {
            validationChain.validateWithAdditionalValidator(requestValidationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
