package qa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dto.request.question.*;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;
import qa.service.QuestionService;
import qa.service.impl.aid.process.QuestionServiceProcess;
import qa.service.impl.aid.process.database.QuestionServiceDatabase;
import qa.service.impl.aid.process.validation.QuestionServiceValidation;

import java.util.List;

@Service
public class QuestionServiceImpl extends QuestionServiceProcess implements QuestionService {

    @Autowired
    public QuestionServiceImpl(QuestionServiceValidation validation,
                               QuestionServiceDatabase database) {
        super(validation, database);
    }

    @Override
    public ResponseEntity<Long> createQuestion(QuestionCreateRequest request, Authentication authentication) {
        final long id = super.createQuestionProcess(request, authentication);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> editQuestion(QuestionEditRequest request, Authentication authentication) {
        super.editQuestionProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteQuestion(QuestionDeleteRequest request, Authentication authentication) {
        super.deleteQuestionProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<QuestionViewResponse>> getQuestions(Integer page) {
        final List<QuestionViewResponse> response = super.getQuestionsProcess(page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<QuestionViewResponse>> getQuestions(QuestionGetViewsRequest request) {
        final List<QuestionViewResponse> response = super.getQuestionsProcess(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<QuestionFullResponse> getFullQuestion(Long questionId, Authentication authentication) {
        final QuestionFullResponse response = super.getFullQuestionProcess(questionId, authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<QuestionFullResponse> getFullQuestion(QuestionGetFullRequest request, Authentication authentication) {
        final QuestionFullResponse response = super.getFullQuestionProcess(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> like(QuestionLikeRequest request, Authentication authentication) {
        super.likeProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
