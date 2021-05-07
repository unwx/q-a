package qa.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.validation.wrapper.authentication.AuthenticationRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.RegistrationRequestValidationWrapper;
import qa.exceptions.validator.ValidationException;
import qa.logger.TestLogger;
import qa.source.ValidationPropertyDataSource;
import qa.tools.annotations.MockitoTest;
import qa.validator.ValidationChainAdditionalImpl;
import qa.validator.abstraction.ValidationChainAdditional;
import util.dao.query.params.UserQueryParameters;
import util.rest.JwtTestUtil;
import util.validation.ValidationTestUtil;

@MockitoTest
public class ValidationAuthenticationRequestsTest {

    private ValidationChainAdditional validationChain;
    private ValidationPropertyDataSource propertyDataSource;

    private final TestLogger logger = new TestLogger(ValidationAuthenticationRequestsTest.class);

    private static final String LOG_VALID               = "valid";
    private static final String LOG_INVALID_EMAIL       = "invalid email";
    private static final String LOG_INVALID_PASS        = "invalid password";
    private static final String LOG_INVALID_NULL        = "invalid | null fields";

    @BeforeAll
    void init() {
        validationChain = Mockito.spy(ValidationChainAdditionalImpl.class);
        propertyDataSource = ValidationTestUtil.mockValidationProperties();
    }

    @Nested
    class registration {

        @Test
        void valid() {
            logger.trace(LOG_VALID);
            final RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, JwtTestUtil.USER_EMAIL, JwtTestUtil.USER_PASSWORD), propertyDataSource
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields() {
            logger.trace(LOG_INVALID_NULL);
            final RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(null, JwtTestUtil.USER_EMAIL, JwtTestUtil.USER_PASSWORD), propertyDataSource
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_1() {
            logger.trace(LOG_INVALID_NULL);
            final RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, null, JwtTestUtil.USER_PASSWORD), propertyDataSource
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_2() {
            logger.trace(LOG_INVALID_NULL);
            final RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, JwtTestUtil.USER_EMAIL, null), propertyDataSource
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_email() {
            logger.trace(LOG_INVALID_EMAIL);
            final RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, "emai-@'''.com", JwtTestUtil.USER_PASSWORD), propertyDataSource
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_password() {
            logger.trace(LOG_INVALID_PASS);
            final RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, JwtTestUtil.USER_EMAIL, "12345"), propertyDataSource
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class login {

        @Test
        void valid() {
            logger.trace(LOG_VALID);
            final AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(JwtTestUtil.USER_EMAIL, JwtTestUtil.USER_PASSWORD), propertyDataSource
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields() {
            logger.trace(LOG_INVALID_NULL);
            final AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(null, JwtTestUtil.USER_PASSWORD), propertyDataSource
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_1() {
            logger.trace(LOG_INVALID_NULL);
            final AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(JwtTestUtil.USER_EMAIL, null), propertyDataSource
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_email() {
            logger.trace(LOG_INVALID_EMAIL);
            final AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest("emai-@'''.com", JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_password() {
            logger.trace(LOG_INVALID_PASS);
            final AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(JwtTestUtil.USER_EMAIL, "qwe123"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }
}
