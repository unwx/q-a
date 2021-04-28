package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import qa.dto.request.comment.*;
import qa.dto.response.comment.CommentQuestionResponse;

import java.util.List;

public interface CommentQuestionService {
    ResponseEntity<Long> createComment(CommentQuestionCreateRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editComment(CommentQuestionEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteComment(CommentQuestionDeleteRequest request, Authentication authentication);

    ResponseEntity<List<CommentQuestionResponse>> getComments(Long questionId, Integer page, Authentication authentication);

    ResponseEntity<List<CommentQuestionResponse>> getComments(CommentQuestionGetRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> like(CommentQuestionLikeRequest request, Authentication authentication);
}
