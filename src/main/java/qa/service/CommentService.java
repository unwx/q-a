package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import qa.dto.request.comment.*;
import qa.dto.request.question.QuestionGetCommentsRequest;
import qa.dto.response.comment.CommentQuestionResponse;

import java.util.List;

public interface CommentService {
    ResponseEntity<Long> createCommentQuestion(CommentQuestionCreateRequest request, Authentication authentication);

    ResponseEntity<Long> createCommentAnswer(CommentAnswerCreateRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editCommentQuestion(CommentQuestionEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editCommentAnswer(CommentAnswerEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteCommentQuestion(CommentQuestionDeleteRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteCommentAnswer(CommentAnswerDeleteRequest request, Authentication authentication);

    ResponseEntity<List<CommentQuestionResponse>> getCommentQuestion(Long questionId, Integer page);

    ResponseEntity<List<CommentQuestionResponse>> getCommentQuestion(QuestionGetCommentsRequest request);
}
