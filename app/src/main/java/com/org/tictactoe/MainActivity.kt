package com.org.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.org.tictactoe.ui.theme.TicTacToeTheme

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    var feedbackManager: FeedbackManager? = null
    private val gameState = GameState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feedbackManager = FeedbackManager(this)
        
        setContent {
            TicTacToeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showGame by remember { mutableStateOf(false) }
                    
                    AnimatedContent(
                        targetState = showGame,
                        transitionSpec = {
                            fadeIn() with fadeOut()
                        }
                    ) { show ->
                        if (show) {
                            feedbackManager?.let { feedback ->
                                TicTacToeGame(
                                    gameState = gameState,
                                    feedbackManager = feedback,
                                    onBackClick = { showGame = false }
                                )
                            }
                        } else {
                            StartScreen(onStartClick = { showGame = true })
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        feedbackManager?.release()
        feedbackManager = null
    }
}

@Composable
fun StartScreen(onStartClick: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tic Tac Toe",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = {
                (context as? MainActivity)?.feedbackManager?.playStartButtonEffect()
                onStartClick()
            },
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp)
                .height(50.dp)
        ) {
            Text("Start Game", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun TicTacToeGame(
    gameState: GameState,
    feedbackManager: FeedbackManager,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar with Back Button
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
            
            // Game Status
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = when {
                        gameState.winner != null -> "Player ${gameState.winner} Wins! "
                        gameState.isDraw -> "It's a Draw! "
                        else -> "Player ${gameState.currentPlayer}'s Turn"
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
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
                for (row in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0..2) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable(
                                        enabled = gameState.winner == null && !gameState.isDraw
                                    ) {
                                        gameState.makeMove(row, col, feedbackManager)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                val cellValue = gameState.getBoardValue(row, col)
                                if (cellValue.isNotEmpty()) {
                                    Text(
                                        text = cellValue,
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (cellValue) {
                                            "X" -> Color(0xFF2196F3) // Blue
                                            "O" -> Color(0xFFF44336) // Red
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

        // Reset Button
        Button(
            onClick = { gameState.reset() },
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(0.5f)
        ) {
            Text("Reset Game", fontSize = 18.sp)
        }
    }
}