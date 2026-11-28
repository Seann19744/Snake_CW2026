package com.comp2042;

import com.comp2042.model.GameSettings;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(JavaFXTestBase.FxThreadExtension.class)
public abstract class JavaFXTestBase {

    private static boolean initialized = false;

    @BeforeAll
    public static synchronized void initJavaFX() {
        GameSettings.getInstance().resetDefaults();
        if (!initialized) {
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException ignored) {
                // Toolkit already initialized
            }
            initialized = true;
        }
    }

    /**
     * Executes {@code @BeforeEach} and {@code @Test} methods on the JavaFX Application Thread
     * so that JavaFX {@code Timeline.play()} and {@code Timeline.stop()} calls (which dispatch
     * asynchronously via {@code Platform.runLater} when invoked from non-FX threads in JavaFX 25)
     * execute synchronously and deterministically without thread race conditions.
     */
    public static class FxThreadExtension implements InvocationInterceptor {

        @Override
        public void interceptBeforeEachMethod(Invocation<Void> invocation,
                                              ReflectiveInvocationContext<Method> invocationContext,
                                              ExtensionContext extensionContext) throws Throwable {
            runOnFxThread(invocation);
        }

        @Override
        public void interceptTestMethod(Invocation<Void> invocation,
                                        ReflectiveInvocationContext<Method> invocationContext,
                                        ExtensionContext extensionContext) throws Throwable {
            runOnFxThread(invocation);
        }

        private void runOnFxThread(Invocation<Void> invocation) throws Throwable {
            if (Platform.isFxApplicationThread()) {
                invocation.proceed();
                return;
            }
            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<Throwable> error = new AtomicReference<>();
            Platform.runLater(() -> {
                try {
                    invocation.proceed();
                } catch (Throwable t) {
                    error.set(t);
                } finally {
                    latch.countDown();
                }
            });
            latch.await();
            if (error.get() != null) {
                throw error.get();
            }
        }
    }
}
