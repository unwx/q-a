package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.AnswerDao;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.dto.validation.wrapper.answer.AnswerCreateRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.util.PrincipalUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.Date;

@Service
public class AnswerService {

    private final AnswerDao answerDao;
    private final ValidationPropertyDataSource propertyDataSource;
    private final ValidationChainAdditional validationChain;

    public AnswerService(AnswerDao answerDao,
                         ValidationPropertyDataSource propertyDataSource,
                         ValidationChainAdditional validationChain) {
        this.answerDao = answerDao;
        this.propertyDataSource = propertyDataSource;
        this.validationChain = validationChain;
    }

    public ResponseEntity<Long> createAnswer(AnswerCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createAnswerProcess(request, authentication), HttpStatus.OK);
    }

    private Long createAnswerProcess(AnswerCreateRequest request, Authentication authentication) {
        validationProcess(request);
        return saveNewAnswer(request, authentication);
    }

    private Long saveNewAnswer(AnswerCreateRequest request, Authentication authentication) {
        Answer answer = new Answer.Builder()
                .text(request.getText())
                .adopted(false)
                .creationDate(new Date())
                .author(new User(PrincipalUtil.getUserIdFromAuthentication(authentication)))
                .question(new Question(request.getId()))
                .build();
        return answerDao.create(answer);
    }

    private void validationProcess(AnswerCreateRequest request) {
        AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(request, propertyDataSource);
        try {
            validationChain.validate(validationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
