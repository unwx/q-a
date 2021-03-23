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
import qa.dto.request.answer.AnswerAnsweredRequest;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.dto.request.answer.AnswerEditRequest;
import qa.service.AnswerService;

@RestController
@RequestMapping("/api/v1/answer/")
public class AnswerRestController {

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
}
