package com.comp2042.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Singleton configuration model managing user-configurable game settings
 * such as grid display preferences and persistent high score across application restarts.
 */
public class GameSettings {

    private static final Path DEFAULT_HIGH_SCORE_FILE =
            Paths.get("highscore.txt");

    private static final GameSettings INSTANCE = new GameSettings();

    private boolean gridEnabled = true;
    private int highScore = 0;
    private Path highScoreFile = DEFAULT_HIGH_SCORE_FILE;

    private GameSettings() {
        loadHighScore();
        if (highScoreFile != null && !Files.exists(highScoreFile)) {
            saveHighScore();
        }
    }

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

    public int getHighScore() {
        return highScore;
    }

    /**
     * Updates the best score if the provided score exceeds the current record,
     * and persists the new record to disk so it remains after closing the game.
     *
     * @param score the newly achieved score
     */
    public void updateHighScore(int score) {
        if (score > this.highScore) {
            this.highScore = score;
            saveHighScore();
        }
    }

    /**
     * Configures the file path used to persist the high score and loads any existing record from it.
     *
     * @param highScoreFile path to the high score file, or null to disable disk writes during in-memory tests
     */
    public void setHighScoreFile(Path highScoreFile) {
        this.highScoreFile = highScoreFile;
        loadHighScore();
    }

    /**
     * Loads the saved high score from disk if the file exists.
     */
    public void loadHighScore() {
        if (highScoreFile == null) {
            return;
        }
        try {
            if (Files.exists(highScoreFile)) {
                String content = Files.readString(highScoreFile, StandardCharsets.UTF_8).trim();
                if (!content.isEmpty()) {
                    this.highScore = Math.max(0, Integer.parseInt(content));
                }
            } else {
                this.highScore = 0;
            }
        } catch (IOException | NumberFormatException ignored) {
            this.highScore = 0;
        }
    }

    private void saveHighScore() {
        if (highScoreFile == null) {
            return;
        }
        try {
            Files.writeString(highScoreFile, Integer.toString(this.highScore), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    public void resetDefaults() {
        this.gridEnabled = true;
        this.highScore = 0;
        this.highScoreFile = null;
    }
}
