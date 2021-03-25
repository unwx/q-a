package qa.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import qa.dto.request.comment.*;
import qa.service.CommentService;

@RestController
@RequestMapping("/api/v1/comment/")
public class CommentRestController {

    private final CommentService commentService;

    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * @uri
     * /api/v1/comment/question/create
     *
     * @method
     * post
     *
     * @request
     * dto {
     *     question_id: long
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
            value = "/question/create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createCommentQuestion(@RequestBody CommentQuestionCreateRequest request, Authentication authentication) {
        return commentService.createCommentQuestion(request, authentication);
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
     *     question_id: long
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
            value = "/answer/create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createCommentAnswer(@RequestBody CommentAnswerCreateRequest request, Authentication authentication) {
        return commentService.createCommentAnswer(request, authentication);
    }


    /**
     * @uri
     * /api/v1/comment/question/edit
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
            value = "/question/edit",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> editCommentQuestion(@RequestBody CommentQuestionEditRequest request, Authentication authentication) {
        return commentService.editCommentQuestion(request, authentication);
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
            value = "/answer/edit",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> editCommentAnswer(@RequestBody CommentAnswerEditRequest request, Authentication authentication) {
        return commentService.editCommentAnswer(request, authentication);
    }


    /**
     * @uri
     * /api/v1/comment/question/edit
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
            value = "/question/delete",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> deleteCommentQuestion(@RequestBody CommentQuestionDeleteRequest request, Authentication authentication) {
        return commentService.deleteCommentQuestion(request, authentication);
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
            value = "/answer/delete",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> deleteCommentAnswer(@RequestBody CommentAnswerDeleteRequest request, Authentication authentication) {
        return commentService.deleteCommentAnswer(request, authentication);
    }
}
