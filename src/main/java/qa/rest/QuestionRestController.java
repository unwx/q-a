package qa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import qa.dto.request.question.*;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;
import qa.service.QuestionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question/")
public class QuestionRestController {

    private final QuestionService questionService;

    @Autowired
    public QuestionRestController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * @uri
     * /api/v1/question/create
     *
     * @method
     * post
     *
     * @request
     * Dto {
     *     title: string|length(min = 10; max = 50).
     *     text: string|length(min = 50; max = 2000)
     *     tags: string[] -> (for each tag: |length(min = 2; max = 20).regex = ^(?![_.\- ])(?!.*[_.-]{2})[a-zA-Z0-9._\-]+(?<![_.\- ])$
     *           tags|length(min = 1; max = 7)
     * }
     *
     * @response
     * OK:
     * id: long (NOT JSON)
     *
     * 400 | 401:
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
    public ResponseEntity<Long> createQuestion(@RequestBody QuestionCreateRequest request, Authentication authentication) {
        return questionService.createQuestion(request, authentication);
    }


    /**
     * @uri
     * /api/v1/question/create
     *
     * @method
     * put
     *
     * @request
     * Dto {
     *     text: string|length(min = 50; max = 2000)
     *     tags: string[] -> (for each tag: |length(min = 2; max = 20).regex = ^(?![_.\- ])(?!.*[_.-]{2})[a-zA-Z0-9._\-]+(?<![_.\- ])$
     *           tags|length(min = 1; max = 7)
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
    public ResponseEntity<HttpStatus> editQuestion(@RequestBody QuestionEditRequest request, Authentication authentication) {
        return questionService.editQuestion(request, authentication);
    }

    /**
     * @uri
     * /api/v1/question/delete
     *
     * @method
     * delete
     *
     * @request
     * id: long
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
    public ResponseEntity<HttpStatus> deleteQuestion(@RequestBody QuestionDeleteRequest request, Authentication authentication) {
        return questionService.deleteQuestion(request, authentication);
    }

    /**
     * @uri
     * /api/v1/question/get/views/{page}
     *
     * @method
     * get
     *
     * @path_variable
     * page: int > 0
     *
     * @response
     * OK: (200)
     * response {
     *     id: long
     *     answers_count: int
     *     title: string
     *     creation_date: string|yyyy-MM-dd HH:mm:ss
     *     last_activity: string|yyyy-MM-dd HH:mm:ss
     *     tags: [
     *         tag: string
     *     ]
     *     author: {
     *         username: string
     *     }
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
            value = "get/views/{page}",
            method = RequestMethod.GET)
    public ResponseEntity<List<QuestionViewResponse>> getQuestions(@PathVariable("page") Integer page) {
        return questionService.getQuestions(page);
    }

    /**
     * @uri
     * /api/v1/question/get/views
     *
     * @method
     * get
     *
     * @request
     * dto {
     *     page: int > 0
     * }
     *
     * @response
     * OK: (200)
     * response {
     *     id: long
     *     answers_count: int
     *     title: string
     *     creation_date: string|yyyy-MM-dd HH:mm:ss
     *     last_activity: string|yyyy-MM-dd HH:mm:ss
     *     tags: [
     *         tag: string
     *     ]
     *     author: {
     *         username: string
     *     }
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
            value = "get/views",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<QuestionViewResponse>> getQuestions(@RequestBody QuestionGetViewsRequest request) {
        return questionService.getQuestions(request);
    }


    /**
     * @uri
     * /api/v1/question/get/full
     *
     * @method
     * get
     *
     * @path_variable
     * id: long
     *
     * @response
     * OK: (200)
     * response {
     *     id: long
     *     text: string
     *     title: string
     *     creation_date: string|yyyy-MM-dd HH:mm:ss
     *     last_activity: string|yyyy-MM-dd HH:mm:ss
     *     tags: [
     *          tag: string
     *     ]
     *     author: {
     *         username: string
     *     }
     *     answers: [
     *          id: long
     *          text: string
     *          answered: string (true, false)
     *          creation_date: string|yyyy-MM-dd HH:mm:ss
     *          author: {
     *              username: string
     *          }
     *          comments: [
     *              id: long
     *              text: string
     *              creation_date: string|yyyy-MM-dd HH:mm:ss
     *              author: {
     *                  username: string
     *              }
     *              ...
     *          ]
     *         ...
     *     ]
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
            value = "get/full/{questionId}",
            method = RequestMethod.GET)
    public ResponseEntity<QuestionFullResponse> getFullQuestion(@PathVariable("questionId") Long questionId) {
        return questionService.getFullQuestion(questionId);
    }


    /**
     * @uri
     * /api/v1/question/get/full
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
     * OK: (200)
     * response {
     *     id: long
     *     text: string
     *     title: string
     *     creation_date: string|yyyy-MM-dd HH:mm:ss
     *     last_activity: string|yyyy-MM-dd HH:mm:ss
     *     tags: [
     *          tag: string
     *     ]
     *     author: {
     *         username: string
     *     }
     *     answers: [
     *          id: long
     *          text: string
     *          answered: string (true, false)
     *          creation_date: string|yyyy-MM-dd HH:mm:ss
     *          author: {
     *              username: string
     *          }
     *          comments: [
     *              id: long
     *              text: string
     *              creation_date: string|yyyy-MM-dd HH:mm:ss
     *              author: {
     *                  username: string
     *              }
     *              ...
     *          ]
     *         ...
     *     ]
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
            value = "get/full",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionFullResponse> getFullQuestion(@RequestBody QuestionGetFullRequest request) {
        return questionService.getFullQuestion(request);
    }
}
