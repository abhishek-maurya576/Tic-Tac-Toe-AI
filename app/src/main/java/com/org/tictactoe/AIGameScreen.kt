package com.org.tictactoe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AIGameScreen(feedbackManager: FeedbackManager, onBackClick: () -> Unit) {
    val gameState = remember { GameState() }
    val aiGame = remember { AIGame(gameState) }
    var winner by remember { mutableStateOf<Char?>(null) }
    var isUserTurn by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_new),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "AI Tic Tac Toe",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            // Empty box for symmetry
            Box(modifier = Modifier.size(48.dp))
        }

        // Game Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = when {
                    winner != null -> if (winner == 'X') "You Win!" else "AI Wins!"
                    gameState.isDraw -> "It's a Draw!"
                    isUserTurn -> "Your Turn (X)"
                    else -> "AI's Turn (O)"
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Game Board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (j in 0..2) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable(
                                        enabled = winner == null && !gameState.isDraw && isUserTurn
                                    ) {
                                        if (gameState.getBoardValue(i, j) == ' ') {
                                            gameState.makeMove(i, j, feedbackManager)
                                            winner = gameState.winner
                                            isUserTurn = false
                                            
                                            // AI's turn
                                            if (winner == null && !gameState.isDraw) {
                                                // Add a small delay before AI moves
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    delay(500) // 500ms delay
                                                    val aiMove = aiGame.bestMove()
                                                    gameState.makeMove(aiMove / 3, aiMove % 3, feedbackManager)
                                                    winner = gameState.winner
                                                    isUserTurn = true
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                val cellValue = gameState.getBoardValue(i, j)
                                if (cellValue != ' ') {
                                    Text(
                                        text = cellValue.toString(),
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (cellValue) {
                                            'X' -> Color(0xFF2196F3) // Blue for User
                                            'O' -> Color(0xFFF44336) // Red for AI
                                            else -> Color.Transparent
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    gameState.reset()
                    winner = null
                    isUserTurn = true
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text("Reset Game", fontSize = 18.sp)
            }
        }
    }
}
