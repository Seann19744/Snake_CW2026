package com.comp2042.model;

import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Obstacle {
    private Color color = Color.BLACK;
    private ArrayList<Point> obstacles = new ArrayList<>();
    private int x;
    private int y;

    public Obstacle(int x, int y) {
       this.x = x;
       this.y = y;
    }

    /**
     * Adds randomly placed obstacles to the grid, avoiding the coin and snake.
     * Does nothing if more than 50 obstacles already exist, so the total can still pass 50 by the end of one call
     *
     * @param snake the snake, used to avoid its head and body
     * @param coin the current coin, used to avoid its square
     * @param amount how many obstacles is added.
     */
    public void newObstacles(Snake snake, Coin coin, int amount) {
        // Exit the method if there are already more than 50 obstacles
        if (obstacles.size() > 50) {
            return;
        }
        Random rnd = new Random();
        // Store each obstacle's coordinates
        Point p;
        for (int i=0; i<amount; i++) {
            do {
                p = new Point(rnd.nextInt(x), rnd.nextInt(y));
            } while (coin.getPos().equals(p) || snake.getBody().contains(p) || snake.getHead().equals(p));
            obstacles.add(p);
        }
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Point> getObstacles() {
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
            // Level 2: Four Pillars (2x2 blocks in each quadrant)
            int[][] offsets = {{4, 4}, {14, 4}, {4, 14}, {14, 14}};
            for (int[] base : offsets) {
                for (int dx = 0; dx < 2; dx++) {
                    for (int dy = 0; dy < 2; dy++) {
                        int px = (base[0] * x) / 20 + dx;
                        int py = (base[1] * y) / 20 + dy;
                        if (px >= 0 && px < x && py >= 0 && py < y) {
                            obstacles.add(new Point(px, py));
                        }
                    }
                }
            }
        } else if (level == 3) {
            // Level 3: The Labyrinth (interior corridor walls leaving center and outer ring open)
            int midX = x / 2;
            int midY = y / 2;
            for (int py = 2; py <= 6; py++) {
                obstacles.add(new Point(midX, py));
            }
            for (int py = y - 7; py <= y - 3; py++) {
                obstacles.add(new Point(midX, py));
            }
            for (int px = 2; px <= 6; px++) {
                obstacles.add(new Point(px, midY));
            }
            for (int px = x - 7; px <= x - 3; px++) {
                obstacles.add(new Point(px, midY));
            }
        }
    }
}
