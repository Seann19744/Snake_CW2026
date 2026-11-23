package com.comp2042;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

public abstract class JavaFXTestBase {

    private static boolean initialized = false;

    @BeforeAll
    public static synchronized void initJavaFX() {
        if (!initialized) {
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException ignored) {
                // Toolkit already initialized
            }
            initialized = true;
        }
    }
}
