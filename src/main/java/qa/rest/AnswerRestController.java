package qa.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import qa.dto.request.answer.AnswerCreateRequest;
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
     *     test: string|length(min = 20; max = 2000)
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
}
