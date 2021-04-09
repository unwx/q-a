package qa.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SerializerDateUtil {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private SerializerDateUtil() {
    }

    public static String dateToString(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
