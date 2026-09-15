package com.example.snakegame.model

data class Position(val x: Int, val y: Int)

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
    val food: Position = Position(5, 5),
    val direction: Direction = Direction.RIGHT,
    val score: Int = 0,
    val status: GameStatus = GameStatus.IDLE
) {
    companion object {
        const val BOARD_SIZE = 20
    }
}
