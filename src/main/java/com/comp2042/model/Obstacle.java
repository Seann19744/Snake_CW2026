package com.comp2042.model;

import java.util.ArrayList;
import java.util.Random;

public class Obstacle {
    private final ArrayList<Cell> obstacles = new ArrayList<>();
    private final int x;
    private final int y;

    public Obstacle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Adds randomly placed obstacles to the grid, avoiding the coin, the snake,
     * existing obstacles, and a 2-tile safety radius around the snake's head.
     * Does nothing if more than 50 obstacles already exist.
     *
     * @param snake the snake, used to avoid its head and body
     * @param coin the current coin, used to avoid its square
     * @param amount how many obstacles are added
     */
    public void newObstacles(Snake snake, Coin coin, int amount) {
        if (obstacles.size() > 50) {
            return;
        }
        Random rnd = new Random();
        int maxAttempts = x * y * 4;
        Cell p;
        for (int i = 0; i < amount; i++) {
            int attempts = 0;
            do {
                p = new Cell(rnd.nextInt(x), rnd.nextInt(y));
                attempts++;
            } while (attempts < maxAttempts && (coin.getPos().equals(p)
                    || snake.getBody().contains(p)
                    || snake.getHead().equals(p)
                    || obstacles.contains(p)
                    || isTooCloseToHead(p, snake.getHead())));

            if (!coin.getPos().equals(p)
                    && !snake.getBody().contains(p)
                    && !snake.getHead().equals(p)
                    && !obstacles.contains(p)
                    && !isTooCloseToHead(p, snake.getHead())) {
                obstacles.add(p);
            }
        }
    }

    /**
     * Checks whether a candidate obstacle tile is within a 2-tile safety radius
     * around the snake's head (accounting for border wrap-around) to prevent
     * unfair unavoidable collisions immediately after eating a Silver Coin.
     *
     * @param candidate the candidate obstacle position
     * @param head the snake's current head position
     * @return true if the candidate is too close to the snake's head
     */
    public boolean isTooCloseToHead(Cell candidate, Cell head) {
        int rawDx = Math.abs(candidate.x() - head.x());
        int rawDy = Math.abs(candidate.y() - head.y());
        int dx = Math.min(rawDx, x - rawDx);
        int dy = Math.min(rawDy, y - rawDy);
        return dx <= 2 && dy <= 2;
    }

    public ArrayList<Cell> getObstacles() {
        return obstacles;
    }

    public void reset() {
        obstacles.clear();
    }

    /**
     * Loads the static obstacle layout for the specified level.
     * Level 1: Open arena (no static obstacles).
     * Level 2: "Four Pillars" (compact 2x2 pillars in each quadrant).
     * Level 3: "The Labyrinth" (cruciform barrier walls dividing the board into corridors).
     *
     * @param level the level to load (1, 2, or 3)
     */
    public void loadLevelLayout(int level) {
        reset();
        if (level == 2) {
            int[][] offsets = {{4, 4}, {14, 4}, {4, 14}, {14, 14}};
            for (int[] base : offsets) {
                for (int dx = 0; dx < 2; dx++) {
                    for (int dy = 0; dy < 2; dy++) {
                        int px = (base[0] * x) / 20 + dx;
                        int py = (base[1] * y) / 20 + dy;
                        if (px >= 0 && px < x && py >= 0 && py < y) {
                            obstacles.add(new Cell(px, py));
                        }
                    }
                }
            }
        } else if (level == 3) {
            int midX = x / 2;
            int midY = y / 2;
            for (int py = 2; py <= 6; py++) {
                obstacles.add(new Cell(midX, py));
            }
            for (int py = y - 7; py <= y - 3; py++) {
                obstacles.add(new Cell(midX, py));
            }
            for (int px = 2; px <= 6; px++) {
                obstacles.add(new Cell(px, midY));
            }
            for (int px = x - 7; px <= x - 3; px++) {
                obstacles.add(new Cell(px, midY));
            }
        }
    }
}
