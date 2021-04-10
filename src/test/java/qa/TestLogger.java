package qa;

import org.apache.logging.log4j.Logger;

public class TestLogger {

    public static final boolean ENABLE = true;
    public static final int DEEP = 0; // 0 = bottom. 0 + n = bottom + n class upper

    private static final String INDENT = "        ";

    private static int LAST_MAX_DEEP = 0;
    private static int LAST_DEEP = 0;

    private static final StringBuilder msgBuilder = new StringBuilder();

    private TestLogger() {
    }

    public static void trace(Logger logger, String message, int deep) {
        if (ENABLE) {
            if (deep >= DEEP) {
                System.out.print(printBefore(deep));
                logger.trace(message);
                if (LAST_MAX_DEEP < deep)
                    LAST_MAX_DEEP = deep;
                LAST_DEEP = deep;
            }
        }
    }

    public static void info(Logger logger, String message, int deep) {
        if (ENABLE) {
            if (deep >= DEEP) {
                System.out.print(printBefore(deep));
                logger.info(message);
                if (LAST_MAX_DEEP < deep)
                    LAST_MAX_DEEP = deep;
                LAST_DEEP = deep;
            }
        }
    }

    private static String printBefore(int deep) {
        if (!msgBuilder.isEmpty()) {
            msgBuilder.delete(0, msgBuilder.length());
        }
        msgBuilder.append(deep >= LAST_MAX_DEEP ? "" : INDENT.repeat(Math.max(0, LAST_MAX_DEEP - deep)));
        if (deep < LAST_DEEP) {
            msgBuilder.deleteCharAt(msgBuilder.length() - 1);
            msgBuilder.append("└╶╶");
        }
        return msgBuilder.toString();
    }
}