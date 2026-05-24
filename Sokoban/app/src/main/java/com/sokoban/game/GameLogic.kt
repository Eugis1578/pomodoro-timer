package com.sokoban.game

import android.util.Log

/**
 * 推箱子游戏核心逻辑
 */

enum class CellType {
    EMPTY,      // 空地（不可达区域）
    FLOOR,      // 地板
    WALL,       // 墙壁
    TARGET,     // 目标点
    BOX,        // 箱子
    BOX_ON_TARGET, // 箱子在目标点上
    PLAYER,     // 玩家
    PLAYER_ON_TARGET // 玩家在目标点上
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

data class MoveRecord(
    val playerPrevRow: Int,
    val playerPrevCol: Int,
    val playerWasOnTarget: Boolean,
    val boxPrevRow: Int = -1,
    val boxPrevCol: Int = -1,
    val boxWasOnTarget: Boolean = false,
    val boxDestWasTarget: Boolean = false,
    val pushDirection: Direction? = null
)

class GameLogic {

    companion object {
        private const val TAG = "SokobanGame"
    }

    var rows: Int = 0
        private set
    var cols: Int = 0
        private set
    var playerRow: Int = 0
        private set
    var playerCol: Int = 0
        private set
    var moves: Int = 0
        private set
    var isCompleted: Boolean = false
        private set

    private lateinit var grid: Array<IntArray>
    private val moveHistory = mutableListOf<MoveRecord>()

    // 存储原始目标点位置（从初始地图扫描，不随游戏变化）
    private val targetPositions = mutableListOf<Pair<Int, Int>>()

    fun loadLevel(level: Level) {
        rows = level.map.size
        cols = level.map[0].size

        // 深拷贝地图数据
        grid = Array(rows) { r -> IntArray(cols) { c -> level.map[r][c] } }

        playerRow = level.playerStartRow
        playerCol = level.playerStartCol
        moves = 0
        isCompleted = false
        moveHistory.clear()
        targetPositions.clear()

        // 从原始地图数据扫描目标点（value == 3 是 TARGET）
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val v = level.map[r][c]
                if (v == CellType.TARGET.ordinal ||
                    v == CellType.BOX_ON_TARGET.ordinal ||
                    v == CellType.PLAYER_ON_TARGET.ordinal) {
                    targetPositions.add(Pair(r, c))
                }
            }
        }

        // 统计箱子数量
        var boxCount = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val v = grid[r][c]
                if (v == CellType.BOX.ordinal || v == CellType.BOX_ON_TARGET.ordinal) {
                    boxCount++
                }
            }
        }

        Log.d(TAG, "关卡加载: ${rows}x${cols}, 箱子=$boxCount, 目标=${targetPositions.size}")
        if (boxCount != targetPositions.size) {
            Log.e(TAG, "错误: 箱子数($boxCount) != 目标数(${targetPositions.size})!")
        }
        if (targetPositions.isEmpty()) {
            Log.e(TAG, "错误: 没有目标点，任何移动都会通关！")
        }
    }

    fun getCell(row: Int, col: Int): Int {
        return grid[row][col]
    }

    fun isTarget(row: Int, col: Int): Boolean {
        return targetPositions.contains(Pair(row, col))
    }

    fun move(direction: Direction): Boolean {
        if (isCompleted) return false

        val (dr, dc) = when (direction) {
            Direction.UP -> Pair(-1, 0)
            Direction.DOWN -> Pair(1, 0)
            Direction.LEFT -> Pair(0, -1)
            Direction.RIGHT -> Pair(0, 1)
        }

        val newRow = playerRow + dr
        val newCol = playerCol + dc

        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) return false

        val targetCell = grid[newRow][newCol]

        // 墙壁或空地，不能移动
        if (targetCell == CellType.WALL.ordinal || targetCell == CellType.EMPTY.ordinal) return false

        // 地板或目标点，直接移动
        if (targetCell == CellType.FLOOR.ordinal || targetCell == CellType.TARGET.ordinal) {
            val record = MoveRecord(
                playerPrevRow = playerRow,
                playerPrevCol = playerCol,
                playerWasOnTarget = isTarget(playerRow, playerCol)
            )
            moveHistory.add(record)
            updatePlayerPosition(newRow, newCol)
            moves++
            return true
        }

        // 箱子，尝试推动
        if (targetCell == CellType.BOX.ordinal || targetCell == CellType.BOX_ON_TARGET.ordinal) {
            val boxNewRow = newRow + dr
            val boxNewCol = newCol + dc

            if (boxNewRow < 0 || boxNewRow >= rows || boxNewCol < 0 || boxNewCol >= cols) return false

            val boxTargetCell = grid[boxNewRow][boxNewCol]

            if (boxTargetCell == CellType.FLOOR.ordinal || boxTargetCell == CellType.TARGET.ordinal) {
                val record = MoveRecord(
                    playerPrevRow = playerRow,
                    playerPrevCol = playerCol,
                    playerWasOnTarget = isTarget(playerRow, playerCol),
                    boxPrevRow = newRow,
                    boxPrevCol = newCol,
                    boxWasOnTarget = (targetCell == CellType.BOX_ON_TARGET.ordinal),
                    boxDestWasTarget = (boxTargetCell == CellType.TARGET.ordinal),
                    pushDirection = direction
                )
                moveHistory.add(record)

                // 移动箱子：旧位置恢复为地板或目标点
                grid[newRow][newCol] = if (isTarget(newRow, newCol))
                    CellType.TARGET.ordinal else CellType.FLOOR.ordinal
                // 新位置设置箱子
                grid[boxNewRow][boxNewCol] = if (isTarget(boxNewRow, boxNewCol))
                    CellType.BOX_ON_TARGET.ordinal else CellType.BOX.ordinal

                // 移动玩家
                updatePlayerPosition(newRow, newCol)
                moves++

                checkCompletion()
                return true
            }
        }

        return false
    }

    fun undo(): Boolean {
        if (moveHistory.isEmpty()) return false

        val record = moveHistory.removeAt(moveHistory.size - 1)

        // 1. 恢复箱子（如果有推箱操作）
        if (record.boxPrevRow >= 0 && record.pushDirection != null) {
            val (dr, dc) = when (record.pushDirection) {
                Direction.UP -> Pair(-1, 0)
                Direction.DOWN -> Pair(1, 0)
                Direction.LEFT -> Pair(0, -1)
                Direction.RIGHT -> Pair(0, 1)
            }

            // 箱子推动后的位置
            val boxPushedRow = record.boxPrevRow + dr
            val boxPushedCol = record.boxPrevCol + dc

            // 清除箱子当前位置（恢复为箱子推来之前的地面状态）
            grid[boxPushedRow][boxPushedCol] = if (record.boxDestWasTarget)
                CellType.TARGET.ordinal else CellType.FLOOR.ordinal

            // 恢复箱子到原位置
            grid[record.boxPrevRow][record.boxPrevCol] = if (record.boxWasOnTarget)
                CellType.BOX_ON_TARGET.ordinal else CellType.BOX.ordinal
        }

        // 2. 清除玩家当前位置
        grid[playerRow][playerCol] = if (isTarget(playerRow, playerCol))
            CellType.TARGET.ordinal else CellType.FLOOR.ordinal

        // 3. 恢复玩家到原位置
        playerRow = record.playerPrevRow
        playerCol = record.playerPrevCol
        grid[playerRow][playerCol] = if (record.playerWasOnTarget)
            CellType.PLAYER_ON_TARGET.ordinal else CellType.PLAYER.ordinal

        moves = (moves - 1).coerceAtLeast(0)
        isCompleted = false

        Log.d(TAG, "撤销: 玩家→($playerRow,$playerCol), 步数=$moves")
        return true
    }

    private fun updatePlayerPosition(newRow: Int, newCol: Int) {
        // 清除旧位置
        grid[playerRow][playerCol] = if (isTarget(playerRow, playerCol))
            CellType.TARGET.ordinal else CellType.FLOOR.ordinal

        playerRow = newRow
        playerCol = newCol
        grid[playerRow][playerCol] = if (isTarget(newRow, newCol))
            CellType.PLAYER_ON_TARGET.ordinal else CellType.PLAYER.ordinal
    }

    private fun checkCompletion() {
        if (targetPositions.isEmpty()) {
            // 没有目标点，不应该通关
            Log.w(TAG, "警告: 没有目标点，跳过通关检查")
            return
        }
        for ((r, c) in targetPositions) {
            if (grid[r][c] != CellType.BOX_ON_TARGET.ordinal) {
                return
            }
        }
        Log.d(TAG, "通关! 所有 ${targetPositions.size} 个目标点都有箱子")
        isCompleted = true
    }
}
