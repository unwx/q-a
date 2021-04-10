package qa.util.serialization;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateSerializationUtil {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private DateSerializationUtil() {
    }

    public static String dateToString(java.util.Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String dateToString(java.util.Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return dateFormat.format(date);
    }
}
