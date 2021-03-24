package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import qa.dto.request.answer.AnswerAnsweredRequest;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.dto.request.answer.AnswerDeleteRequest;
import qa.dto.request.answer.AnswerEditRequest;

public interface AnswerService {
    ResponseEntity<Long> createAnswer(AnswerCreateRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editAnswer(AnswerEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> setAnswered(AnswerAnsweredRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> removeAnswered(AnswerAnsweredRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteAnswer(AnswerDeleteRequest request, Authentication authentication);
}
