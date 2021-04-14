package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import qa.dto.request.comment.CommentAnswerCreateRequest;
import qa.dto.request.comment.CommentAnswerDeleteRequest;
import qa.dto.request.comment.CommentAnswerEditRequest;
import qa.dto.request.comment.CommentAnswerGetRequest;
import qa.dto.response.comment.CommentAnswerResponse;

import java.util.List;

public interface CommentAnswerService {
    ResponseEntity<Long> createComment(CommentAnswerCreateRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> editComment(CommentAnswerEditRequest request, Authentication authentication);

    ResponseEntity<HttpStatus> deleteComment(CommentAnswerDeleteRequest request, Authentication authentication);

    ResponseEntity<List<CommentAnswerResponse>> getComments(Long answerId, Integer page);

    ResponseEntity<List<CommentAnswerResponse>> getComments(CommentAnswerGetRequest request);
}
