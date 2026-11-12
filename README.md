# COMP2042 Snake Coursework

## Github
https://github.com/Seann19744/Snake_CW2026

## Compilation Instructions

## Implemented and Working Properly

## Implemented but not Working Properly

## Features not Implemented

## New Java Classes
- Launcher.java : Let's Intellij start JavaFX app

## Modified Java Classes
- Game.java has a duplicate line of code at line 32, was removed (this.powerUp = powerUp;)
- GameController.java rainbow coin never spawns because ran == 1, so changed ran == 2. This is because both gold and rainbow coin is 1 so it runs gold as it is first in the if/else.
- GameController.java fix reverse direction by changing (KeyCode.DOWN) to (KeyCode.UP), so that snake cannot move in the opposite direction using movement key
- Obstacle.java obstacles does not spawn on the leftmost side of the game grid because the x coordinate has a + 1, removed it so that obstacles can spawn everywhere on the game except on the snake and coin

## Unexpected Problem