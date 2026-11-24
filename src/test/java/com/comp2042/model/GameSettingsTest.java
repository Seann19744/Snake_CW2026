package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameSettings Unit Tests")
class GameSettingsTest {

    private GameSettings settings;

    @BeforeEach
    void setUp() {
        settings = GameSettings.getInstance();
        settings.resetDefaults();
    }

    @Test
    @DisplayName("Verify GameSettings singleton instance is non-null and persistent")
    void testSingletonInstance() {
        assertNotNull(settings);
        assertSame(settings, GameSettings.getInstance(), "getInstance should return the same singleton reference");
    }

    @Test
    @DisplayName("Verify default gridEnabled is true")
    void testDefaultGridEnabled() {
        assertTrue(settings.isGridEnabled(), "Grid should be enabled by default");
    }

    @Test
    @DisplayName("Verify setGridEnabled updates the state")
    void testSetGridEnabled() {
        settings.setGridEnabled(false);
        assertFalse(settings.isGridEnabled(), "Grid should be disabled");

        settings.setGridEnabled(true);
        assertTrue(settings.isGridEnabled(), "Grid should be re-enabled");
    }

    @Test
    @DisplayName("Verify toggleGrid flips between true and false")
    void testToggleGrid() {
        assertTrue(settings.isGridEnabled());

        settings.toggleGrid();
        assertFalse(settings.isGridEnabled(), "Grid should be false after toggle");

        settings.toggleGrid();
        assertTrue(settings.isGridEnabled(), "Grid should be true after second toggle");
    }

    @Test
    @DisplayName("Verify resetDefaults restores gridEnabled to true")
    void testResetDefaults() {
        settings.setGridEnabled(false);
        assertFalse(settings.isGridEnabled());

        settings.resetDefaults();
        assertTrue(settings.isGridEnabled(), "resetDefaults should restore gridEnabled to true");
    }
}
