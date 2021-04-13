package qa.propertySetter;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.annotations.Logged;
import qa.domain.setters.PropertySetter;
import qa.domain.setters.PropertySetterFactory;
import qa.entities.Entity;
import qa.entities.NestedEntity;
import qa.exceptions.domain.SetterNotImplementedException;
import qa.exceptions.domain.SetterTargetIsNullException;
import qa.logger.LoggingExtension;
import qa.logger.TestLogger;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({MockitoExtension.class, LoggingExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PropertySetterTest {

    @InjectMocks
    private PropertySetterFactory propertySetterFactory;

    private final TestLogger logger = new TestLogger(PropertySetterTest.class);

    @Test
    public void assert_correct_test_data() {
        Entity entity = new Entity();
        assertThat(entity.getId(), equalTo(null));
        assertThat(entity.getStr(), equalTo(null));
        assertThat(entity.getBool(), equalTo(null));
        assertThat(entity.getDate(), equalTo(null));
    }

    @Logged
    class correct_parameters {

        @BeforeAll
        void init() {
            logger.nested(correct_parameters.class);
        }

        @Test
        public void default_entity() {
            logger.trace("simple entity");
            PropertySetter propertySetter = propertySetterFactory.getSetter(new Entity());
            Entity entity = new Entity();

            propertySetter.set(entity, "id", 1L); //id - Long
            propertySetter.set(entity, "str", "string"); // str - String
            propertySetter.set(entity, "bool", true); // bool - Boolean
            propertySetter.set(entity, "date", LocalDateTime.of(1, 1, 1, 1, 1)); // date - LocalDateTime

            assertThat(entity.getId(), equalTo(1L));
            assertThat(entity.getStr(), equalTo("string"));
            assertThat(entity.getBool(), equalTo(true));
            assertThat(entity.getDate(), equalTo(LocalDateTime.of(1, 1, 1, 1, 1)));
        }

        @Test
        public void nested_entity() {
            logger.trace("nested entity");
            PropertySetter propertySetter = propertySetterFactory.getSetter(new Entity());
            Entity entity = new Entity();
            NestedEntity nestedEntity = new NestedEntity();

            propertySetter.set(entity, "nested1", nestedEntity);

            assertThat(entity.getNested1(), notNullValue());
        }

        @Test
        public void set_all_default() {
            logger.trace("set all primitive");
            PropertySetter propertySetter = propertySetterFactory.getSetter(new Entity());
            Entity entity = new Entity();

            propertySetter.setAll(
                    entity,
                    new String[]{"id", "str", "bool", "date"},
                    new Object[]{1L, "string", true, LocalDateTime.of(1, 1, 1, 1, 1)});

            assertThat(entity.getId(), equalTo(1L));
            assertThat(entity.getStr(), equalTo("string"));
            assertThat(entity.getBool(), equalTo(true));
            assertThat(entity.getDate(), equalTo(LocalDateTime.of(1, 1, 1, 1, 1)));
        }
    }

    @Test
    public void setAll_null_params() {
        logger.trace("set all null");
        PropertySetter propertySetter = propertySetterFactory.getSetter(new Entity());
        assertThrows(SetterTargetIsNullException.class, () -> propertySetter.set(null, "id", 1L));
        Entity entity = new Entity();
        assertThrows(SetterNotImplementedException.class, () -> propertySetter.set(entity, null, 1L));
        assertDoesNotThrow(() -> propertySetter.set(entity, "id", null));
    }

    @AfterAll
    void close() {
        logger.end();
    }
}
