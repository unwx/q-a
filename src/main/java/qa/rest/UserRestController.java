package qa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.response.user.FullUserResponse;
import qa.service.UserService;

@RestController
@RequestMapping("/api/v1/user/")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "test/{username}", method = RequestMethod.GET)
    public ResponseEntity<FullUserResponse> test(@PathVariable String username) {
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
    public ResponseEntity<FullUserResponse> getUser(@PathVariable("username") String username) {
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
    public ResponseEntity<FullUserResponse> getUser(@RequestBody UserGetFullRequest request) {
        return userService.getFullUser(request);
    }
}
