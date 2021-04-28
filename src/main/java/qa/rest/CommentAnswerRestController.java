package qa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import qa.dto.request.comment.*;
import qa.dto.response.comment.CommentAnswerResponse;
import qa.service.CommentAnswerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment/answer/")
public class CommentAnswerRestController {

    private final CommentAnswerService commentService;

    @Autowired
    public CommentAnswerRestController(CommentAnswerService commentService) {
        this.commentService = commentService;
    }

    /**
     * @uri
     * /api/v1/comment/answer/create
     *
     * @method
     * post
     *
     * @request
     * dto {
     *     answer_id: long
     *     text: string
     * }
     *
     * @response
     * OK: (200)
     * id: long (NOT JSON)
     *
     * 400 | 401 | 403:
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(
            value = "create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createComment(@RequestBody CommentAnswerCreateRequest request, Authentication authentication) {
        return commentService.createComment(request, authentication);
    }

    /**
     * @uri
     * /api/v1/comment/answer/edit
     *
     * @method
     * put
     *
     * @request
     * dto {
     *     id: long
     *     text: string
     * }
     *
     * @response
     * OK: (200)
     * id: long (NOT JSON)
     *
     * 400 | 401 | 403:
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(
            value = "edit",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> editComment(@RequestBody CommentAnswerEditRequest request, Authentication authentication) {
        return commentService.editComment(request, authentication);
    }

    /**
     * @uri
     * /api/v1/comment/answer/edit
     *
     * @method
     * delete
     *
     * @request
     * dto {
     *     id: long
     * }
     *
     * @response
     * OK: (200)
     * id: long (NOT JSON)
     *
     * 400 | 401 | 403:
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(
            value = "/delete",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> deleteComment(@RequestBody CommentAnswerDeleteRequest request, Authentication authentication) {
        return commentService.deleteComment(request, authentication);
    }


    /**
     * @uri
     * /api/v1/comment/answer/get/{answerId}/{page}
     *
     * @method
     * get
     *
     * @path_variable
     * answerId: long
     * page: long
     *
     * @response
     * OK: (200)
     * response {
     *     comments: [
     *          id: long
     *          text: string
     *          creation_date: string|yyyy-MM-dd HH:mm:ss
     *          author: {
     *              username: string
     *          }
     *          ...
     *     ]
     * }
     *
     * 400 | 401 | 404 | 403:
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @RequestMapping(
            value = "get/{answerId}/{page}",
            method = RequestMethod.GET)
    public ResponseEntity<List<CommentAnswerResponse>> getComments(@PathVariable("answerId") Long answerId,
                                                                   @PathVariable("page") Integer page,
                                                                   Authentication authentication) {
        return commentService.getComments(answerId, page, authentication);
    }

    /**
     * @uri
     * /api/v1/comment/answer/get
     *
     * @method
     * get
     *
     * @request
     * dto {
     *     questionId: long
     *     page: int
     * }
     *
     * @response
     * OK: (200)
     * response {
     *     comments: [
     *          id: long
     *          text: string
     *          creation_date: string|yyyy-MM-dd HH:mm:ss
     *          author: {
     *              username: string
     *          }
     *          ...
     *     ]
     * }
     *
     * 400 | 401 | 404 | 403:
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @RequestMapping(
            value = "get",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentAnswerResponse>> getComments(@RequestBody CommentAnswerGetRequest request,
                                                                   Authentication authentication) {
        return commentService.getComments(request, authentication);
    }

    /**
     * @uri
     * /api/v1/comment/answer/like
     *
     * @method
     * get
     *
     * @request
     * dto {
     *     id: long
     * }
     *
     * @response
     * (OK): 200
     *
     * 400 | 401 | 403:
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(
            value = "like",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> like(@RequestBody CommentAnswerLikeRequest request,
                                           Authentication authentication) {
        return this.commentService.like(request, authentication);
    }
}
