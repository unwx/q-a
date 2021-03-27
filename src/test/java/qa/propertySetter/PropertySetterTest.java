package qa.propertySetter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import qa.Entity;
import qa.NestedEntity;
import qa.config.spring.SpringConfig;
import qa.domain.setters.PropertySetter;
import qa.domain.setters.PropertySetterFactory;
import qa.exceptions.domain.SetterNotImplementedException;
import qa.exceptions.domain.SetterTargetIsNullException;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class PropertySetterTest {

    @Autowired
    private PropertySetterFactory propertySetterFactory;

    @Test
    public void entityNullAttributes() {
        Entity entity = new Entity();
        assertThat(entity.getId(), equalTo(null));
        assertThat(entity.getStr(), equalTo(null));
        assertThat(entity.getBool(), equalTo(null));
        assertThat(entity.getDate(), equalTo(null));
    }
    @Test
    public void default_CORRECT_CONFIGURATION() {
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
    public void nestedEntity_CORRECT_CONFIGURATION() {
        PropertySetter propertySetter = propertySetterFactory.getSetter(new Entity());
        Entity entity = new Entity();
        NestedEntity nestedEntity = new NestedEntity();

        propertySetter.set(entity, "nested1", nestedEntity);

        assertThat(entity.getNested1(), notNullValue());
    }

    @Test
    public void setAll_CORRECT_CONFIGURATION() {
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

    @Test
    public void setAll_NULL_CONFIGURATION() {
        PropertySetter propertySetter = propertySetterFactory.getSetter(new Entity());
        assertThrows(SetterTargetIsNullException.class, () -> propertySetter.set(null, "id", 1L));
        Entity entity = new Entity();
        assertThrows(SetterNotImplementedException.class, () -> propertySetter.set(entity, null, 1L));
        assertDoesNotThrow(() -> propertySetter.set(entity, "id", null));
    }
}
