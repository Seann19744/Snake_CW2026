package com.comp2042.model;

public class Game {
    public static final int MAX_LEVEL = 3;

    private final int width;
    private final int height;
    private final double multiplier;
    private int scorePoints;
    private final double speed;
    private final boolean powerUp;
    private final boolean noBorder;

    private final GameMode gameMode;
    private int currentLevel = 1;
    private int levelCoinsEaten = 0;
    private int totalCoinsEaten = 0;

    public Game(int x, int y, boolean powerUp, GameMode gameMode) {
        this.gameMode = gameMode;
        this.speed = gameMode.getSpeed();
        this.noBorder = gameMode.isNoBorder();
        this.powerUp = powerUp;
        this.width = x;
        this.height = y;
        if (height > 50) {
            this.multiplier = 10;
        } else {
            this.multiplier = 20;
        }
    }

    /**
     * Returns whether the player has won the entire game (completed the final level).
     */
    public boolean gameWon() {
        return currentLevel == MAX_LEVEL && isLevelCompleted();
    }

    /**
     * Checks if the required coin quota for the current level has been reached.
     */
    public boolean isLevelCompleted() {
        return levelCoinsEaten >= gameMode.getCoinsRequired(currentLevel);
    }

    /**
     * Advances to the next level if not already at MAX_LEVEL.
     *
     * @return true if successfully advanced to a new level, false if already at MAX_LEVEL
     */
    public boolean advanceLevel() {
        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            levelCoinsEaten = 0;
            return true;
        }
        return false;
    }

    /**
     * Increments the current level and total coin counters when a coin is collected.
     */
    public void recordCoinEaten() {
        levelCoinsEaten++;
        totalCoinsEaten++;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resetScore() {
        scorePoints = 0;
        currentLevel = 1;
        levelCoinsEaten = 0;
        totalCoinsEaten = 0;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void addPoints(int n) {
        scorePoints += n;
        GameSettings.getInstance().updateHighScore(scorePoints);
    }

    public int getScorePoints() {
        return scorePoints;
    }

    public boolean isPowerUp() {
        return powerUp;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isNoBorder() {
        return noBorder;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getLevelCoinsEaten() {
        return levelCoinsEaten;
    }

    public int getTotalCoinsEaten() {
        return totalCoinsEaten;
    }

    public int getCoinsRequiredForCurrentLevel() {
        return gameMode.getCoinsRequired(currentLevel);
    }
}
