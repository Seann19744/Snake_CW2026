package com.comp2042.model;

import javafx.scene.paint.Color;

public class Game {
    public static final int MAX_LEVEL = 3;

    private Color color = Color.web("feffe4");
    private int width;
    private int height;
    private int score;
    private int maxScore;
    private double multiplier;
    private int scorePoints;
    private double speed;
    private boolean powerUp;
    private boolean noBorder;

    private GameMode gameMode;
    private int currentLevel = 1;
    private int levelCoinsEaten = 0;
    private int totalCoinsEaten = 0;

    public Game(int x, int y, boolean powerUp, GameMode gameMode) {
        this.gameMode = gameMode;
        speed = gameMode.getSpeed();
        noBorder = gameMode.isNoBorder();
        this.powerUp = powerUp;
        width = x;
        height = y;
        maxScore = x * y - 2;
        if (height > 50) {
            multiplier = 10;
        } else {
            multiplier = 20;
        }
    }

    /**
     * Backward-compatible constructor mapping legacy Difficulty to GameMode.
     */
    public Game(int x, int y, boolean powerUp, Difficulty difficulty) {
        this(x, y, powerUp, difficulty == Difficulty.EASY ? GameMode.QUICK
                : (difficulty == Difficulty.HARD ? GameMode.HARDCORE : GameMode.CLASSIC));
    }

    /**
     * Returns whether the player has won the entire game (completed the final
     * level).
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
     * @return true if successfully advanced to a new level, false if already at
     *         MAX_LEVEL
     */
    public boolean advanceLevel() {
        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            levelCoinsEaten = 0;
            return true;
        }
        return false;
    }

    public void incScore() {
        score++;
        levelCoinsEaten++;
        totalCoinsEaten++;
    }

    public void decMaxScore(int num) {
        this.maxScore -= (num - 1);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resetScore() {
        score = 0;
        scorePoints = 0;
        currentLevel = 1;
        levelCoinsEaten = 0;
        totalCoinsEaten = 0;
    }

    public Color getColor() {
        return color;
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
