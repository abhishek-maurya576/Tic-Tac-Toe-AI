package com.org.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GameState {
    private val _board = Array(3) { Array(3) { mutableStateOf("") } }
    var currentPlayer by mutableStateOf("X")
        private set
    var winner by mutableStateOf<String?>(null)
        private set
    var isDraw by mutableStateOf(false)
        private set

    fun makeMove(row: Int, col: Int, feedbackManager: FeedbackManager): Boolean {
        if (winner != null || isDraw || _board[row][col].value.isNotEmpty()) {
            return false
        }

        _board[row][col].value = currentPlayer
        feedbackManager.playMoveEffect()

        when {
            checkWin(row, col) -> {
                winner = currentPlayer
                feedbackManager.playWinEffect()
            }
            checkDraw() -> {
                isDraw = true
                feedbackManager.playDrawEffect()
            }
            else -> {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
            }
        }
        return true
    }

    private fun checkWin(row: Int, col: Int): Boolean {
        // Check row
        if (_board[row].all { it.value == currentPlayer }) return true

        // Check column
        if (_board.all { it[col].value == currentPlayer }) return true

        // Check diagonals
        if (row == col && _board.indices.all { _board[it][it].value == currentPlayer }) return true
        if (row + col == 2 && _board.indices.all { _board[it][2 - it].value == currentPlayer }) return true

        return false
    }

    private fun checkDraw(): Boolean {
        return _board.all { row -> row.all { it.value.isNotEmpty() } }
    }

    fun reset() {
        for (i in _board.indices) {
            for (j in _board[i].indices) {
                _board[i][j].value = ""
            }
        }
        currentPlayer = "X"
        winner = null
        isDraw = false
    }

    fun getBoardValue(row: Int, col: Int): String {
        return _board[row][col].value
    }
}
