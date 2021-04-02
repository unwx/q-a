package qa.util;

import java.util.Arrays;

public class QuestionTagsUtil {

    private QuestionTagsUtil() {
    }

    public static String tagsToString(String[] tags) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(tags).forEach((t) -> sb.append(t).append(","));
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String[] stringToTags(String tags) {
        return tags.split(",");
    }
}
