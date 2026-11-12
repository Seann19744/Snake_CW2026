package com.comp2042;

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
     * @param snek the snake, used to avoid its head and body
     * @param coin the current coin, used to avoid its square
     * @param amount how many obstacles is added.
     */
    public void newObstacles(Snake snek, Coin coin, int amount) {
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
            } while (coin.getPos().equals(p) || snek.getBody().contains(p) || snek.getHead().equals(p));
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
}
