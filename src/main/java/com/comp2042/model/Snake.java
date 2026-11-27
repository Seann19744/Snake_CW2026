package com.comp2042.model;

import java.awt.*;
import java.util.ArrayList;

public class Snake {
    private boolean dead = false;
    private final Point head = new Point();
    private final ArrayList<Point> body = new ArrayList<>();
    private final ArrayList<Direction> bodyDirs = new ArrayList<>();
    private final int gx;
    private final int gy;
    private boolean ignoreBorders = false;
    private boolean noBorder;
    private Obstacle obstacle;
    private Direction direction = Direction.LEFT;
    private int growthRate = 1;

    public Snake(int x, int y, boolean noBorder, Obstacle obstacle) {
        this(x, y, noBorder, obstacle, 1);
    }

    public Snake(int x, int y, boolean noBorder, Obstacle obstacle, int growthRate) {
        this.obstacle = obstacle;
        this.noBorder = noBorder;
        this.growthRate = growthRate;
        this.head.setLocation(x / 2, y / 2);
        this.body.add(new Point(x / 2 + 1, y / 2));
        this.bodyDirs.add(Direction.LEFT);
        this.gx = x;
        this.gy = y;
    }

    public void move(char dir) {
        move(Direction.fromChar(dir));
    }

    public void move(Direction dir) {
        if (dir == null) {
            return;
        }
        Point prevHead = new Point(head);
        this.direction = dir;
        head.translate(dir.getDx(), dir.getDy());

        if (borderControl()) {
            if (!ignoreBorders && !noBorder) {
                head.setLocation(prevHead);
                dead = true;
            } else {
                int wrappedX = ((int) head.getX() + gx) % gx;
                int wrappedY = ((int) head.getY() + gy) % gy;
                head.setLocation(wrappedX, wrappedY);
            }
        }

        body.add(prevHead);
        bodyDirs.add(direction);

        if (!dead && obstacle.getObstacles().contains(head)) {
            head.setLocation(prevHead);
            dead = true;
        }

        if (body.size() > 4 && hitSelf() && !body.get(0).equals(head)) {
            dead = true;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getSegmentDirection(int index) {
        if (bodyDirs.isEmpty()) {
            return direction;
        }
        if (index < 0) {
            return bodyDirs.get(0);
        }
        if (index >= bodyDirs.size()) {
            return bodyDirs.get(bodyDirs.size() - 1);
        }
        return bodyDirs.get(index);
    }

    public int getTailDir() {
        return getSegmentDirection(0).getAngle();
    }

    public int getBodyDir(int bodyCount) {
        if (bodyCount < 0 || bodyCount >= bodyDirs.size()) {
            return 0;
        }
        return bodyDirs.get(bodyCount).getBodyAngle();
    }

    public boolean ateCoin(Coin coin) {
        boolean ate = head.equals(coin.getPos());
        if (!ate) {
            body.remove(0);
            if (!bodyDirs.isEmpty()) {
                bodyDirs.remove(0);
            }
        } else if (growthRate > 1) {
            // Adds extra segment(s) when growthRate > 1 (e.g. Hardcore mode)
            Direction tailDirection = getSegmentDirection(0);
            for (int i = 1; i < growthRate; i++) {
                body.add(0, new Point(body.get(0)));
                bodyDirs.add(0, tailDirection);
            }
        }
        return ate;
    }

    /**
     * Resets the snake's head to the center spawn block while stacking all existing
     * body segments onto the initial tail block so they uncoil as the snake moves.
     *
     * @param x grid width
     * @param y grid height
     */
    public void resetPosition(int x, int y) {
        int preservedSize = Math.max(1, body.size());
        head.setLocation(x / 2, y / 2);
        body.clear();
        bodyDirs.clear();
        for (int i = 0; i < preservedSize; i++) {
            body.add(new Point(x / 2 + 1, y / 2));
            bodyDirs.add(Direction.LEFT);
        }
        dead = false;
        direction = Direction.LEFT;
    }

    public int getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(int growthRate) {
        this.growthRate = growthRate;
    }

    private boolean borderControl() {
        return head.getX() < 0 || head.getY() < 0 || head.getX() >= gx || head.getY() >= gy;
    }

    public boolean hitSelf() {
        return body.contains(head);
    }

    public ArrayList<Point> getBody() {
        return new ArrayList<>(body);
    }

    public Point getHead() {
        return new Point(head);
    }

    public boolean getDead() {
        return dead;
    }

    public void ignoreBorders(boolean ign) {
        ignoreBorders = ign;
    }

    public int getSnakeDir() {
        return direction.getAngle();
    }
}
