package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import qa.dto.request.question.QuestionCreateRequest;
import qa.dto.request.question.QuestionDeleteRequest;
import qa.dto.request.question.QuestionEditRequest;

public interface QuestionService {
    ResponseEntity<Long> createQuestion(QuestionCreateRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editQuestion(QuestionEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteQuestion(QuestionDeleteRequest request, Authentication authentication);
}
