package util.validation;

import org.mockito.Mockito;
import qa.source.ValidationPropertyDataSource;

public class ValidationTestUtil {

    private static ValidationPropertyDataSource propertyDataSource;

    private ValidationTestUtil() {}

    public static ValidationPropertyDataSource mockValidationProperties() {
        if (propertyDataSource == null) {
            propertyDataSource = Mockito.mock(ValidationPropertyDataSource.class, Mockito.RETURNS_DEEP_STUBS);
            Mockito.lenient().when(propertyDataSource.getAnswer().getANSWER_TEXT_LENGTH_MAX()).thenReturn(2000);
            Mockito.lenient().when(propertyDataSource.getAnswer().getANSWER_TEXT_LENGTH_MIN()).thenReturn(20);
            Mockito.lenient().when(propertyDataSource.getAuthentication().getAUTHENTICATION_PASSWORD_LENGTH_MAX()).thenReturn(30);
            Mockito.lenient().when(propertyDataSource.getAuthentication().getAUTHENTICATION_PASSWORD_LENGTH_MIN()).thenReturn(10);
            Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TAG_LENGTH_MAX()).thenReturn(20);
            Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TAG_LENGTH_MIN()).thenReturn(2);
            Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TAG_REGEXP()).thenReturn("^(?![_.\\- ])(?!.*[_.-]{2})[a-zA-Z0-9._\\-]+(?<![_.\\- ])$");
            Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TAGS_COUNT_MAX()).thenReturn(7);
            Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TAGS_COUNT_MIN()).thenReturn(1);
            Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TEXT_LENGTH_MAX()).thenReturn(2000);
            Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TEXT_LENGTH_MIN()).thenReturn(50);
            Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TITLE_LENGTH_MAX()).thenReturn(50);
            Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TITLE_LENGTH_MIN()).thenReturn(10);
            Mockito.lenient().when(propertyDataSource.getUser().getUSER_ABOUT_LENGTH_MAX()).thenReturn(1024);
            Mockito.lenient().when(propertyDataSource.getUser().getUSER_ABOUT_LENGTH_MIN()).thenReturn(1);
            Mockito.lenient().when(propertyDataSource.getUser().getUSER_USERNAME_LENGTH_MAX()).thenReturn(30);
            Mockito.lenient().when(propertyDataSource.getUser().getUSER_USERNAME_LENGTH_MIN()).thenReturn(2);
            Mockito.lenient().when(propertyDataSource.getUser().getUSER_USERNAME_REGEXP()).thenReturn("^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
            Mockito.lenient().when(propertyDataSource.getComment().getCOMMENT_TEXT_LENGTH_MIN()).thenReturn(15);
            Mockito.lenient().when(propertyDataSource.getComment().getCOMMENT_TEXT_LENGTH_MAX()).thenReturn(500);
        }
        return propertyDataSource;
    }
}
