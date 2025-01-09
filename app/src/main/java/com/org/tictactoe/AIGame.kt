package com.org.tictactoe

import android.util.Log
import kotlin.math.max
import kotlin.math.min

class AIGame(private val gameState: GameState) {
    private val player = 'X' // User
    private val ai = 'O' // AI
    private val corners = listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2))
    private val edges = listOf(Pair(0, 1), Pair(1, 0), Pair(1, 2), Pair(2, 1))

    fun bestMove(): Int {
        val board = gameState.board
        val moveCount = board.sumOf { row -> row.count { it != ' ' } }

        // Opening moves strategy
        if (moveCount == 0) {
            // First move: Take a corner
            return 0 // Top-left corner
        }

        if (moveCount == 1) {
            // If player took center, take corner
            if (board[1][1] == player) {
                return 0 // Top-left corner
            }
            // If player took corner or edge, take center
            return 4 // Center
        }

        // Use minimax for subsequent moves
        var bestScore = Int.MIN_VALUE
        var bestMove = -1
        
        // First check for winning moves
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    val tempBoard = Array(3) { row -> Array(3) { col ->
                        if (row == i && col == j) ai else board[row][col]
                    }}
                    if (checkWinner(tempBoard) == ai) {
                        return i * 3 + j // Take winning move immediately
                    }
                }
            }
        }

        // Then check for blocking moves
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    val tempBoard = Array(3) { row -> Array(3) { col ->
                        if (row == i && col == j) player else board[row][col]
                    }}
                    if (checkWinner(tempBoard) == player) {
                        return i * 3 + j // Block player's winning move
                    }
                }
            }
        }

        // If no immediate winning or blocking moves, use minimax with alpha-beta pruning
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    val tempBoard = Array(3) { row -> Array(3) { col ->
                        if (row == i && col == j) ai else board[row][col]
                    }}
                    val score = minimax(tempBoard, 0, false, Int.MIN_VALUE, Int.MAX_VALUE)
                    if (score > bestScore) {
                        bestScore = score
                        bestMove = i * 3 + j
                    }
                }
            }
        }

        // If no optimal move found, prioritize strategic positions
        if (bestMove == -1) {
            // Try to take center if available
            if (board[1][1] == ' ') return 4

            // Try to take corners
            for ((i, j) in corners) {
                if (board[i][j] == ' ') return i * 3 + j
            }

            // Take any edge
            for ((i, j) in edges) {
                if (board[i][j] == ' ') return i * 3 + j
            }
        }

        return bestMove
    }

    private fun minimax(board: Array<Array<Char>>, depth: Int, isMaximizing: Boolean, alpha: Int, beta: Int): Int {
        val winner = checkWinner(board)
        if (winner != ' ') {
            return when (winner) {
                ai -> 10 - depth // Prefer winning sooner
                player -> depth - 10 // Prefer losing later
                else -> 0
            }
        }

        if (isGameOver(board)) return 0

        if (isMaximizing) {
            var bestScore = Int.MIN_VALUE
            var alpha = alpha
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        val tempBoard = Array(3) { row -> Array(3) { col ->
                            if (row == i && col == j) ai else board[row][col]
                        }}
                        val score = minimax(tempBoard, depth + 1, false, alpha, beta)
                        bestScore = maxOf(score, bestScore)
                        alpha = maxOf(alpha, bestScore)
                        if (beta <= alpha) break
                    }
                }
            }
            return bestScore
        } else {
            var bestScore = Int.MAX_VALUE
            var beta = beta
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        val tempBoard = Array(3) { row -> Array(3) { col ->
                            if (row == i && col == j) player else board[row][col]
                        }}
                        val score = minimax(tempBoard, depth + 1, true, alpha, beta)
                        bestScore = minOf(score, bestScore)
                        beta = minOf(beta, bestScore)
                        if (beta <= alpha) break
                    }
                }
            }
            return bestScore
        }
    }

    private fun isGameOver(board: Array<Array<Char>>): Boolean {
        return board.all { row -> row.all { it != ' ' } }
    }

    private fun checkWinner(board: Array<Array<Char>>): Char {
        // Check rows
        for (i in 0..2) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0]
            }
        }
        
        // Check columns
        for (i in 0..2) {
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return board[0][i]
            }
        }
        
        // Check diagonals
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0]
        }
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2]
        }
        
        return ' '
    }
}
