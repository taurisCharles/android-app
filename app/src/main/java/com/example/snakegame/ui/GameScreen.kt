package com.example.snakegame.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snakegame.model.Direction
import com.example.snakegame.model.GameState
import com.example.snakegame.model.GameStatus
import com.example.snakegame.viewmodel.GameViewModel
import kotlin.math.abs

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val state = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Snakerito",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = when (state.status) {
                GameStatus.RUNNING -> "Salsa: ${state.score}"
                GameStatus.GAME_OVER -> "Final salsa: ${state.score}"
                GameStatus.IDLE -> "Swipe or use the controls"
            },
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            GameBoard(
                state = state,
                onSwipe = { direction -> viewModel.changeDirection(direction) },
                onTap = {
                    when (state.status) {
                        GameStatus.IDLE -> viewModel.startGame()
                        GameStatus.GAME_OVER -> viewModel.resetGame()
                        GameStatus.RUNNING -> {}
                    }
                }
            )

            if (state.status == GameStatus.IDLE) {
                OverlayText("Tap to Start\nGrab the salsa")
            }

            if (state.status == GameStatus.GAME_OVER) {
                OverlayText("Snakerito Down!\nScore: ${state.score}\n\nTap to Restart")
            }
        }

        DirectionPad(
            enabled = state.status == GameStatus.RUNNING,
            onDirection = viewModel::changeDirection,
            modifier = Modifier.padding(top = 18.dp)
        )
    }
}

@Composable
private fun GameBoard(
    state: GameState,
    onSwipe: (Direction) -> Unit,
    onTap: () -> Unit
) {
    val boardColor = Color(0xFF5D4037)       // Dark brown table
    val gridLineColor = Color(0xFF6D4C41)    // Subtle wood grain
    val burritoColor = Color(0xFFF5DEB3)     // Warm tortilla
    val burritoHeadColor = Color(0xFFFFE0B2) // Lighter tortilla (front end)
    val fillingColor = Color(0xFF8D6E63)     // Filling peeking through
    val riceColor = Color(0xFFFFF8E1)        // Rice grains
    val cilantroColor = Color(0xFF43A047)    // Cilantro
    val salsaColor = Color(0xFFE53935)       // Salsa (food)
    val salsaBowlColor = Color(0xFFBCAAA4)   // Bowl

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { onTap() }
            }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val (dx, dy) = dragAmount
                    if (abs(dx) > abs(dy)) {
                        onSwipe(if (dx > 0) Direction.RIGHT else Direction.LEFT)
                    } else {
                        onSwipe(if (dy > 0) Direction.DOWN else Direction.UP)
                    }
                }
            }
    ) {
        val cellSize = size.width / GameState.BOARD_SIZE

        // Draw board background (table surface)
        drawRect(color = boardColor)

        // Draw grid lines (subtle)
        for (i in 1 until GameState.BOARD_SIZE) {
            val pos = i * cellSize
            drawLine(gridLineColor, Offset(pos, 0f), Offset(pos, size.height), strokeWidth = 0.5f)
            drawLine(gridLineColor, Offset(0f, pos), Offset(size.width, pos), strokeWidth = 0.5f)
        }

        // Draw salsa bowl (food)
        val foodCenter = Offset(
            state.food.x * cellSize + cellSize / 2,
            state.food.y * cellSize + cellSize / 2
        )
        drawCircle(color = salsaBowlColor, radius = cellSize / 2.2f, center = foodCenter)
        drawCircle(color = salsaColor, radius = cellSize / 3f, center = foodCenter)

        // Draw burrito
        state.snake.forEachIndexed { index, position ->
            val padding = 0.5f
            val topLeft = Offset(position.x * cellSize + padding, position.y * cellSize + padding)
            val segmentSize = Size(cellSize - padding * 2, cellSize - padding * 2)

            // Tortilla wrap
            drawRoundRect(
                color = if (index == 0) burritoHeadColor else burritoColor,
                topLeft = topLeft,
                size = segmentSize,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize / 3, cellSize / 3)
            )

            // Filling stripe down the middle
            if (index > 0) {
                drawRoundRect(
                    color = fillingColor,
                    topLeft = Offset(
                        position.x * cellSize + cellSize * 0.3f,
                        position.y * cellSize + cellSize * 0.3f
                    ),
                    size = Size(cellSize * 0.4f, cellSize * 0.4f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                )
                drawCircle(
                    color = riceColor,
                    radius = cellSize * 0.07f,
                    center = Offset(position.x * cellSize + cellSize * 0.38f, position.y * cellSize + cellSize * 0.38f)
                )
                drawCircle(
                    color = cilantroColor,
                    radius = cellSize * 0.06f,
                    center = Offset(position.x * cellSize + cellSize * 0.62f, position.y * cellSize + cellSize * 0.58f)
                )
            }
        }
    }
}

@Composable
private fun DirectionPad(
    enabled: Boolean,
    onDirection: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DirectionButton("Up", enabled) { onDirection(Direction.UP) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DirectionButton("Left", enabled) { onDirection(Direction.LEFT) }
            Spacer(Modifier.size(56.dp))
            DirectionButton("Right", enabled) { onDirection(Direction.RIGHT) }
        }
        DirectionButton("Down", enabled) { onDirection(Direction.DOWN) }
    }
}

@Composable
private fun DirectionButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(72.dp, 44.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OverlayText(text: String) {
    Surface(
        color = Color.Black.copy(alpha = 0.72f),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 6.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(24.dp),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
