package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import qa.dto.request.question.*;
import qa.dto.response.question.QuestionCommentResponse;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;

import java.util.List;

public interface QuestionService {
    ResponseEntity<Long> createQuestion(QuestionCreateRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editQuestion(QuestionEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteQuestion(QuestionDeleteRequest request, Authentication authentication);

    ResponseEntity<List<QuestionViewResponse>> getQuestions(Integer page);

    ResponseEntity<List<QuestionViewResponse>> getQuestions(QuestionGetViewsRequest request);

    ResponseEntity<QuestionFullResponse> getFullQuestion(Long questionId);

    ResponseEntity<QuestionFullResponse> getFullQuestion(QuestionGetFullRequest request);

    ResponseEntity<List<QuestionCommentResponse>> getQuestionComments(Long questionId, Integer page);

    ResponseEntity<List<QuestionCommentResponse>> getQuestionComments(QuestionGetCommentsRequest request);
}
