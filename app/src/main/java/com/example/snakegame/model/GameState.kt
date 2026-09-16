package com.example.snakegame.model

data class Position(val x: Int, val y: Int)

enum class FoodType(val label: String, val points: Int) {
    TACO("Taco", 1),
    BURRITO("Burrito", 1),
    CHILI("Chili", 1),
    SALSA("Salsa", 1),
    LIME("Lime", 1),
    NACHOS("Nachos", 1)
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun isOpposite(other: Direction): Boolean = when (this) {
        UP -> other == DOWN
        DOWN -> other == UP
        LEFT -> other == RIGHT
        RIGHT -> other == LEFT
    }
}

enum class GameStatus {
    IDLE, RUNNING, GAME_OVER
}

data class GameState(
    val snake: List<Position> = listOf(Position(10, 10)),
    val previousSnake: List<Position> = snake,
    val food: Position = Position(5, 5),
    val foodType: FoodType = FoodType.TACO,
    val direction: Direction = Direction.RIGHT,
    val score: Int = 0,
    val bestScore: Int = 0,
    val tick: Int = 0,
    val status: GameStatus = GameStatus.IDLE
) {
    companion object {
        const val BOARD_SIZE = 20
    }
}
