package qa.util.serialization;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public class QuestionSerializationUtil {

    private QuestionSerializationUtil() {
    }

    public static void writeViewTags(String[] tags, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeArrayFieldStart("tags");
        for (String s : tags) {
            jsonGenerator.writeString(s);
        }
        jsonGenerator.writeEndArray();
    }
}
