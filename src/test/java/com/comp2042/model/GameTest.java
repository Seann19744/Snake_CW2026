package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Game Model Unit Tests")
class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(20, 20, true, GameMode.CLASSIC);
    }

    @Test
    @DisplayName("Verify Game initialization sets dimensions, speed, game mode, and level 1")
    void testGameInitialization() {
        assertEquals(20, game.getWidth(), "Width should be 20");
        assertEquals(20, game.getHeight(), "Height should be 20");
        assertTrue(game.isPowerUp(), "PowerUp flag should be true");
        assertEquals(GameMode.CLASSIC, game.getGameMode(), "GameMode should be CLASSIC");
        assertEquals(8.5, game.getSpeed(), 0.001, "Speed should match Classic mode (8.5)");
        assertFalse(game.isNoBorder(), "noBorder should match Classic mode (false)");
        assertEquals(0, game.getScorePoints(), "Initial score points should be 0");
        assertEquals(1, game.getCurrentLevel(), "Initial level should be 1");
        assertEquals(0, game.getLevelCoinsEaten(), "Initial level coins should be 0");
        assertEquals(0, game.getTotalCoinsEaten(), "Initial total coins should be 0");
    }

    @Test
    @DisplayName("Verify grid multiplier calculation based on height")
    void testMultiplierCalculation() {
        // Height <= 50 -> multiplier is 20
        Game standardGame = new Game(20, 20, false, GameMode.QUICK);
        assertEquals(20.0, standardGame.getMultiplier(), "Multiplier for height <= 50 should be 20");

        // Height > 50 -> multiplier is 10
        Game largeGame = new Game(60, 60, false, GameMode.HARDCORE);
        assertEquals(10.0, largeGame.getMultiplier(), "Multiplier for height > 50 should be 10");
    }

    @Test
    @DisplayName("Verify score incrementation and point addition")
    void testScoreAndPoints() {
        game.incScore();
        game.incScore();
        game.addPoints(15);

        assertEquals(15, game.getScorePoints(), "Score points should be 15 after adding 15");
        assertEquals(2, game.getLevelCoinsEaten(), "Level coins eaten should be 2");
        assertEquals(2, game.getTotalCoinsEaten(), "Total coins eaten should be 2");

        game.resetScore();
        assertEquals(0, game.getScorePoints(), "Score points should reset to 0");
        assertEquals(1, game.getCurrentLevel(), "Level should reset to 1");
        assertEquals(0, game.getLevelCoinsEaten(), "Level coins should reset to 0");
    }

    @Test
    @DisplayName("Verify level progression from Level 1 -> Level 2 -> Level 3 and victory on Level 3")
    void testLevelProgressionAndVictory() {
        // Level 1: requires 8 coins in Classic mode
        assertEquals(8, game.getCoinsRequiredForCurrentLevel());
        for (int i = 0; i < 7; i++) {
            game.incScore();
            assertFalse(game.isLevelCompleted(), "Level 1 should not be complete before 8 coins");
        }
        game.incScore(); // 8th coin
        assertTrue(game.isLevelCompleted(), "Level 1 should be complete after 8 coins");
        assertFalse(game.gameWon(), "Game should not be won after Level 1");

        // Advance to Level 2
        assertTrue(game.advanceLevel(), "Should advance to Level 2");
        assertEquals(2, game.getCurrentLevel());
        assertEquals(0, game.getLevelCoinsEaten());
        assertEquals(10, game.getCoinsRequiredForCurrentLevel());

        // Level 2: requires 10 coins
        for (int i = 0; i < 10; i++) {
            game.incScore();
        }
        assertTrue(game.isLevelCompleted());
        assertFalse(game.gameWon(), "Game should not be won after Level 2");

        // Advance to Level 3
        assertTrue(game.advanceLevel(), "Should advance to Level 3");
        assertEquals(3, game.getCurrentLevel());
        assertEquals(12, game.getCoinsRequiredForCurrentLevel());

        // Level 3: requires 12 coins
        for (int i = 0; i < 11; i++) {
            game.incScore();
            assertFalse(game.gameWon());
        }
        game.incScore(); // 12th coin
        assertTrue(game.isLevelCompleted());
        assertTrue(game.gameWon(), "Game should be won after completing Level 3!");

        // Cannot advance past Level 3
        assertFalse(game.advanceLevel(), "Cannot advance beyond max level");
    }
}
