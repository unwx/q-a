package qa.logger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Nested
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Inherited
public @interface Logged {}
