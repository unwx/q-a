package qa.util.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import qa.domain.User;

import java.io.IOException;

public class AuthorSerializationUtil {

    private AuthorSerializationUtil() {
    }

    public static void writeAuthorUsername(User author, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeObjectFieldStart("author");
        jsonGenerator.writeStringField("username", author.getUsername());
        jsonGenerator.writeEndObject();
    }
}
