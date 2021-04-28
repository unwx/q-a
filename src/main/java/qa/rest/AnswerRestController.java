package qa.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import qa.dto.request.answer.*;
import qa.dto.response.answer.AnswerFullResponse;
import qa.service.AnswerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/answer/")
public class AnswerRestController { // TODO REFACTOR

    private final AnswerService answerService;

    public AnswerRestController(AnswerService answerService) {
        this.answerService = answerService;
    }


    /**
     * @uri
     * /api/v1/answer/create
     *
     * @method
     * post
     *
     * @request
     * Dto {
     *     question_id: long
     *     text: string|length(min = 20; max = 2000)
     * }
     *
     * @response
     * OK:
     * created answer id: long (NOT JSON)
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
    public ResponseEntity<Long> createAnswer(@RequestBody AnswerCreateRequest request, Authentication authentication) {
        return answerService.createAnswer(request, authentication);
    }


    /**
     * @uri
     * /api/v1/answer/edit
     *
     * @method
     * put
     *
     * @request
     * Dto {
     *     id: long
     *     text: string|length(min = 20; max = 2000)
     * }
     *
     * @response
     * OK: (200)
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
    public ResponseEntity<HttpStatus> editAnswer(@RequestBody AnswerEditRequest request, Authentication authentication) {
        return answerService.editAnswer(request, authentication);
    }

    /**
     * @uri
     * /api/v1/answer/answered
     *
     * @method
     * post
     *
     * @request
     * Dto {
     *     id: long
     * }
     *
     * @response
     * OK: (200)
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
            value = "answered",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> setAnswered(@RequestBody AnswerAnsweredRequest request, Authentication authentication) {
        return answerService.setAnswered(request, authentication);
    }

    /**
     * @uri
     * /api/v1/answer/not-answered
     *
     * @method
     * post
     *
     * @request
     * Dto {
     *     id: long
     * }
     *
     * @response
     * OK: (200)
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
            value = "not-answered",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> removeAnswered(@RequestBody AnswerAnsweredRequest request, Authentication authentication) {
        return answerService.removeAnswered(request, authentication);
    }


    /**
     * @uri
     * /api/v1/answer/delete
     *
     * @method
     * delete
     *
     * @request
     * Dto {
     *     id: long
     * }
     *
     * @response
     * OK: (200)
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
    public ResponseEntity<HttpStatus> deleteAnswer(@RequestBody AnswerDeleteRequest request, Authentication authentication) {
        return answerService.deleteAnswer(request, authentication);
    }


    /**
     * @uri
     * /api/v1/answer/get/{questionId}/{page}
     *
     * @method
     * get
     *
     * @path_variable
     * questionId: long
     * page: int
     *
     * @response
     * Response {
     *     id: long
     *     text: string
     *     creation_date: string
     *     answered: string (true : false)
     *     author {
     *         username: string
     *     }
     *     comments [
     *          id: long
     *          text: string
     *          creation_date: string
     *          author {
     *              username: string
     *          }
     *     ]
     *     ... 6 sorted by creation date DESC
     * }
     *
     * 400 | 401 | 403:
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
    public ResponseEntity<List<AnswerFullResponse>> getAnswers(@PathVariable("questionId") Long questionId,
                                                              @PathVariable("page") Integer page,
                                                              Authentication authentication) {
        return answerService.getAnswers(questionId, page, authentication);
    }


    /**
     * @uri
     * /api/v1/answer/get
     *
     * @method
     * get
     *
     * @request
     * dto {
     *     id: long
     *     page: int
     * }
     *
     * @response
     * Response {
     *     id: long
     *     text: string
     *     creation_date: string
     *     answered: string (true : false)
     *     author {
     *         username: string
     *     }
     *     comments [
     *          id: long
     *          text: string
     *          creation_date: string
     *          author {
     *              username: string
     *          }
     *     ]
     *     ... 6 sorted by creation date DESC
     * }
     *
     * 400 | 401 | 403:
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
    public ResponseEntity<List<AnswerFullResponse>> getAnswers(@RequestBody AnswerGetFullRequest request,
                                                               Authentication authentication) {
        return answerService.getAnswers(request, authentication);
    }

    /**
     * @uri
     * /api/v1/answer/like
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
    public ResponseEntity<HttpStatus> like(@RequestBody AnswerLikeRequest request,
                                           Authentication authentication) {
        return this.answerService.like(request, authentication);
    }
}
