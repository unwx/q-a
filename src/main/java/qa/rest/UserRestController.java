package qa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qa.dto.request.user.UserGetAnswersRequest;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.request.user.UserGetQuestionsRequest;
import qa.dto.response.user.UserAnswersResponse;
import qa.dto.response.user.UserFullResponse;
import qa.dto.response.user.UserQuestionsResponse;
import qa.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "test/{username}", method = RequestMethod.GET)
    public ResponseEntity<UserFullResponse> test(@PathVariable String username) {
        return userService.getFullUser(username);
    }


    /**
     * @uri
     * /api/v1/user/get/{username}
     *
     * @method
     * get
     *
     * @path_variable
     * username: string
     *
     * @response
     * OK:
     * User {
     *     username: string
     *     about: string
     *     answers: [
     *          {
     *              id: long
     *              text: string(length max = 50)
     *          }
     *          ... (sorted by creation date. (newer))
     *     ]
     *     questions: [
     *          {
     *              id: long
     *              title: string
     *          }
     *          ... (sorted by creation date. (newer))
     *     ]
     * }
     *
     * 400 | 401 :
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @RequestMapping(
            value = "get/{username}",
            method = RequestMethod.GET)
    public ResponseEntity<UserFullResponse> getUser(@PathVariable("username") String username) {
        return userService.getFullUser(username);
    }


    /**
     * @uri
     * /api/v1/user/get
     *
     * @method
     * get
     *
     * @request
     * dto {
     *     username: string
     * }
     *
     * @response
     * OK:
     * User {
     *     username: string
     *     about: string
     *     answers: [
     *          {
     *              id: long
     *              text: string(length max = 50)
     *          }
     *          ... (sorted by creation date. (newer))
     *     ]
     *     questions: [
     *          {
     *              id: long
     *              title: string
     *          }
     *          ... (sorted by creation date. (newer))
     *     ]
     * }
     *
     * 400 | 401 :
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @RequestMapping(value = "get",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserFullResponse> getUser(@RequestBody UserGetFullRequest request) {
        return userService.getFullUser(request);
    }


    /**
     * @uri
     * /api/v1/user/questions/get/{userId}/{page}
     *
     * @method
     * get
     *
     * @path_variable
     * userId: long
     * page: int
     *
     * @response
     * OK:
     * Questions: [
     *  {
     *  id: long
     *  title: string
     *  }
     *  ... 25 (sorted by creation date. (newer))
     * ]
     *
     * 400 | 404 | 401 :
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @RequestMapping(
            value = "questions/get/{userId}/{page}",
            method = RequestMethod.GET)
    public ResponseEntity<List<UserQuestionsResponse>> getUserQuestions(@PathVariable("userId") Long userId,
                                                                  @PathVariable("page") Integer page) {
        return userService.getUserQuestions(userId, page);
    }

    /**
     * @uri
     * /api/v1/user/questions/get
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
     * OK:
     * Questions: [
     *  {
     *  id: long
     *  title: string
     *  }
     *  ... 25 (sorted by creation date. (newer))
     * ]
     *
     * 400 | 404 | 401 :
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @RequestMapping(
            value = "questions/get",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserQuestionsResponse>> getUserQuestions(@RequestBody UserGetQuestionsRequest request) {
        return userService.getUserQuestions(request);
    }


    /**
     * @uri
     * /api/v1/user/answers/get
     *
     * @method
     * get
     *
     * @path_variable
     * userId: long
     * page: int
     *
     * @response
     * OK:
     * Answers: [
     *  {
     *  id: long
     *  text: string
     *  }
     *  ... 25 (sorted by creation date. (newer))
     * ]
     *
     * 400 | 404 | 401 :
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @RequestMapping(
            value = "answers/get/{userId}/{page}",
            method = RequestMethod.GET)
    public ResponseEntity<List<UserAnswersResponse>> getUserAnswers(@PathVariable("userId") Long userId,
                                                                    @PathVariable("page") Integer page) {
        return userService.getUserAnswers(userId, page);
    }


    /**
     * @uri
     * /api/v1/user/answers/get
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
     * OK:
     * Answers: [
     *  {
     *  id: long
     *  text: string
     *  }
     *  ... 25 (sorted by creation date. (newer))
     * ]
     *
     * 400 | 404 | 401 :
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @RequestMapping(
            value = "answers/get",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserAnswersResponse>> getUserAnswers(@RequestBody UserGetAnswersRequest request) {
        return userService.getUserAnswers(request);
    }
}
