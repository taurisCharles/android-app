package com.example.snakegame.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snakegame.model.Direction
import com.example.snakegame.model.GameState
import com.example.snakegame.model.GameStatus
import com.example.snakegame.model.Position
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    var state by mutableStateOf(GameState())
        private set

    private var gameJob: Job? = null

    fun startGame() {
        val snake = listOf(Position(10, 10), Position(9, 10), Position(8, 10))
        state = GameState(
            snake = snake,
            status = GameStatus.RUNNING,
            food = randomFoodPosition(snake)
        )
        startGameLoop()
    }

    fun resetGame() {
        gameJob?.cancel()
        startGame()
    }

    fun changeDirection(newDirection: Direction) {
        if (!state.direction.isOpposite(newDirection) && state.status == GameStatus.RUNNING) {
            state = state.copy(direction = newDirection)
        }
    }

    private fun startGameLoop() {
        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            while (state.status == GameStatus.RUNNING) {
                delay(150L)
                updateGame()
            }
        }
    }

    private fun updateGame() {
        val head = state.snake.first()
        val newHead = when (state.direction) {
            Direction.UP -> Position(head.x, head.y - 1)
            Direction.DOWN -> Position(head.x, head.y + 1)
            Direction.LEFT -> Position(head.x - 1, head.y)
            Direction.RIGHT -> Position(head.x + 1, head.y)
        }

        // Check wall collision
        if (newHead.x < 0 || newHead.x >= GameState.BOARD_SIZE ||
            newHead.y < 0 || newHead.y >= GameState.BOARD_SIZE
        ) {
            state = state.copy(status = GameStatus.GAME_OVER)
            gameJob?.cancel()
            return
        }

        val ateFood = newHead == state.food
        val bodyToCheck = if (ateFood) state.snake else state.snake.dropLast(1)

        // Moving into the old tail position is valid when the tail advances this tick.
        if (newHead in bodyToCheck) {
            state = state.copy(status = GameStatus.GAME_OVER)
            gameJob?.cancel()
            return
        }

        val newSnake = listOf(newHead) + if (ateFood) state.snake else state.snake.dropLast(1)
        val newFood = if (ateFood) randomFoodPosition(newSnake) else state.food
        val newScore = if (ateFood) state.score + 1 else state.score

        state = state.copy(
            snake = newSnake,
            food = newFood,
            score = newScore
        )
    }

    private fun randomFoodPosition(snake: List<Position>): Position {
        val emptyCells = (0 until GameState.BOARD_SIZE).flatMap { x ->
            (0 until GameState.BOARD_SIZE).map { y -> Position(x, y) }
        }.filter { it !in snake }

        return emptyCells.random()
    }
}
