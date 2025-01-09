package com.org.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GameState {
    private val _board = Array(3) { Array(3) { mutableStateOf(' ') } }
    val board: Array<Array<Char>>
        get() = Array(3) { i -> Array(3) { j -> _board[i][j].value } }

    var currentPlayer by mutableStateOf('X')
        private set
    var winner by mutableStateOf<Char?>(null)
        private set
    var isDraw by mutableStateOf(false)
        private set

    fun getBoardValue(row: Int, col: Int): Char = _board[row][col].value

    fun makeMove(row: Int, col: Int, feedbackManager: FeedbackManager): Boolean {
        if (winner != null || isDraw || _board[row][col].value != ' ') {
            return false
        }

        _board[row][col].value = currentPlayer
        feedbackManager.playMoveEffect()

        when {
            checkWinner() != ' ' -> {
                winner = currentPlayer
                feedbackManager.playWinEffect()
            }
            checkDraw() -> {
                isDraw = true
                feedbackManager.playDrawEffect()
            }
            else -> {
                currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
            }
        }
        return true
    }

    fun checkWinner(): Char {
        // Check rows, columns, and diagonals for a winner
        for (i in 0..2) {
            if (_board[i][0].value == _board[i][1].value && 
                _board[i][1].value == _board[i][2].value && 
                _board[i][0].value != ' ') {
                return _board[i][0].value
            }
            if (_board[0][i].value == _board[1][i].value && 
                _board[1][i].value == _board[2][i].value && 
                _board[0][i].value != ' ') {
                return _board[0][i].value
            }
        }
        if (_board[0][0].value == _board[1][1].value && 
            _board[1][1].value == _board[2][2].value && 
            _board[0][0].value != ' ') {
            return _board[0][0].value
        }
        if (_board[0][2].value == _board[1][1].value && 
            _board[1][1].value == _board[2][0].value && 
            _board[0][2].value != ' ') {
            return _board[0][2].value
        }
        return ' '
    }

    private fun checkDraw(): Boolean {
        return _board.all { row -> row.all { it.value != ' ' } }
    }

    fun reset() {
        for (i in 0..2) {
            for (j in 0..2) {
                _board[i][j].value = ' '
            }
        }
        currentPlayer = 'X'
        winner = null
        isDraw = false
    }
}
