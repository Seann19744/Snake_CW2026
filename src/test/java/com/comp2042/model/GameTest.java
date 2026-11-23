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
        game = new Game(20, 20, true, Difficulty.NORMAL);
    }

    @Test
    @DisplayName("Verify Game initialization sets dimensions, speed, and difficulty correctly")
    void testGameInitialization() {
        assertEquals(20, game.getWidth(), "Width should be 20");
        assertEquals(20, game.getHeight(), "Height should be 20");
        assertTrue(game.isPowerUp(), "PowerUp flag should be true");
        assertEquals(8.5, game.getSpeed(), 0.001, "Speed should match Difficulty.NORMAL (8.5)");
        assertFalse(game.isNoBorder(), "noBorder should match Difficulty.NORMAL (false)");
        assertEquals(0, game.getScorePoints(), "Initial score points should be 0");
    }

    @Test
    @DisplayName("Verify grid multiplier calculation based on height")
    void testMultiplierCalculation() {
        // Height <= 50 -> multiplier is 20
        Game standardGame = new Game(20, 20, false, Difficulty.EASY);
        assertEquals(20.0, standardGame.getMultiplier(), "Multiplier for height <= 50 should be 20");

        // Height > 50 -> multiplier is 10
        Game largeGame = new Game(60, 60, false, Difficulty.HARD);
        assertEquals(10.0, largeGame.getMultiplier(), "Multiplier for height > 50 should be 10");
    }

    @Test
    @DisplayName("Verify score incrementation and point addition")
    void testScoreAndPoints() {
        game.incScore();
        game.incScore();
        game.addPoints(15);

        assertEquals(15, game.getScorePoints(), "Score points should be 15 after adding 15");

        game.resetScore();
        assertEquals(0, game.getScorePoints(), "Score points should reset to 0");
    }

    @Test
    @DisplayName("Verify decMaxScore adjusts victory threshold by (num - 1)")
    void testDecMaxScore() {
        // Initial maxScore for 20x20 is 20*20 - 2 = 398
        // Calling decMaxScore(5) reduces maxScore by (5 - 1) = 4 -> 394
        game.decMaxScore(5);
        // We verify that reaching 394 triggers gameWon
        for (int i = 0; i < 394; i++) {
            assertFalse(game.gameWon(), "Game should not be won before reaching maxScore");
            game.incScore();
        }
        assertTrue(game.gameWon(), "Game should be won when score reaches 394");
    }

    @Test
    @DisplayName("Verify gameWon returns true only when score equals maxScore")
    void testGameWon() {
        Game smallGame = new Game(2, 2, false, Difficulty.NORMAL);
        // For 2x2: maxScore = 2*2 - 2 = 2
        assertFalse(smallGame.gameWon(), "Game should not be won initially");
        smallGame.incScore();
        assertFalse(smallGame.gameWon(), "Game should not be won with score 1");
        smallGame.incScore();
        assertTrue(smallGame.gameWon(), "Game should be won when score equals maxScore (2)");
    }
}
