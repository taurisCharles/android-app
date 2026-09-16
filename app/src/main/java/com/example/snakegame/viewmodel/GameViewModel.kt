package com.example.snakegame.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snakegame.model.Direction
import com.example.snakegame.model.FoodType
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
    private var bestScore = 0

    fun startGame() {
        val snake = listOf(Position(10, 10), Position(9, 10), Position(8, 10))
        val food = randomFoodPosition(snake)
        state = GameState(
            snake = snake,
            previousSnake = snake,
            status = GameStatus.RUNNING,
            food = food,
            foodType = randomFoodType(),
            bestScore = bestScore
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
            endGame()
            gameJob?.cancel()
            return
        }

        val ateFood = newHead == state.food
        val bodyToCheck = if (ateFood) state.snake else state.snake.dropLast(1)

        // Moving into the old tail position is valid when the tail advances this tick.
        if (newHead in bodyToCheck) {
            endGame()
            gameJob?.cancel()
            return
        }

        val newSnake = listOf(newHead) + if (ateFood) state.snake else state.snake.dropLast(1)
        val newFood = if (ateFood) randomFoodPosition(newSnake) else state.food
        val newFoodType = if (ateFood) randomFoodType() else state.foodType
        val newScore = if (ateFood) state.score + state.foodType.points else state.score
        bestScore = maxOf(bestScore, newScore)

        state = state.copy(
            previousSnake = state.snake,
            snake = newSnake,
            food = newFood,
            foodType = newFoodType,
            score = newScore,
            bestScore = bestScore,
            tick = state.tick + 1
        )
    }

    private fun endGame() {
        bestScore = maxOf(bestScore, state.score)
        state = state.copy(status = GameStatus.GAME_OVER, bestScore = bestScore)
    }

    private fun randomFoodPosition(snake: List<Position>): Position {
        val emptyCells = (0 until GameState.BOARD_SIZE).flatMap { x ->
            (0 until GameState.BOARD_SIZE).map { y -> Position(x, y) }
        }.filter { it !in snake }

        return emptyCells.random()
    }

    private fun randomFoodType(): FoodType = FoodType.entries.random()
}
