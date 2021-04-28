package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import qa.dto.request.answer.*;
import qa.dto.response.answer.AnswerFullResponse;

import java.util.List;

public interface AnswerService {
    ResponseEntity<Long> createAnswer(AnswerCreateRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editAnswer(AnswerEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> setAnswered(AnswerAnsweredRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> removeAnswered(AnswerAnsweredRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteAnswer(AnswerDeleteRequest request, Authentication authentication);

    ResponseEntity<List<AnswerFullResponse>> getAnswers(Long questionId, Integer page, Authentication authentication);

    ResponseEntity<List<AnswerFullResponse>> getAnswers(AnswerGetFullRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> like(AnswerLikeRequest likeRequest, Authentication authentication);
}
