package qa.logger;

public class TestLogger extends CustomLogger {

    public TestLogger(Class<?> clazz) {
        super(clazz);
        LogCenter.setCurrentLogger(this);
    }

    public void nested(Class<?> clazz) {
        super.nested(clazz);
    }

    public void trace(String message) {
        super.trace(message);
    }

    public void end() {
        super.end();
    }
}