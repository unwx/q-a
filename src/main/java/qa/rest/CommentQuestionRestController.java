package qa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import qa.dto.request.comment.CommentQuestionCreateRequest;
import qa.dto.request.comment.CommentQuestionDeleteRequest;
import qa.dto.request.comment.CommentQuestionEditRequest;
import qa.dto.request.comment.CommentQuestionGetRequest;
import qa.dto.response.comment.CommentQuestionResponse;
import qa.service.CommentQuestionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment/question/")
public class CommentQuestionRestController {

    private final CommentQuestionService commentService;

    @Autowired
    public CommentQuestionRestController(CommentQuestionService commentService) {
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
            value = "create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createComment(@RequestBody CommentQuestionCreateRequest request, Authentication authentication) {
        return commentService.createComment(request, authentication);
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
            value = "edit",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> editComment(@RequestBody CommentQuestionEditRequest request, Authentication authentication) {
        return commentService.editComment(request, authentication);
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
            value = "delete",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> deleteComment(@RequestBody CommentQuestionDeleteRequest request, Authentication authentication) {
        return commentService.deleteComment(request, authentication);
    }

    /**
     * @uri
     * /api/v1/comment/question/get/{questionId}/{page}
     *
     * @method
     * get
     *
     * @path_variable
     * questionId: long
     * page: int
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
            value = "get/{questionId}/{page}",
            method = RequestMethod.GET)
    public ResponseEntity<List<CommentQuestionResponse>> getComments(@PathVariable("questionId") Long questionId,
                                                                             @PathVariable("page") Integer page) {
        return commentService.getComments(questionId, page);
    }

    /**
     * @uri
     * /api/v1/comment/question/get
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
    public ResponseEntity<List<CommentQuestionResponse>> getComments(@RequestBody CommentQuestionGetRequest request) {
        return commentService.getComments(request);
    }
}
