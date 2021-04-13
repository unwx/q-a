package qa.tools.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import qa.tools.center.LogCenter;

public class LoggingExtension implements TestExecutionExceptionHandler, BeforeAllCallback, AfterAllCallback {

    private boolean superClass = true;

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        LogCenter.get().end();
        throw throwable;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (superClass) {
            superClass = false;
            return;
        }

        Class<?> caller = extensionContext.getTestClass().orElseThrow(ClassNotFoundException::new);
        LogCenter.get().nested(caller);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (LogCenter.isLastClass())
            LogCenter.get().end();
    }
}
