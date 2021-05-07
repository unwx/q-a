package qa.tools.center;

import qa.logger.TestLogger;

public class LogCenter {

    private static TestLogger currentLogger;
    private static boolean lastClass = false;

    private LogCenter() {}

    public static TestLogger get() {
        return currentLogger;
    }

    public static void set(TestLogger logger) {
        currentLogger = logger;
    }

    public static boolean isLastClass() {
        return lastClass;
    }

    public static void setLastClass(boolean lastClass) {
        LogCenter.lastClass = lastClass;
    }
}
