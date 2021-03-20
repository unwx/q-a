package qa.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import qa.dto.request.QuestionCreateRequest;
import qa.dto.request.QuestionEditRequest;
import qa.service.QuestionService;

@RestController
@RequestMapping("/api/v1/question/")
public class QuestionRestController {

    private final QuestionService questionService;

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
     *     title: string|length(min = 10; max = 50). regex = ^(?![_.\- ()])(?!.*[_.-]{2})[a-zA-Z0-9а-я._\-() ]+(?<![_.\- ()])$
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
     * post
     *
     * @request
     * Dto {
     *     title: string|length(min = 10; max = 50). regex = ^(?![_.\- ()])(?!.*[_.-]{2})[a-zA-Z0-9а-я._\-() ]+(?<![_.\- ()])$
     *     text: string|length(min = 50; max = 2000)
     *     tags: string[] -> (for each tag: |length(min = 2; max = 20).regex = ^(?![_.\- ])(?!.*[_.-]{2})[a-zA-Z0-9._\-]+(?<![_.\- ])$
     *           tags|length(min = 1; max = 7)
     * }
     *
     * @response
     * OK:
     * http status: int (NOT JSON)
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
    public ResponseEntity<Integer> editQuestion(@RequestBody QuestionEditRequest request, Authentication authentication) {
        return questionService.editQuestion(request, authentication);
    }
}
