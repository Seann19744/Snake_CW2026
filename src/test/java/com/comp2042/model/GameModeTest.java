package com.comp2042.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameMode Enum Unit Tests")
class GameModeTest {

    @Test
    @DisplayName("Verify Quick Mode configuration: 2 fewer coins, wrapping borders, 1x growth")
    void testQuickMode() {
        GameMode mode = GameMode.QUICK;
        assertEquals("Quick Mode", mode.getDisplayName());
        assertEquals(8.5, mode.getSpeed(), 0.001);
        assertTrue(mode.isNoBorder(), "Quick mode should enable border wrap-around");
        assertEquals(6, mode.getCoinsRequired(1), "Level 1 should require 6 coins (8 - 2)");
        assertEquals(8, mode.getCoinsRequired(2), "Level 2 should require 8 coins (10 - 2)");
        assertEquals(10, mode.getCoinsRequired(3), "Level 3 should require 10 coins (12 - 2)");
        assertEquals(1, mode.getGrowthRate(), "Quick mode should have standard 1x growth rate");
        assertNotNull(mode.getDescription());
        assertTrue(mode.getDescription().contains("pass safely through screen edges"));
    }

    @Test
    @DisplayName("Verify Classic Mode configuration: standard coins, fatal borders, 1x growth")
    void testClassicMode() {
        GameMode mode = GameMode.CLASSIC;
        assertEquals("Classic Mode", mode.getDisplayName());
        assertEquals(8.5, mode.getSpeed(), 0.001);
        assertFalse(mode.isNoBorder(), "Classic mode should have fatal borders");
        assertEquals(8, mode.getCoinsRequired(1), "Level 1 should require 8 coins");
        assertEquals(10, mode.getCoinsRequired(2), "Level 2 should require 10 coins");
        assertEquals(12, mode.getCoinsRequired(3), "Level 3 should require 12 coins");
        assertEquals(1, mode.getGrowthRate(), "Classic mode should have standard 1x growth rate");
        assertNotNull(mode.getDescription());
        assertTrue(mode.getDescription().contains("solid, deadly walls"));
    }

    @Test
    @DisplayName("Verify Hardcore Mode configuration: +2 coins, high speed, fatal borders, 2x growth")
    void testHardcoreMode() {
        GameMode mode = GameMode.HARDCORE;
        assertEquals("Hardcore Mode", mode.getDisplayName());
        assertEquals(13.0, mode.getSpeed(), 0.001);
        assertFalse(mode.isNoBorder(), "Hardcore mode should have fatal borders");
        assertEquals(10, mode.getCoinsRequired(1), "Level 1 should require 10 coins (8 + 2)");
        assertEquals(12, mode.getCoinsRequired(2), "Level 2 should require 12 coins (10 + 2)");
        assertEquals(14, mode.getCoinsRequired(3), "Level 3 should require 14 coins (12 + 2)");
        assertEquals(2, mode.getGrowthRate(), "Hardcore mode must increase snake by 2 segments per coin");
        assertNotNull(mode.getDescription());
        assertTrue(mode.getDescription().contains("2 body segments"));
    }

    @Test
    @DisplayName("Verify GameMode values array contains exactly 3 modes")
    void testEnumValues() {
        GameMode[] modes = GameMode.values();
        assertEquals(3, modes.length);
        assertArrayEquals(new GameMode[]{GameMode.QUICK, GameMode.CLASSIC, GameMode.HARDCORE}, modes);
    }
}
