package qa.logger;

import qa.tools.center.LogCenter;

public class TestLogger extends CustomLogger {

    private int clazzCount;

    public TestLogger(Class<?> clazz) {
        super(clazz);
        LogCenter.set(this);
        this.clazzCount = super.getClazzCount();
    }

    public void nested(Class<?> clazz) {
        super.nested(clazz);
        updateClazzCounter();
        updateCenterInfo();
    }

    public void trace(String message) {
        super.trace(message);
    }

    public void print() {
        super.print();
    }
    public void end() {
        super.print();
    }

    private void updateClazzCounter() {
        clazzCount--;
    }

    private void updateCenterInfo() {
        if (clazzCount == 1)
            LogCenter.setLastClass(true);
    }
}