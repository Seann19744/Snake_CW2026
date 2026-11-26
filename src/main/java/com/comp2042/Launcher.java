package com.comp2042;

/**
 * Application entry point wrapper that delegates to {@link Main#main(String[])}
 * without extending {@code javafx.application.Application}, bypassing JavaFX 11+
 * module-path runtime launcher restrictions in IDEs and packaged JARs.
 */
public class Launcher {

    /**
     * Launches the Snake JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
