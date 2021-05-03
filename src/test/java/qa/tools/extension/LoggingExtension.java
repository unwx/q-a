package qa.tools.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import qa.logger.TestLogger;
import qa.tools.center.LogCenter;

public class LoggingExtension implements BeforeAllCallback, AfterAllCallback, AfterEachCallback {

    private boolean superClass = true;
    private boolean hasNested = true;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (superClass) {
            superClass = false;
            if (extensionContext.getTestClass().orElseThrow().getDeclaredClasses().length == 0) {
                hasNested = false;
            }
            return;
        }

        Class<?> caller = extensionContext.getTestClass().orElseThrow(ClassNotFoundException::new);
        LogCenter.get().nested(caller);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (!hasNested)
            LogCenter.get().end();
        else if (LogCenter.isLastClass())
            LogCenter.get().end();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        final TestLogger logger = LogCenter.get();
        if (logger != null) logger.print();
    }
}
