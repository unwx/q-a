package qa.tools.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import qa.logger.TestLogger;
import qa.tools.center.LogCenter;

public class LoggingExtension implements TestExecutionExceptionHandler, BeforeAllCallback, AfterAllCallback {

    private boolean superClass = true;
    private boolean hasNested = true;

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        final TestLogger logger = LogCenter.get();
        if (logger != null) {
            logger.end();
        }
        throw throwable;
    }

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
}
