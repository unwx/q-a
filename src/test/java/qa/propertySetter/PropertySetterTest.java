package qa.propertySetter;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.domain.setters.PropertySetter;
import qa.domain.setters.PropertySetterFactory;
import qa.entities.Entity;
import qa.entities.NestedEntity;
import qa.exceptions.domain.SetterNotImplementedException;
import qa.exceptions.domain.SetterTargetIsNullException;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PropertySetterTest {

    @InjectMocks
    private PropertySetterFactory propertySetterFactory;

    @Test
    public void assert_correct_test_data() {
        Entity entity = new Entity();
        assertThat(entity.getId(), equalTo(null));
        assertThat(entity.getStr(), equalTo(null));
        assertThat(entity.getBool(), equalTo(null));
        assertThat(entity.getDate(), equalTo(null));
    }

    @Nested
    class correct_parameters {
        @Test
        public void default_entity() {
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
            PropertySetter propertySetter = propertySetterFactory.getSetter(new Entity());
            Entity entity = new Entity();
            NestedEntity nestedEntity = new NestedEntity();

            propertySetter.set(entity, "nested1", nestedEntity);

            assertThat(entity.getNested1(), notNullValue());
        }

        @Test
        public void set_all_default() {
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
        PropertySetter propertySetter = propertySetterFactory.getSetter(new Entity());
        assertThrows(SetterTargetIsNullException.class, () -> propertySetter.set(null, "id", 1L));
        Entity entity = new Entity();
        assertThrows(SetterNotImplementedException.class, () -> propertySetter.set(entity, null, 1L));
        assertDoesNotThrow(() -> propertySetter.set(entity, "id", null));
    }
}
