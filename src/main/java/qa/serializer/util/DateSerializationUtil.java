package qa.serializer.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializationUtil {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private DateSerializationUtil() {}

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return dateFormat.format(date);
    }
}
