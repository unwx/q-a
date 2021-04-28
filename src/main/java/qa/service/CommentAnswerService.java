package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import qa.dto.request.comment.*;
import qa.dto.response.comment.CommentAnswerResponse;

import java.util.List;

public interface CommentAnswerService {
    ResponseEntity<Long> createComment(CommentAnswerCreateRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editComment(CommentAnswerEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteComment(CommentAnswerDeleteRequest request, Authentication authentication);

    ResponseEntity<List<CommentAnswerResponse>> getComments(Long answerId, Integer page, Authentication authentication);

    ResponseEntity<List<CommentAnswerResponse>> getComments(CommentAnswerGetRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> like(CommentAnswerLikeRequest request, Authentication authentication);
}
