package qa.serializer.question;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class QuestionTagsSerializer extends JsonSerializer<String[]> {
    @Override
    public void serialize(String[] strings, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeArrayFieldStart("tags");
        for (String s : strings) {
            jsonGenerator.writeString(s);
        }
        jsonGenerator.writeEndArray();
    }
}
