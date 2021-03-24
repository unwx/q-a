package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import qa.dto.request.comment.*;

public interface CommentService {
    ResponseEntity<Long> createCommentQuestion(CommentQuestionCreateRequest request, Authentication authentication);

    ResponseEntity<Long> createCommentAnswer(CommentAnswerCreateRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editCommentQuestion(CommentQuestionEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editCommentAnswer(CommentAnswerEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteCommentQuestion(CommentQuestionDeleteRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteCommentAnswer(CommentAnswerCreateRequest request, Authentication authentication);
}
