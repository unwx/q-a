package qa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import qa.dto.request.AuthenticationRequestDto;
import qa.dto.request.RegistrationRequestDto;
import qa.dto.response.JwtPairResponseDto;
import qa.security.jwt.entity.JwtClaims;
import qa.service.AuthenticationService;

import javax.servlet.ServletRequest;

@RestController
@RequestMapping(value = "/api/v1/authentication/")
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    /**
     * @uri
     * /api/v1/authentication/registration
     *
     * @method
     * post
     *
     * @request
     * Dto {
     *     username: string|length(min = 2; max = 30). regex = ^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$
     *     email: string
     *     password: string|length(min = 10; max = 30)
     * }
     *
     * @response
     * OK:
     * Tokens {
     *     access: string
     *     refresh: string
     * }
     *
     * 400:
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @RequestMapping(
            value = "registration",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtPairResponseDto> registration(@RequestBody RegistrationRequestDto request) {
        return authenticationService.registration(request);
    }


    /**
     * @uri
     * /api/v1/authentication/login
     *
     * @method
     * post
     *
     * @request
     * Dto {
     *     email: string
     *     password: string|length(min = 10; max = 30)
     * }
     *
     * @response
     * OK:
     * Tokens {
     *     access: string
     *     refresh: string
     * }
     *
     * 400 | 401:
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @RequestMapping(
            value = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtPairResponseDto> login(@RequestBody AuthenticationRequestDto request) {
        return authenticationService.login(request);
    }


    /**
     * @uri
     * /api/v1/authentication/refresh-tokens
     *
     * @method
     * post
     *
     * @headers
     * refresh-token: string
     *
     * @response
     * OK:
     * Tokens {
     *     access: string
     *     refresh: string
     * }
     *
     * 401:
     * Message {
     *     statusCode: int
     *     timestamp: long
     *     message: string
     *     description: string
     * }
     */
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(
            value = "refresh-tokens",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<JwtPairResponseDto> refresh(ServletRequest servletRequest) {
        JwtClaims claims = (JwtClaims) servletRequest.getAttribute("claims");
        String email = claims.getSub();
        return authenticationService.refreshTokens(email);
    }
}
