package com.example.snakegame.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snakegame.model.Direction
import com.example.snakegame.model.FoodType
import com.example.snakegame.model.GameState
import com.example.snakegame.model.GameStatus
import com.example.snakegame.model.Position
import com.example.snakegame.viewmodel.GameViewModel
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val state = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E1B12))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Snakerito",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFFD166),
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HudChip("Score", state.score.toString())
            HudChip("Best", state.bestScore.toString())
            HudChip("Snack", state.foodType.label)
        }

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
                OverlayText("Tap to Start\nDrag to steer")
            }

            if (state.status == GameStatus.GAME_OVER) {
                OverlayText("Snakerito Down!\nScore: ${state.score}\nBest: ${state.bestScore}\n\nTap to Restart")
            }
        }

        Text(
            text = "Hold and drag on the arena to steer",
            color = Color(0xFFFFF3D1),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
        )
    }
}

@Composable
private fun HudChip(label: String, value: String) {
    Surface(
        color = Color(0xFF4D2C1D),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.dp,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = Color(0xFFFFF3D1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color(0xFFFFD166), fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun GameBoard(
    state: GameState,
    onSwipe: (Direction) -> Unit,
    onTap: () -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { onTap() }
            }
            .pointerInput(Unit) {
                var dragOrigin = Offset.Zero
                detectDragGestures(
                    onDragStart = { offset -> dragOrigin = offset },
                    onDrag = { change, dragAmount ->
                        val dragFromStart = change.position - dragOrigin
                        val dx = if (abs(dragFromStart.x) > 18f) dragFromStart.x else dragAmount.x
                        val dy = if (abs(dragFromStart.y) > 18f) dragFromStart.y else dragAmount.y

                        if (abs(dx) < 3f && abs(dy) < 3f) {
                            return@detectDragGestures
                        }

                        change.consume()

                        if (abs(dx) > abs(dy)) {
                            onSwipe(if (dx > 0) Direction.RIGHT else Direction.LEFT)
                        } else {
                            onSwipe(if (dy > 0) Direction.DOWN else Direction.UP)
                        }
                    }
                )
            }
    ) {
        val cellSize = size.width / GameState.BOARD_SIZE
        drawFiestaBoard(cellSize)
        drawFood(state.food, state.foodType, cellSize, state.tick)
        drawSnake(state, cellSize, 1f)
    }
}

private fun DrawScope.drawFiestaBoard(cellSize: Float) {
    drawRect(Color(0xFF7A3E24))
    for (x in 0 until GameState.BOARD_SIZE) {
        for (y in 0 until GameState.BOARD_SIZE) {
            if ((x + y) % 2 == 0) {
                drawRect(Color(0xFF8F4A2B).copy(alpha = 0.55f), Offset(x * cellSize, y * cellSize), Size(cellSize, cellSize))
            }
        }
    }

    val lineColor = Color(0xFFFFD166).copy(alpha = 0.16f)
    for (i in 1 until GameState.BOARD_SIZE) {
        val pos = i * cellSize
        drawLine(lineColor, Offset(pos, 0f), Offset(pos, size.height), strokeWidth = 0.7f)
        drawLine(lineColor, Offset(0f, pos), Offset(size.width, pos), strokeWidth = 0.7f)
    }

    drawRoundRect(
        color = Color(0xFFFFF3D1).copy(alpha = 0.22f),
        topLeft = Offset(cellSize * 0.5f, cellSize * 0.5f),
        size = Size(size.width - cellSize, size.height - cellSize),
        cornerRadius = CornerRadius(cellSize * 0.4f),
        style = Stroke(width = cellSize * 0.12f)
    )
}

private fun DrawScope.drawFood(position: Position, type: FoodType, cellSize: Float, tick: Int) {
    val pulse = 1f + sin((tick % 12) / 12f * 2f * PI).toFloat() * 0.06f
    val center = Offset(position.x * cellSize + cellSize / 2f, position.y * cellSize + cellSize / 2f)
    val radius = cellSize * 0.48f * pulse

    when (type) {
        FoodType.TACO -> {
            drawArc(Color(0xFFFFC857), 180f, 180f, true, Offset(center.x - radius, center.y - radius), Size(radius * 2, radius * 2))
            drawCircle(Color(0xFF4CAF50), radius * 0.16f, center + Offset(-radius * 0.35f, -radius * 0.05f))
            drawCircle(Color(0xFFE53935), radius * 0.14f, center + Offset(radius * 0.15f, -radius * 0.1f))
        }
        FoodType.BURRITO -> {
            drawRoundRect(Color(0xFFF5D39B), Offset(center.x - radius * 0.85f, center.y - radius * 0.42f), Size(radius * 1.7f, radius * 0.84f), CornerRadius(radius * 0.35f))
            drawCircle(Color(0xFF6D4C41), radius * 0.16f, center + Offset(-radius * 0.18f, 0f))
            drawCircle(Color(0xFF43A047), radius * 0.12f, center + Offset(radius * 0.28f, radius * 0.08f))
        }
        FoodType.CHILI -> {
            drawOval(Color(0xFFE53935), Offset(center.x - radius * 0.55f, center.y - radius * 0.25f), Size(radius * 1.1f, radius * 0.72f))
            drawLine(Color(0xFF43A047), center + Offset(radius * 0.3f, -radius * 0.28f), center + Offset(radius * 0.58f, -radius * 0.58f), strokeWidth = radius * 0.16f)
        }
        FoodType.SALSA -> {
            drawCircle(Color(0xFFE0C9A6), radius, center)
            drawCircle(Color(0xFFD62828), radius * 0.72f, center)
            drawCircle(Color(0xFF43A047), radius * 0.12f, center + Offset(radius * 0.25f, -radius * 0.16f))
        }
        FoodType.LIME -> {
            drawCircle(Color(0xFFB7E03A), radius, center)
            drawCircle(Color(0xFFE7FF8F), radius * 0.62f, center)
            drawLine(Color(0xFF6DAA22), center + Offset(0f, -radius * 0.55f), center + Offset(0f, radius * 0.55f), strokeWidth = radius * 0.08f)
        }
        FoodType.NACHOS -> {
            val chip = Path().apply {
                moveTo(center.x, center.y - radius)
                lineTo(center.x - radius * 0.9f, center.y + radius * 0.65f)
                lineTo(center.x + radius * 0.9f, center.y + radius * 0.65f)
                close()
            }
            drawPath(chip, Color(0xFFFFC857))
            drawCircle(Color(0xFFE53935), radius * 0.13f, center + Offset(radius * 0.2f, radius * 0.15f))
        }
    }
}

private fun DrawScope.drawSnake(state: GameState, cellSize: Float, progress: Float) {
    state.snake.asReversed().forEachIndexed { reverseIndex, position ->
        val index = state.snake.lastIndex - reverseIndex
        val previous = state.previousSnake.getOrNull(index) ?: position
        val visual = interpolate(previous, position, progress)
        val center = Offset(visual.x * cellSize + cellSize / 2f, visual.y * cellSize + cellSize / 2f)
        val radius = if (index == 0) cellSize * 0.45f else cellSize * 0.39f
        val color = if (index == 0) Color(0xFF3BB273) else if (index % 2 == 0) Color(0xFF2FA866) else Color(0xFF64C987)

        drawCircle(Color.Black.copy(alpha = 0.18f), radius, center + Offset(cellSize * 0.06f, cellSize * 0.08f))
        drawCircle(color, radius, center)
        drawCircle(Color(0xFFFFF3D1).copy(alpha = 0.22f), radius * 0.18f, center + Offset(-radius * 0.25f, -radius * 0.28f))

        if (index == 0) {
            drawSombrero(center, cellSize)
            drawFace(center, cellSize, state.direction)
        }
    }
}

private fun DrawScope.drawSombrero(center: Offset, cellSize: Float) {
    drawOval(Color(0xFFD69A2D), Offset(center.x - cellSize * 0.6f, center.y - cellSize * 0.76f), Size(cellSize * 1.2f, cellSize * 0.34f))
    drawOval(Color(0xFFFFD166), Offset(center.x - cellSize * 0.33f, center.y - cellSize * 0.94f), Size(cellSize * 0.66f, cellSize * 0.46f))
    drawLine(Color(0xFFE63946), Offset(center.x - cellSize * 0.42f, center.y - cellSize * 0.62f), Offset(center.x + cellSize * 0.42f, center.y - cellSize * 0.62f), strokeWidth = cellSize * 0.08f)
}

private fun DrawScope.drawFace(center: Offset, cellSize: Float, direction: Direction) {
    val eyeOffset = when (direction) {
        Direction.LEFT -> Offset(-cellSize * 0.14f, -cellSize * 0.08f)
        Direction.RIGHT -> Offset(cellSize * 0.14f, -cellSize * 0.08f)
        Direction.UP -> Offset(0f, -cellSize * 0.16f)
        Direction.DOWN -> Offset(0f, cellSize * 0.02f)
    }
    drawCircle(Color.White, cellSize * 0.07f, center + eyeOffset + Offset(-cellSize * 0.11f, 0f))
    drawCircle(Color.White, cellSize * 0.07f, center + eyeOffset + Offset(cellSize * 0.11f, 0f))
    drawCircle(Color(0xFF1F130F), cellSize * 0.035f, center + eyeOffset + Offset(-cellSize * 0.11f, 0f))
    drawCircle(Color(0xFF1F130F), cellSize * 0.035f, center + eyeOffset + Offset(cellSize * 0.11f, 0f))
    drawOval(Color(0xFF1F130F), Offset(center.x - cellSize * 0.22f, center.y + cellSize * 0.16f), Size(cellSize * 0.2f, cellSize * 0.1f))
    drawOval(Color(0xFF1F130F), Offset(center.x + cellSize * 0.02f, center.y + cellSize * 0.16f), Size(cellSize * 0.2f, cellSize * 0.1f))
}

private fun interpolate(from: Position, to: Position, progress: Float): Offset {
    val eased = progress * progress * (3f - 2f * progress)
    return Offset(
        x = from.x + (to.x - from.x) * eased,
        y = from.y + (to.y - from.y) * eased
    )
}

@Composable
private fun OverlayText(text: String) {
    Surface(
        color = Color(0xFF1F130F).copy(alpha = 0.86f),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 6.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(24.dp),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFF3D1),
            textAlign = TextAlign.Center
        )
    }
}
