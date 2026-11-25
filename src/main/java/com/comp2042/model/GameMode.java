package com.comp2042.model;

/**
 * Defines the playable game modes of the game.
 * Each mode encapsulates speed, boundary behavior (fatal vs wrapping),
 * coins required to clear each level, and snake body growth rate per coin eaten.
 */
public enum GameMode {
    QUICK("Quick Mode", 8.5, true, 6, 8, 10, 1,
            "Normal Speed | Borderless Wrap | Growth +1 | 6/8/10 Coins"),
    CLASSIC("Classic Mode", 8.5, false, 8, 10, 12, 1,
            "Normal Speed | Solid Borders | Growth +1 | 8/10/12 Coins"),
    HARDCORE("Hardcore Mode", 13.0, false, 10, 12, 14, 2,
            "Fast Speed | Solid Borders | Double Growth (+2) | 10/12/14 Coins");

    private final String displayName;
    private final double speed;
    private final boolean noBorder;
    private final int level1Coins;
    private final int level2Coins;
    private final int level3Coins;
    private final int growthRate;
    private final String description;

    GameMode(String displayName, double speed, boolean noBorder,
             int level1Coins, int level2Coins, int level3Coins, int growthRate, String description) {
        this.displayName = displayName;
        this.speed = speed;
        this.noBorder = noBorder;
        this.level1Coins = level1Coins;
        this.level2Coins = level2Coins;
        this.level3Coins = level3Coins;
        this.growthRate = growthRate;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isNoBorder() {
        return noBorder;
    }

    /**
     * Returns the number of coins required to complete the specified level.
     *
     * @param level the level number (1, 2, or 3)
     * @return number of coins required to complete the level
     */
    public int getCoinsRequired(int level) {
        return switch (level) {
            case 1 -> level1Coins;
            case 2 -> level2Coins;
            case 3 -> level3Coins;
            default -> level3Coins;
        };
    }

    /**
     * Returns how many segments the snake grows by when eating a coin in this mode.
     *
     * @return growth rate in segments
     */
    public int getGrowthRate() {
        return growthRate;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
