package qa.serializer.user;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.domain.User;

import java.io.IOException;

public class AuthorSerializer extends JsonSerializer<User> {

    private static final String AUTHOR = "author";
    private static final String USERNAME = "username";

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        final String username = user.getUsername();

        gen.writeObjectFieldStart(AUTHOR);
        gen.writeStringField(USERNAME, username);
        gen.writeEndObject();
    }
}
