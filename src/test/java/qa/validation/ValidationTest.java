package qa.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.request.question.QuestionCreateRequest;
import qa.dto.request.question.QuestionDeleteRequest;
import qa.dto.request.question.QuestionEditRequest;
import qa.dto.validation.wrapper.answer.AnswerCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.AuthenticationRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.RegistrationRequestValidationWrapper;
import qa.dto.validation.wrapper.question.QuestionCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.question.QuestionDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.question.QuestionEditRequestValidationWrapper;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.validators.ValidationChainAdditionalImpl;
import qa.validators.abstraction.ValidationChainAdditional;

@ExtendWith(MockitoExtension.class)
public class ValidationTest {

    @Mock
    ValidationPropertyDataSource propertyDataSource;
    ValidationChainAdditional validationChain = new ValidationChainAdditionalImpl();

    @BeforeEach
    void init() {
        Mockito.lenient().when(propertyDataSource.getANSWER_TEXT_LENGTH_MAX()).thenReturn(2000);
        Mockito.lenient().when(propertyDataSource.getANSWER_TEXT_LENGTH_MIN()).thenReturn(20);
        Mockito.lenient().when(propertyDataSource.getAUTHENTICATION_PASSWORD_LENGTH_MAX()).thenReturn(30);
        Mockito.lenient().when(propertyDataSource.getAUTHENTICATION_PASSWORD_LENGTH_MIN()).thenReturn(10);
        Mockito.lenient().when(propertyDataSource.getQUESTION_TAG_LENGTH_MAX()).thenReturn(20);
        Mockito.lenient().when(propertyDataSource.getQUESTION_TAG_LENGTH_MIN()).thenReturn(2);
        Mockito.lenient().when(propertyDataSource.getQUESTION_TAG_REGEXP()).thenReturn("^(?![_.\\- ])(?!.*[_.-]{2})[a-zA-Z0-9._\\-]+(?<![_.\\- ])$");
        Mockito.lenient().when(propertyDataSource.getQUESTION_TAGS_COUNT_MAX()).thenReturn(7);
        Mockito.lenient().when(propertyDataSource.getQUESTION_TAGS_COUNT_MIN()).thenReturn(1);
        Mockito.lenient().when(propertyDataSource.getQUESTION_TEXT_LENGTH_MAX()).thenReturn(2000);
        Mockito.lenient().when(propertyDataSource.getQUESTION_TEXT_LENGTH_MIN()).thenReturn(50);
        Mockito.lenient().when(propertyDataSource.getQUESTION_TITLE_LENGTH_MAX()).thenReturn(50);
        Mockito.lenient().when(propertyDataSource.getQUESTION_TITLE_LENGTH_MIN()).thenReturn(10);
        Mockito.lenient().when(propertyDataSource.getUSER_ABOUT_LENGTH_MAX()).thenReturn(1024);
        Mockito.lenient().when(propertyDataSource.getUSER_ABOUT_LENGTH_MIN()).thenReturn(1);
        Mockito.lenient().when(propertyDataSource.getUSER_USERNAME_LENGTH_MAX()).thenReturn(30);
        Mockito.lenient().when(propertyDataSource.getUSER_USERNAME_LENGTH_MIN()).thenReturn(2);
        Mockito.lenient().when(propertyDataSource.getUSER_USERNAME_REGEXP()).thenReturn("^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
    }

    @Test
    void registrationValid() {
        RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                new RegistrationRequest("username132", "name@email.com", "password*(J:S#JOP$"), propertyDataSource);
        Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void registrationInvalid_NullField() {
        RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                new RegistrationRequest(null, "name@email.com", "password*(J:S#JOP$"), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void registrationInvalid_NullField_1() {
        RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                new RegistrationRequest("username", null, "password*(J:S#JOP$"), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void registrationInvalid_NullField_2() {
        RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                new RegistrationRequest("username", "email@email.com", null), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void registrationInvalid_InvalidEmail() {
        RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                new RegistrationRequest("correct", "emai-@'''.com", "password*(J:S#JOP$"), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void registrationInvalid_InvalidPassword() {
        RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                new RegistrationRequest("correct", "email@email.com", "12345"), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void loginValid() {
        AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                new AuthenticationRequest("email@email.com", "password12344321"), propertyDataSource);
        Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void loginInvalid_NullField() {
        AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                new AuthenticationRequest(null, "password12344321"), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void loginInvalid_NullField_1() {
        AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                new AuthenticationRequest("email@email.com", null), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void loginInvalid_InvalidEmail() {
        AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                new AuthenticationRequest("emai-@'''.com", "password12344321"), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void loginInvalid_InvalidPassword() {
        AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                new AuthenticationRequest("emai-@'''.com", "qwe123"), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void questionCreateValid() {
        QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                new QuestionCreateRequest("How do I mock an autowired @Value field in Spring", "\n" +
                        "\n" +
                        "I'm using Spring 3.1.4.RELEASE and Mockito 1.9.5. In my Spring class I have:\n" +
                        "\n" +
                        "@Value(\"#{myProps['default.url']}\")\n" +
                        "private String defaultUrl;\n" +
                        "\n" +
                        "@Value(\"#{myProps['default.password']}\")\n" +
                        "private String defaultrPassword;\n" +
                        "\n" +
                        "// ...\n" +
                        "\n" +
                        "From my JUnit test, which I currently have set up like so:\n" +
                        "\n" +
                        "@RunWith(SpringJUnit4ClassRunner.class)\n" +
                        "@ContextConfiguration({ \"classpath:test-context.xml\" })\n" +
                        "public class MyTest \n" +
                        "{ \n" +
                        "\n" +
                        "I would like to mock a value for my \"defaultUrl\" field. Note that I don't want to mock values for the other fields — I'd like to keep those as they are, only the \"defaultUrl\" field. Also note that I have no explicit \"setter\" methods (e.g. setDefaultUrl) in my class and I don't want to create any just for the purposes of testing.\n" +
                        "\n" +
                        "Given this, how can I mock a value for that one field?\n",
                        new String[]{"spring", "mockito", "autowired", "value-initialization"}), propertyDataSource);
        Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void questionCreateInvalid_InvalidTitle() {
        QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                new QuestionCreateRequest("How do",
                        "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq",
                        new String[]{"spring", "mockito", "autowired", "value-initialization"}), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void questionCreateInvalid_InvalidText() {
        QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                new QuestionCreateRequest("How do I mock an autowired @Value field in Spring",
                        "how do",
                        new String[]{"spring", "mockito", "autowired", "value-initialization"}), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void questionCreateInvalid_InvalidTags() {
        QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                new QuestionCreateRequest(
                        "How do I mock an autowired @Value field in Spring",
                        "\n" +
                                "I'm using Spring 3.1.4.RELEASE and Mockito 1.9.5. In my Spring class I have:\n" +
                                "\n" +
                                "@Value(\"#{myProps['default.url']}\")\n" +
                                "private String defaultUrl;",
                        new String[]{null, "mockito", "autowired"}), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));

        QuestionCreateRequestValidationWrapper validationWrapper1 = new QuestionCreateRequestValidationWrapper(
                new QuestionCreateRequest(
                        "How do I mock an autowired @Value field in Spring",
                        "\n" +
                                "I'm using Spring 3.1.4.RELEASE and Mockito 1.9.5. In my Spring class I have:\n" +
                                "\n" +
                                "@Value(\"#{myProps['default.url']}\")\n" +
                                "private String defaultUrl;",
                        new String[]{"sp_!#@$", "q"}), propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper1));
    }

    @Test
    void questionEditValid() {
        QuestionEditRequestValidationWrapper validationWrapper = new QuestionEditRequestValidationWrapper(
                new QuestionEditRequest(
                        1L, "\n" +
                        "I'm using Spring 3.1.4.RELEASE and Mockito 1.9.5. In my Spring class I have:\n" +
                        "\n" +
                        "@Value(\"#{myProps['default.url']}\")\n" +
                        "private String defaultUrl;",
                        new String[]{"spring", "mockito", "autowired", "value-initialization"}),
                propertyDataSource);
        Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void questionEditInvalid_InvalidId() {
        QuestionEditRequestValidationWrapper validationWrapper = new QuestionEditRequestValidationWrapper(
                new QuestionEditRequest(
                        -5L, "How do I mock an autowired @Value field in Spring",
                        new String[]{"spring", "mockito", "autowired", "value-initialization"}),
                propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void questionDeleteValid() {
        QuestionDeleteRequestValidationWrapper validationWrapper = new QuestionDeleteRequestValidationWrapper(
                new QuestionDeleteRequest(5L));
        Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void questionDeleteInvalid_InvalidId() {
        QuestionDeleteRequestValidationWrapper validationWrapper = new QuestionDeleteRequestValidationWrapper(
                new QuestionDeleteRequest(-5L));
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void answerCreateValid() {
        AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                new AnswerCreateRequest(
                        5L,
                        "You can use the magic of Spring's ReflectionTestUtils.setField in order to avoid making any modifications whatsoever to your code."),
                propertyDataSource);
        Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void answerCreateInvalid_InvalidId() {
        AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                new AnswerCreateRequest(
                        -5L,
                        "You can use the magic of Spring's ReflectionTestUtils.setField in order to avoid making any modifications whatsoever to your code."),
                propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }

    @Test
    void answerCreateInvalid_InvalidText() {
        AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                new AnswerCreateRequest(
                        5L,
                        "idk"),
                propertyDataSource);
        Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
    }
}
