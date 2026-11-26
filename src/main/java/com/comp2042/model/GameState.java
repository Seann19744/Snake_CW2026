package com.comp2042.model;

/**
 * Represents the lifecycle phases of a Snake game session.
 */
public enum GameState {
    READY,
    RUNNING,
    PAUSED,
    LEVEL_CLEARED,
    WON,
    DEAD
}
