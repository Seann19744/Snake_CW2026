# COMP2042 Snake Coursework

## Github
https://github.com/Seann19744/Snake_CW2026

## Compilation Instructions

## Implemented and Working Properly
- GameController.java coinSoundPath was an unused String. Replaced it with an AudioClip loaded from the classpath and played when a coin is eaten

## Implemented but not Working Properly

## Features not Implemented

## New Java Classes
- Launcher.java : Let's Intellij start JavaFX app

## Modified Java Classes
- Game.java has a duplicate line of code at line 32, was removed (this.powerUp = powerUp;)
- GameController.java rainbow coin never spawns because ran == 1, so changed ran == 2. This is because both gold and rainbow coin is 1 so it runs gold as it is first in the if/else.
- GameController.java fix reverse direction by changing (KeyCode.DOWN) to (KeyCode.UP), so that snake cannot move in the opposite direction using movement key
- Obstacle.java obstacles does not spawn on the leftmost side of the game grid because the x coordinate has a + 1, removed it so that obstacles can spawn everywhere on the game except on the snake and coin
- GameController.java fix fullscreen key binds by adding EQUAL and ADD so that game can go to fullscreen, also add SUBSTRACT to minimize screen. Both using numpad + and - works same with the keyboard shift + and -
- GameView.java fix snake being enlarged in fullscreen, removed extra (* fullScale) at both snake head and body for both width and height
- GameController.java removed legacyScoreMultiplier and calculateBonusScore(), does not affect gameplay
- GameController.java removed unusedObstacleCount, does not affect gameplay
- GameController.java removed import javafx for media, multimedia and file
- GameController.java added an AudioClip to play coin sound that was never used before in resources

### Package Reorganisation
- com.comp2042.controller: GameController, MainMenuController
- com.comp2042.model: BlueCoin, Coin, Game, GoldCoin, Obstacle, RainbowCoin, SilverCoin, Snake
- com.comp2042.view: GameView, ViewManager
- Reason for reorganisation was because everything was under com.comp2042 which made the files harder to search and now easier to identify if a specific area has a problem

### Renamed identifiers
- snek is renamed to snake in all files
- ViewManager.initalizeMainMenu() is changed to initializeMainMenu() instead because of spelling error
- GameController.valkey() is changed to isValidTurn() for easier understanding

## Unexpected Problem
- After moving classes into packages, IntelliJ's saved Run configuration changed its main class to `com.comp2042.model.comp2042.Launcher`, so the game failed with `ClassNotFoundException`. Fixed by correcting the Main class to `com.comp2042.Launcher` in Edit Configurations