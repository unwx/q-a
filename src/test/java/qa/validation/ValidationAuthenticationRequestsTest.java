package qa.validation;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.validation.wrapper.authentication.AuthenticationRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.RegistrationRequestValidationWrapper;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.util.dao.query.params.UserQueryParameters;
import qa.util.rest.JwtTestUtil;
import qa.util.validation.ValidationTestUtil;
import qa.validators.ValidationChainAdditionalImpl;
import qa.validators.abstraction.ValidationChainAdditional;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ValidationAuthenticationRequestsTest {

    private final ValidationChainAdditional validationChain = new ValidationChainAdditionalImpl();
    private ValidationPropertyDataSource propertyDataSource;

    @BeforeAll
    void init() {
        propertyDataSource = ValidationTestUtil.mockValidationProperties();
    }

    @Nested
    class registration {
        @Test
        void valid() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, JwtTestUtil.USER_EMAIL, JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(null, JwtTestUtil.USER_EMAIL, JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_1() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, null, JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_2() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, JwtTestUtil.USER_EMAIL, null), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_email() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, "emai-@'''.com", JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_password() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(UserQueryParameters.USERNAME, JwtTestUtil.USER_EMAIL, "12345"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class login {
        @Test
        void valid() {
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(JwtTestUtil.USER_EMAIL, JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields() {
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(null, JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_1() {
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(JwtTestUtil.USER_EMAIL, null), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_email() {
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest("emai-@'''.com", JwtTestUtil.USER_PASSWORD), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_password() {
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(JwtTestUtil.USER_EMAIL, "qwe123"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }
}
