package com.comp2042.model;

/**
 * Singleton configuration model managing user-configurable game settings
 * such as grid display preferences across menus and active gameplay sessions.
 */
public class GameSettings {

    private static final GameSettings INSTANCE = new GameSettings();

    private boolean gridEnabled = true;

    private GameSettings() {}

    public static GameSettings getInstance() {
        return INSTANCE;
    }

    public boolean isGridEnabled() {
        return gridEnabled;
    }

    public void setGridEnabled(boolean gridEnabled) {
        this.gridEnabled = gridEnabled;
    }

    public void toggleGrid() {
        this.gridEnabled = !this.gridEnabled;
    }

    public void resetDefaults() {
        this.gridEnabled = true;
    }
}
