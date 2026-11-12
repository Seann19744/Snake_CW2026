# Documentation
## Debug
- Game.java has a duplicate line of code at line 32, it will be removed (this.powerUp = powerUp;)
- GameController.java rainbow coin never spawns because ran == 1, so changed ran == 2. This is because both gold and rainbow coin is 1 so it runs gold as it is first in the if/else.
- GameController.java fix reverse direction by changing (KeyCode.DOWN) to (KeyCode.UP), so that snake cannot move in the opposite direction using movement key