package com.comp2042.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Difficulty Enum Unit Tests")
class DifficultyTest {

    @Test
    @DisplayName("Verify Easy difficulty parameters: speed 8.5 and noBorder enabled")
    void testEasyDifficulty() {
        Difficulty easy = Difficulty.EASY;
        assertEquals(8.5, easy.getSpeed(), 0.001, "Easy speed should be 8.5");
        assertTrue(easy.isNoBorder(), "Easy mode should have noBorder enabled (wrap around)");
    }

    @Test
    @DisplayName("Verify Normal difficulty parameters: speed 8.5 and fatal borders")
    void testNormalDifficulty() {
        Difficulty normal = Difficulty.NORMAL;
        assertEquals(8.5, normal.getSpeed(), 0.001, "Normal speed should be 8.5");
        assertFalse(normal.isNoBorder(), "Normal mode should have fatal borders");
    }

    @Test
    @DisplayName("Verify Hard difficulty parameters: speed 13.0 and fatal borders")
    void testHardDifficulty() {
        Difficulty hard = Difficulty.HARD;
        assertEquals(13.0, hard.getSpeed(), 0.001, "Hard speed should be 13.0");
        assertFalse(hard.isNoBorder(), "Hard mode should have fatal borders");
    }

    @Test
    @DisplayName("Verify all expected difficulties are present in Difficulty.values()")
    void testEnumValues() {
        Difficulty[] values = Difficulty.values();
        assertEquals(3, values.length, "Difficulty should contain exactly 3 levels");
        assertArrayEquals(new Difficulty[]{Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD}, values);
    }
}
