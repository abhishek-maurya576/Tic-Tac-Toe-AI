package com.org.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.org.tictactoe.ui.theme.TicTacToeTheme

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    var feedbackManager: FeedbackManager? = null  

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feedbackManager = FeedbackManager(this)
        
        setContent {
            TicTacToeTheme {
                var currentScreen by remember { mutableStateOf("menu") }
                
                when (currentScreen) {
                    "menu" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Tic Tac Toe",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(bottom = 32.dp)
                            )
                            
                            Button(
                                onClick = { currentScreen = "game" },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(vertical = 8.dp)
                            ) {
                                Text("Start Game", fontSize = 20.sp)
                            }
                            
                            Button(
                                onClick = { currentScreen = "ai" },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(vertical = 8.dp)
                            ) {
                                Text("Start with AI", fontSize = 20.sp)
                            }
                        }
                    }
                    "game" -> {
                        feedbackManager?.let { feedback ->
                            TicTacToeGame(
                                gameState = GameState(),
                                feedbackManager = feedback,
                                onBackClick = { currentScreen = "menu" }
                            )
                        }
                    }
                    "ai" -> {
                        feedbackManager?.let { feedback ->
                            AIGameScreen(
                                feedbackManager = feedback,
                                onBackClick = { currentScreen = "menu" }
                            )
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
fun StartScreen(onStartClick: () -> Unit, onStartAIClick: () -> Unit) {
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
        Button(
            onClick = {
                onStartAIClick()
            },
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp)
                .height(50.dp)
        ) {
            Text("Start with AI", style = MaterialTheme.typography.titleLarge)
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Game board
        for (i in 0..2) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (j in 0..2) {
                    Button(
                        onClick = {
                            gameState.makeMove(i, j, feedbackManager)
                        },
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = gameState.getBoardValue(i, j).toString(),
                            fontSize = 32.sp
                        )
                    }
                }
            }
        }

        // Game status
        gameState.winner?.let { winner ->
            if (winner != ' ') {
                Text(
                    text = "Winner: $winner",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (gameState.isDraw) {
            Text(
                text = "Game Draw!",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Current player
        if (gameState.winner == null && !gameState.isDraw) {
            Text(
                text = "Current Player: ${gameState.currentPlayer}",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Reset and Back buttons
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onBackClick) {
                Text("Back to Menu", fontSize = 18.sp)
            }
            Button(onClick = { gameState.reset() }) {
                Text("Reset Game", fontSize = 18.sp)
            }
        }
    }
}