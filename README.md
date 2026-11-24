# COMP2042 Snake Coursework

## GitHub Repository
https://github.com/Seann19744/Snake_CW2026

---

## Compilation and Execution Instructions

### Prerequisites
- **Java Development Kit (JDK)**: Version 21 or higher (Project configured for Java 21 LTS in `pom.xml`).
- **Build Tool**: Apache Maven 3.8+ (or use the included Maven wrapper `mvnw` / `mvnw.cmd`).
- **JavaFX SDK**: Version 25.0.1 (automatically managed via Maven dependencies in `pom.xml`).
- **IDE** (Optional): IntelliJ IDEA (recommended), Eclipse, or VS Code with Java extensions.

### How to Run the Application

#### Option 1: Running via IntelliJ IDEA (Recommended)
1. Open IntelliJ IDEA, select **File -> Open**, and choose the project root directory (`Snake_CW2026`).
2. Allow IntelliJ to import and resolve dependencies from `pom.xml`.
3. Verify that the Project SDK is set to JDK 21+ (**File -> Project Structure -> Project -> SDK**).
4. Navigate to `src/main/java/com/comp2042/Launcher.java`.
5. Right-click `Launcher.java` and select **Run 'Launcher.main()'**.
   > *Note*: Running via `Launcher.java` instead of `Main.java` bypasses JavaFX 11+ runtime component checks, preventing `Runtime components are missing` errors when running directly in an IDE without custom VM options.

#### Option 2: Running via Maven Command Line
Open a terminal in the root directory of the project and execute:
```bash
# Compile project
mvn clean compile

# Run JavaFX application
mvn javafx:run
```
*(On Windows PowerShell using the wrapper: `.\mvnw.cmd compile; .\mvnw.cmd javafx:run`)*

## Implemented and Working Properly

The following features, enhancements, and bug fixes have been implemented, tested, and verified to be working properly:

1. **Game Modes and Playable Levels System (UI, Logic, & Map Layouts)**:
   - Replaced basic difficulty levels with 3 fully-featured **Game Modes** selectable via a dropdown on the main menu (`MainMenuController.java` & `MainMenu.fxml`):
     - **Classic Mode**: Standard speed (8.5), fatal outer borders, standard growth (+1 segment per coin), coins to clear levels: 8 -> 10 -> 12 (30 coins total to win).
     - **Quick Mode**: Standard speed (8.5), border wrap-around enabled (`noBorder = true`), 2 fewer coins per level: 6 -> 8 -> 10 (24 coins total to win).
     - **Hardcore Mode**: High speed (13.0), fatal borders, +2 coins per level: 10 -> 12 -> 14 (36 coins total to win), and **double snake growth (+2 segments per coin eaten)**.
   - **Automatic In-Game Level Progression**:
     - Replaced the legacy 398-coin endless requirement with structured level advancement based on the exact count of coins eaten, decoupling victory from random score luck.
     - **Level 1 (The Open Field)**: Clean 20x20 open grid.
     - **Level 2 (Four Pillars)**: 4 static 2x2 stone pillars spawned in quadrants to restrict turning space.
     - **Level 3 (The Labyrinth)**: Interior corridor barriers dividing the field into interconnected quadrants. Completing Level 3 triggers the victory screen (**"YOU WON!"**).
   - **Live In-Game HUD**: Displays live mode, level, coin quota, and score: e.g., `[Classic Mode] Level 2/3 | Coins: 4/10 | Score: 45`.

2. **Audio Effects Implementation**:
   - Replaced an unused string path with a functional JavaFX `AudioClip` in `GameController.java` to load `/resources/views/res/coinSound.wav`.
   - Plays an audio sound effect every time the snake consumes a coin.

3. **Bug Fix: Rainbow Coin Spawning Logic**:
   - Resolved a bug in `GameController.newCoin()` where `ran == 1` was duplicated in the if-else branch (`if (ran == 1) ... else if (ran == 1) ...`). Because the first branch created `GoldCoin`, the second branch was unreachable dead code, making `RainbowCoin` impossible to spawn.
   - Updated condition to `else if (ran == 2)`, successfully enabling rainbow coin spawns and speed bonuses.

4. **Bug Fix: Reverse Direction Suicide Prevention (`isValidTurn`)**:
   - Resolved a flaw in the directional input validation method (`isValidTurn`, formerly `valKey`). Previously, pressing `DOWN` while traveling `UP` was allowed due to a self-comparison bug (`!this.key.equals(KeyCode.DOWN)`), resulting in immediate 180-degree self-collision.
   - Updated logic so `DOWN` is rejected when moving `UP`, `UP` is rejected when moving `DOWN`, `LEFT` is rejected when moving `RIGHT`, and `RIGHT` is rejected when moving `LEFT`.

5. **Bug Fix: Leftmost Obstacle Column Generation**:
   - Resolved an indexing issue in `Obstacle.newObstacles()` where `rnd.nextInt(x - 1) + 1` was used to generate x-coordinates. This mathematical offset restricted obstacles to `[1, x-1]`, preventing obstacles from ever spawning on the leftmost grid column (`x = 0`).
   - Fixed to `rnd.nextInt(x)`, allowing obstacles to spawn across the full grid width while still ensuring they never overlap the snake or coin.

6. **Bug Fix: Fullscreen Toggle Keybindings**:
   - Enhanced fullscreen toggle controls in `GameController.java` to support keyboards without dedicated numpads.
   - In addition to `KeyCode.PLUS`, key codes `KeyCode.EQUALS` and `KeyCode.ADD` now trigger fullscreen.
   - In addition to `KeyCode.MINUS`, `KeyCode.SUBTRACT` now exits fullscreen / minimizes the window.

7. **Bug Fix: Fullscreen Visual Scaling for Snake**:
   - Fixed a rendering scaling bug in `GameView.java` where the snake's head and body segments were scaled by `fullScale` twice: `(game.getMultiplier() * fullScale) * fullScale`.
   - This caused the snake to render disproportionately large and overflow grid tiles in fullscreen mode. Removing the extra `* fullScale` restored correct 1:1 proportional scaling with the game grid.

8. **Bug Fix: ESC Key Game Loop Termination and State Cleanup**:
   - Fixed an issue where pressing `ESCAPE` returned the user to the main menu but allowed the JavaFX `Timeline` animation loop to keep running in the background. Added `animation.stop()` when `ESCAPE` is pressed.
   - Added explicit resets for `key` and `lastKey` to `KeyCode.LEFT` in the constructor and when exiting to the main menu, preventing previous game inputs from carrying over to a new session.

9. **In-Game Controls & Gameplay Mechanics**:
   - **Movement**: Arrow keys (`UP`, `DOWN`, `LEFT`, `RIGHT`) start the game and steer the snake.
   - **Pause Game**: Press `P` to pause the animation loop.
   - **Reset Game**: Press `TAB` to reset the snake, coin, obstacles, score, and canvas without quitting.
   - **Power-Up Coins**:
     - Regular Coin (Red): +5 points, extends snake body.
     - Gold Coin: +15 points.
     - Rainbow Coin: +5 points, temporary speed increase.
     - Blue Coin: +5 points, temporarily enables passing through borders without dying.
     - Silver Coin: +5 points, spawns random obstacle blocks.
   - **Return to Menu**: Press `ESCAPE` to halt the game and return to the main menu.

10. **Architectural MVC Refactoring**:
    - Reorganized all classes from the flat default package `com.comp2042` into distinct architectural layers: `model`, `view`, and `controller`, improving code cohesion and modularity.

11. **Code Cleanup & Dead Code Removal**:
    - Removed unused variables and dead methods in `GameController.java`, including `legacyScoreMultiplier`, `calculateBonusScore()`, and `unusedObstacleCount`.
    - Removed redundant imports (`javafx.scene.media.Media`, `MediaPlayer`, `java.io.File`).
    - Removed duplicate assignment in `Game.java` (`this.powerUp = powerUp;`).

12. **Code Readability & Naming Conventions**:
    - Renamed informal identifier `snek` to standard `snake` across all classes and methods.
    - Renamed cryptic method `valKey()` to descriptive boolean predicate `isValidTurn()`.
    - Corrected typographical error `initalizeMainMenu()` to `initializeMainMenu()` in `ViewManager.java`.

13. **Design Pattern: GoF Factory Pattern (`CoinGenerator`)**:
    - Extracted coin creation out of `GameController` into a dedicated `CoinGenerator` class (`com.comp2042.model.CoinGenerator`).
    - Encapsulates probability distributions and board condition checks (e.g., ensuring `SilverCoin` only spawns when the board has no obstacles).
    - Enables dependency injection of `Random` for deterministic unit testing.

14. **SOLID Refactoring: Polymorphic Power-Up Effects (Open-Closed Principle)**:
    - Replaced the hardcoded class-type checking ladder (`if (coin.getClass().equals(RainbowCoin.class)) ...`) in `GameController.powerCoin()` with a polymorphic `applyEffect(Game, Snake, Obstacle, Timeline)` method across the `Coin` hierarchy.
    - Each coin subclass encapsulates its own effect (`RainbowCoin` adds speed bonus, `BlueCoin` enables border-crossing, `SilverCoin` spawns obstacles, `GoldCoin` adds 15 points).
    - New coin types can now be introduced without modifying existing controller logic, fully conforming to the Open-Closed Principle (OCP) and Single Responsibility Principle (SRP).

---

## Implemented but not Working Properly

- **None**: All features, mechanics, and bug fixes listed above have been tested and are working properly as expected.

---

## Features not Implemented

The following features were considered during design and development but were not implemented in this release:

1. **Persistent High Scores / Leaderboard**:
   - The game currently displays the live session score on screen (`updateScoreLabel()`), but scores are not saved to a local file (e.g., JSON/CSV) or database between application restarts.
2. **Custom Grid Dimensions Configuration**:
   - Although the `Game` and `Obstacle` models support variable grid dimensions (`X_VALUE`, `Y_VALUE`), the UI currently launches with the standard 20x20 grid layout and does not expose a slider or text input in the main menu for custom board sizing.
3. **Sound Effects for Collisions and Game Over**:
   - Audio feedback is currently implemented for coin collection (`coinSound.wav`). Additional sound effects for wall collisions, self-collisions, and victory/defeat screens were not included in this iteration.
4. **Dynamic / Moving Obstacles**:
   - Obstacles generated by silver coins remain static in their spawned locations throughout the round. Dynamic moving obstacles were left for future enhancements.

---

## New Java Classes

### 1. `com.comp2042.Launcher`
- **Location**: `src/main/java/com/comp2042/Launcher.java`
- **Purpose**: Application launcher entry point.
- **Justification**: JavaFX 11+ enforces strict runtime checks when the entry point class directly inherits from `javafx.application.Application`. If launched from an IDE or a shaded fat JAR without explicit VM options pointing to the JavaFX module path, the JVM throws `Error: JavaFX runtime components are missing, and are required to run this application`. `Launcher` contains a standard `main(String[] args)` method that does not extend `Application` and delegates directly to `Main.main(args)`. This enables IntelliJ IDEA, Eclipse, and packaged fat JARs to execute seamlessly across all environments.

### 2. `com.comp2042.model.Difficulty`
- **Location**: `src/main/java/com/comp2042/model/Difficulty.java`
- **Purpose**: Type-safe enumeration defining game difficulty levels (`EASY`, `NORMAL`, `HARD`).
- **Justification**: The original codebase utilized loose `char` values (`'E'`, `'N'`, `'H'`) passed into constructors without input validation, creating risk of undefined states or a frozen snake if an unexpected character was passed. `Difficulty` encapsulates gameplay parameters:
  - `speed` (double): Animation rate (8.5 for `EASY` and `NORMAL`, 13.0 for `HARD`).
  - `noBorder` (boolean): `true` for `EASY` (enabling wall wrap-around), `false` for `NORMAL` and `HARD`.
  - Provides a clean data model that integrates directly with JavaFX UI controls such as `ComboBox<Difficulty>`.

### 3. `com.comp2042.model.CoinGenerator`
- **Location**: `src/main/java/com/comp2042/model/CoinGenerator.java`
- **Purpose**: Implements the GoF Factory Pattern for generating `Coin` instances and subclasses.
- **Justification**: Decouples coin generation from controller classes. Encapsulates probability-based power-up selection and board condition validation. Supports constructor dependency injection with `java.util.Random`, enabling deterministic unit testing.

### 4. `com.comp2042.model.GameMode`
- **Location**: `src/main/java/com/comp2042/model/GameMode.java`
- **Purpose**: Type-safe enumeration defining game modes (`QUICK`, `CLASSIC`, `HARDCORE`).
- **Justification**: Encapsulates speed, wall wrap-around behavior, coin quotas required to clear each level, and snake body growth rate per coin eaten.

---

## Modified Java Classes

### 1. `com.comp2042.Main`
- Updated package imports to reflect the new package architecture (`com.comp2042.view.ViewManager`).
- Fixed method invocation typo: updated `viewManager.initalizeMainMenu()` to `viewManager.initializeMainMenu()`.

### 2. `com.comp2042.controller.MainMenuController`
- Moved to package `com.comp2042.controller`.
- Added FXML binding `@FXML private ComboBox<Difficulty> difficultyBox`.
- Added `initialize()` method to populate `difficultyBox` with all `Difficulty.values()` and set `Difficulty.NORMAL` as the default selection.
- Refactored `handleStart()` and `launchSnake(Stage, Difficulty)` to retrieve the player's chosen difficulty from the dropdown and pass it to the `Game` constructor.
- Replaced hardcoded char constant `DIFFICULTY = 'N'` with dynamic selection.

### 3. `src/main/resources/resources/views/MainMenu.fxml`
- Updated `fx:controller` attribute to match the new package path: `com.comp2042.controller.MainMenuController`.
- Added a `<ComboBox fx:id="difficultyBox" .../>` component positioned below the Start button to provide an intuitive user interface for difficulty selection.

### 4. `com.comp2042.controller.GameController`
- Moved to package `com.comp2042.controller` and updated model and view imports.
- **Factory Pattern Integration**: Injected `CoinGenerator` to handle coin spawning in `newCoin()` and `reset()`, removing hardcoded random coin instantiation from the controller.
- **Polymorphic Power-Up Handling (OCP)**: Refactored `powerCoin()` to delegate directly to `coin.applyEffect(game, snake, obstacle, animation)`, completely removing fragile `getClass().equals(...)` conditionals and conforming to the Open-Closed Principle.
- **Rainbow Coin Bug Fix**: Corrected condition in `newCoin()` from duplicate `ran == 1` to `ran == 2`, making `RainbowCoin` spawnable during gameplay.
- **Direction Reversal Bug Fix**: In `isValidTurn()` (formerly `valKey()`), corrected the check for `KeyCode.DOWN` from `!this.key.equals(KeyCode.DOWN)` to `!this.key.equals(KeyCode.UP)`, preventing instant 180° suicide turns across all four directions.
- **Audio Integration**: Replaced unused String variable `coinSoundPath` with a loaded `AudioClip` (`/resources/views/res/coinSound.wav`) played whenever `snake.ateCoin(coin)` is true.
- **Fullscreen Keybindings**: Added `KeyCode.EQUALS` and `KeyCode.ADD` to fullscreen triggers, and `KeyCode.SUBTRACT` to minimize triggers.
- **ESC Key Handling**: Added `animation.stop()` in `KeyCode.ESCAPE` to halt background loop execution when returning to the main menu.
- **Direction State Initialization**: Explicitly set `key = KeyCode.LEFT` and `lastKey = KeyCode.LEFT` in the constructor and `reset()` method so direction states do not bleed into subsequent games.
- **Dead Code Removal**: Removed unused fields `legacyScoreMultiplier`, `unusedObstacleCount`, unused method `calculateBonusScore()`, and unused imports (`javafx.scene.media.Media`, `MediaPlayer`, `java.io.File`).

### 5. `com.comp2042.model.Game`
- Moved to package `com.comp2042.model`.
- Removed duplicate assignment statement `this.powerUp = powerUp;` in the constructor.
- Refactored constructor signature from `Game(int x, int y, boolean powerUp, char difficulty)` to `Game(int x, int y, boolean powerUp, Difficulty difficulty)`, extracting speed and border properties safely from the enum.

### 6. `com.comp2042.model.Obstacle`
- Moved to package `com.comp2042.model`.
- Fixed coordinate generation in `newObstacles()`: changed `rnd.nextInt(x - 1) + 1` to `rnd.nextInt(x)`, allowing obstacles to spawn across all columns including column 0.
- Added comprehensive Javadoc documentation explaining obstacle placement logic and collision prevention with the snake and coin.

### 7. `com.comp2042.view.GameView`
- Moved to package `com.comp2042.view`.
- Fixed fullscreen distortion: removed duplicate `* fullScale` in `drawHead()` and `drawBody()`, eliminating the bug where snake body parts were scaled quadratically.
- Renamed parameter `snek` to `snake` in methods (`turnDir`, `gameOver`, `drawGrid`, `drawHead`, `drawBody`).

### 8. `com.comp2042.view.ViewManager`
- Moved to package `com.comp2042.view`.
- Corrected method name spelling: `initalizeMainMenu()` -> `initializeMainMenu()`.
- Updated imports and stage initialization.

### 9. `com.comp2042.model.Snake`
- Moved to package `com.comp2042.model`.
- Standardized variable naming throughout the class (`snek` -> `snake`).

### 10. `com.comp2042.model.Coin` and Subclasses (`GoldCoin`, `SilverCoin`, `BlueCoin`, `RainbowCoin`)
- Moved to package `com.comp2042.model`.
- Updated constructors and method parameters from `Snake snek` to `Snake snake`.
- Implemented polymorphic `applyEffect(Game, Snake, Obstacle, Timeline)` across all coin types, encapsulating power-up behaviors within the coin models rather than external controller conditionals.

---

### Package Reorganisation (MVC Architecture)

The project structure was refactored from a single flat package (`com.comp2042`) into a Model-View-Controller (MVC) architectural design pattern:

```
src/main/java/com/comp2042/
├── Launcher.java                       [Application Entry Point]
├── Main.java                           [JavaFX Application Lifecycle]
├── controller/                         [Controller Layer]
│   ├── GameController.java             (Input handling, game loop, audio trigger)
│   └── MainMenuController.java         (Main menu UI interactions, difficulty binding)
├── model/                              [Model Layer]
│   ├── BlueCoin.java                   (Border pass power-up coin)
│   ├── Coin.java                       (Base coin entity)
│   ├── CoinGenerator.java              (GoF Factory Pattern for coin instantiation)
│   ├── Difficulty.java                 (Legacy difficulty enumeration)
│   ├── Game.java                       (Game state, score, speed, grid boundaries)
│   ├── GameMode.java                   (Game modes: Quick, Classic, Hardcore)
│   ├── GoldCoin.java                   (High-value bonus coin)
│   ├── Obstacle.java                   (Obstacle entity and placement algorithms)
│   ├── RainbowCoin.java                (Speed boost coin)
│   ├── SilverCoin.java                 (Obstacle-spawning coin)
│   └── Snake.java                      (Snake segments, movement, collision detection)
└── view/                               [View Layer]
    ├── GameView.java                   (Canvas rendering, head/tail rotations, UI labels)
    └── ViewManager.java                (JavaFX stage management and scene transitions)
```

**Architectural Rationale**:
- **Separation of Concerns**: Model classes have zero dependencies on JavaFX UI controls or input listeners, making them easier to maintain and unit test.
- **Maintainability and Extensibility**: Adding new coin types, UI screens, or input mechanisms now requires changes only in their dedicated package without risking regressions elsewhere.
- **Improved Code Navigation**: Eliminates the cognitive overhead of searching through a cluttered root package.

---

### Renamed Identifiers and Code Quality Improvements

Adhering to clean code principles and Java naming conventions:
- **`snek` -> `snake`**: Replaced all colloquialisms with descriptive, professional terminology across variable names, parameter lists, and method signatures in all classes.
- **`valKey()` -> `isValidTurn()`**: Renamed this method in `GameController.java` to clearly express its purpose as a boolean predicate checking whether a direction change is legally permitted.
- **`initalizeMainMenu()` -> `initializeMainMenu()`**: Corrected spelling error in `ViewManager.java` and updated all callers (`Main.java`).
- **Standardized Constants**: Maintained UPPER_SNAKE_CASE for class constants (`X_VALUE`, `Y_VALUE`, `POWER_UP`).

---

## Unexpected Problems Encountered and Solutions

1. **IntelliJ IDEA Run Configuration Classpath Issue (`ClassNotFoundException`)**:
   - **Problem**: Following the package restructuring into `com.comp2042.controller`, `com.comp2042.model`, and `com.comp2042.view`, IntelliJ IDEA automatically updated its cached run configuration to `com.comp2042.model.comp2042.Launcher`. Attempting to start the application resulted in `java.lang.ClassNotFoundException: com.comp2042.model.comp2042.Launcher`.
   - **Solution**: Manually edited the Run/Debug Configuration in IntelliJ IDEA, ensuring the Main class correctly pointed to `com.comp2042.Launcher` in the project root module.

2. **Quadratic Fullscreen Scaling Distortion**:
   - **Problem**: When switching to fullscreen mode using the fullscreen key, the snake head and body segments rendered at an enormous scale, overflowing beyond their grid tiles and distorting the game visual layout, while obstacles and coins rendered at standard fullscreen dimensions.
   - **Solution**: Code inspection in `GameView.drawHead()` and `GameView.drawBody()` revealed that each segment's draw dimensions were calculated as `(game.getMultiplier() * fullScale) * fullScale`. Because `gridCanvas` was already scaled by `fullScale`, multiplying by `fullScale` a second time produced quadratic enlargement ($fullScale^2$). Removing the extra `* fullScale` restored proper proportional scaling.

3. **Background Game Loop Persistence on Main Menu Navigation**:
   - **Problem**: Pressing `ESCAPE` during gameplay switched stages and displayed the main menu, but the game loop `Timeline animation` was never stopped. The game continued executing in the background, updating state, consuming CPU cycles, and creating memory leaks.
   - **Solution**: Added `animation.stop()` inside the `KeyCode.ESCAPE` event branch in `GameController.handle()`, ensuring the animation loop is completely halted when the player exits the game session.

4. **Input Direction Carryover Across Sessions**:
   - **Problem**: Static variables `key` and `lastKey` in `GameController` retained their values from the previous game session upon restarting. If a player was moving DOWN when they exited or died, starting a new game could cause the snake to instantly register DOWN instead of the default LEFT, leading to unexpected turns or collisions.
   - **Solution**: Explicitly reset `key = KeyCode.LEFT` and `lastKey = KeyCode.LEFT` in the `GameController` constructor as well as inside the `reset()` method.
