package qa.validation;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.annotations.Logged;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.validation.wrapper.authentication.AuthenticationRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.RegistrationRequestValidationWrapper;
import qa.exceptions.validator.ValidationException;
import qa.logger.LoggingExtension;
import qa.logger.TestLogger;
import qa.source.ValidationPropertyDataSource;
import qa.util.dao.query.params.UserQueryParameters;
import qa.util.rest.JwtTestUtil;
import qa.util.validation.ValidationTestUtil;
import qa.validators.ValidationChainAdditionalImpl;
import qa.validators.abstraction.ValidationChainAdditional;

@ExtendWith({MockitoExtension.class, LoggingExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ValidationAuthenticationRequestsTest {

    private final ValidationChainAdditional validationChain = new ValidationChainAdditionalImpl();
    private ValidationPropertyDataSource propertyDataSource;

    private final TestLogger logger = new TestLogger(ValidationAuthenticationRequestsTest.class);

    @BeforeAll
    void init() {
        propertyDataSource = ValidationTestUtil.mockValidationProperties();
    }

    @Logged
    class registration {

        @BeforeAll
        void init() {
            logger.nested(registration.class);
        }

        @Test
        void valid() {
            logger.trace("valid");
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, JwtTestUtil.USER_EMAIL, JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields() {
            logger.trace("invalid. null fields (1)");
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(null, JwtTestUtil.USER_EMAIL, JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_1() {
            logger.trace("invalid. null fields (2)");
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, null, JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_2() {
            logger.trace("invalid. null fields (3)");
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, JwtTestUtil.USER_EMAIL, null), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_email() {
            logger.trace("invalid email");
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, "emai-@'''.com", JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_password() {
            logger.trace("invalid password");
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, JwtTestUtil.USER_EMAIL, "12345"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Logged
    class login {

        @BeforeAll
        void init() {
            logger.nested(login.class);
        }

        @Test
        void valid() {
            logger.trace("valid");
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(JwtTestUtil.USER_EMAIL, JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields() {
            logger.trace("invalid. null fields (1)");
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(null, JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_1() {
            logger.trace("invalid. null fields (2)");
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(JwtTestUtil.USER_EMAIL, null), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_email() {
            logger.trace("invalid email");
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest("emai-@'''.com", JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_password() {
            logger.trace("invalid password");
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(JwtTestUtil.USER_EMAIL, "qwe123"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @AfterAll
    void close() {
        logger.end();
    }
}
