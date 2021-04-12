package qa.logger;

public class LogCenter {

    private static TestLogger currentLogger;

    private LogCenter() {
    }

    public static TestLogger getCurrentLogger() {
        return currentLogger;
    }

    public static void setCurrentLogger(TestLogger logger) {
        currentLogger = logger;
    }
}
