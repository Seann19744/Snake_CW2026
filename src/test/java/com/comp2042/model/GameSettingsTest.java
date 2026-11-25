package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

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
    @DisplayName("Verify resetDefaults restores gridEnabled to true and highScore to 0")
    void testResetDefaults() {
        settings.setGridEnabled(false);
        settings.updateHighScore(45);
        assertFalse(settings.isGridEnabled());
        assertEquals(45, settings.getHighScore());

        settings.resetDefaults();
        assertTrue(settings.isGridEnabled(), "resetDefaults should restore gridEnabled to true");
        assertEquals(0, settings.getHighScore(), "resetDefaults should reset highScore to 0");
    }

    @Test
    @DisplayName("Verify updateHighScore only updates when new score exceeds current highScore")
    void testUpdateHighScore() {
        assertEquals(0, settings.getHighScore());

        settings.updateHighScore(25);
        assertEquals(25, settings.getHighScore(), "HighScore should update to 25");

        settings.updateHighScore(10);
        assertEquals(25, settings.getHighScore(), "Lower score (10) should not overwrite highScore (25)");

        settings.updateHighScore(60);
        assertEquals(60, settings.getHighScore(), "Higher score (60) should update highScore");
    }

    @Test
    @DisplayName("Verify highScore persists to disk and reloads across sessions")
    void testHighScoreDiskPersistence(@TempDir Path tempDir) {
        Path tempScoreFile = tempDir.resolve("test_highscore.txt");
        settings.setHighScoreFile(tempScoreFile);
        assertEquals(0, settings.getHighScore());

        settings.updateHighScore(85);
        assertEquals(85, settings.getHighScore());

        // Simulate restarting and reloading from the persisted file
        settings.resetDefaults();
        assertEquals(0, settings.getHighScore());
        settings.setHighScoreFile(tempScoreFile);
        assertEquals(85, settings.getHighScore(), "High score should be restored from disk after reload");
    }
}
