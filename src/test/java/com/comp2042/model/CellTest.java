package com.comp2042.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cell Record Unit Tests")
class CellTest {

    @Test
    @DisplayName("Verify Cell coordinates and value equality")
    void testCellCoordinatesAndEquality() {
        Cell cell1 = new Cell(5, 10);
        Cell cell2 = new Cell(5, 10);
        Cell cell3 = new Cell(6, 10);

        assertEquals(5, cell1.x());
        assertEquals(10, cell1.y());
        assertEquals(cell1, cell2);
        assertEquals(cell1.hashCode(), cell2.hashCode());
        assertNotEquals(cell1, cell3);
    }

    @Test
    @DisplayName("Verify Cell translation by deltas and Direction returns new immutable instance")
    void testCellTranslate() {
        Cell origin = new Cell(10, 10);

        assertEquals(new Cell(10, 9), origin.translate(Direction.UP));
        assertEquals(new Cell(10, 11), origin.translate(Direction.DOWN));
        assertEquals(new Cell(9, 10), origin.translate(Direction.LEFT));
        assertEquals(new Cell(11, 10), origin.translate(Direction.RIGHT));
        assertEquals(new Cell(13, 8), origin.translate(3, -2));

        // Origin must remain unchanged (immutability)
        assertEquals(10, origin.x());
        assertEquals(10, origin.y());
    }
}
