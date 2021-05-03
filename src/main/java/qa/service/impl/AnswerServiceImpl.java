package qa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dto.request.answer.*;
import qa.dto.response.answer.AnswerFullResponse;
import qa.service.AnswerService;
import qa.service.impl.processor.AnswerServiceProcessor;
import qa.service.impl.processor.manager.AnswerDataManager;
import qa.service.impl.processor.validator.AnswerRequestValidator;

import java.util.List;

@Service
public class AnswerServiceImpl extends AnswerServiceProcessor implements AnswerService {

    @Autowired
    protected AnswerServiceImpl(AnswerRequestValidator validation,
                                AnswerDataManager database) {
        super(validation, database);
    }

    @Override
    public ResponseEntity<Long> createAnswer(AnswerCreateRequest request, Authentication authentication) {
        final long id = super.createAnswerProcess(request, authentication);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> editAnswer(AnswerEditRequest request, Authentication authentication) {
        super.editAnswerProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> setAnswered(AnswerAnsweredRequest request, Authentication authentication) {
        super.setAnsweredProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> removeAnswered(AnswerAnsweredRequest request, Authentication authentication) {
        super.removeAnsweredProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteAnswer(AnswerDeleteRequest request, Authentication authentication) {
        super.deleteAnswerProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AnswerFullResponse>> getAnswers(Long questionId, Integer page, Authentication authentication) {
        final List<AnswerFullResponse> response = super.getAnswersProcess(questionId, page, authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AnswerFullResponse>> getAnswers(AnswerGetFullRequest request, Authentication authentication) {
        final List<AnswerFullResponse> response = super.getAnswersProcess(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> like(AnswerLikeRequest likeRequest, Authentication authentication) {
        super.likeProcess(likeRequest, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
