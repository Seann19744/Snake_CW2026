package com.comp2042.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Direction Enum Unit Tests")
class DirectionTest {

    @Test
    @DisplayName("Verify Direction deltas and rotation angles")
    void testDirectionDeltasAndAngles() {
        assertEquals(0, Direction.UP.getDx());
        assertEquals(-1, Direction.UP.getDy());
        assertEquals(0, Direction.UP.getAngle());
        assertEquals(0, Direction.UP.getBodyAngle());

        assertEquals(0, Direction.DOWN.getDx());
        assertEquals(1, Direction.DOWN.getDy());
        assertEquals(180, Direction.DOWN.getAngle());
        assertEquals(0, Direction.DOWN.getBodyAngle());

        assertEquals(-1, Direction.LEFT.getDx());
        assertEquals(0, Direction.LEFT.getDy());
        assertEquals(270, Direction.LEFT.getAngle());
        assertEquals(270, Direction.LEFT.getBodyAngle());

        assertEquals(1, Direction.RIGHT.getDx());
        assertEquals(0, Direction.RIGHT.getDy());
        assertEquals(90, Direction.RIGHT.getAngle());
        assertEquals(270, Direction.RIGHT.getBodyAngle());
    }

    @Test
    @DisplayName("Verify isOpposite detects 180-degree opposite directions")
    void testIsOpposite() {
        assertTrue(Direction.UP.isOpposite(Direction.DOWN));
        assertTrue(Direction.DOWN.isOpposite(Direction.UP));
        assertTrue(Direction.LEFT.isOpposite(Direction.RIGHT));
        assertTrue(Direction.RIGHT.isOpposite(Direction.LEFT));

        assertFalse(Direction.UP.isOpposite(Direction.LEFT));
        assertFalse(Direction.UP.isOpposite(Direction.RIGHT));
        assertFalse(Direction.UP.isOpposite(Direction.UP));
        assertFalse(Direction.UP.isOpposite(null));
    }

    @Test
    @DisplayName("Verify corner rotation angles for all 8 perpendicular turns")
    void testCornerRotations() {
        assertEquals(180, Direction.getCornerRotation(Direction.LEFT, Direction.UP));
        assertEquals(180, Direction.getCornerRotation(Direction.DOWN, Direction.RIGHT));
        assertEquals(270, Direction.getCornerRotation(Direction.LEFT, Direction.DOWN));
        assertEquals(270, Direction.getCornerRotation(Direction.UP, Direction.RIGHT));
        assertEquals(90, Direction.getCornerRotation(Direction.RIGHT, Direction.UP));
        assertEquals(90, Direction.getCornerRotation(Direction.DOWN, Direction.LEFT));
        assertEquals(360, Direction.getCornerRotation(Direction.UP, Direction.LEFT));
        assertEquals(360, Direction.getCornerRotation(Direction.RIGHT, Direction.DOWN));
    }

    @Test
    @DisplayName("Verify char conversions")
    void testConversions() {
        assertEquals(Direction.UP, Direction.fromChar('U'));
        assertEquals(Direction.DOWN, Direction.fromChar('D'));
        assertEquals(Direction.LEFT, Direction.fromChar('L'));
        assertEquals(Direction.RIGHT, Direction.fromChar('R'));
        assertThrows(IllegalArgumentException.class, () -> Direction.fromChar('X'));
    }
}
