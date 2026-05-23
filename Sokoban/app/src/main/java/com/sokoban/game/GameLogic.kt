package com.sokoban.game

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
    val playerRow: Int,
    val playerCol: Int,
    val boxRow: Int = -1,
    val boxCol: Int = -1,
    val boxWasOnTarget: Boolean = false,
    val playerWasOnTarget: Boolean = false
)

class GameLogic {

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

    // 存储原始目标点位置
    private val targetPositions = mutableListOf<Pair<Int, Int>>()

    fun loadLevel(level: Level) {
        rows = level.map.size
        cols = level.map[0].size
        grid = Array(rows) { row -> IntArray(cols) { col -> level.map[row][col] } }
        playerRow = level.playerStartRow
        playerCol = level.playerStartCol
        moves = 0
        isCompleted = false
        moveHistory.clear()
        targetPositions.clear()

        // 记录所有目标点位置
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (grid[r][c] == CellType.TARGET.ordinal ||
                    grid[r][c] == CellType.BOX_ON_TARGET.ordinal ||
                    grid[r][c] == CellType.PLAYER_ON_TARGET.ordinal) {
                    targetPositions.add(Pair(r, c))
                }
            }
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

        // 检查边界
        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) return false

        val targetCell = grid[newRow][newCol]

        // 墙壁，不能移动
        if (targetCell == CellType.WALL.ordinal || targetCell == CellType.EMPTY.ordinal) return false

        // 空地或目标点，直接移动
        if (targetCell == CellType.FLOOR.ordinal || targetCell == CellType.TARGET.ordinal) {
            recordMove(newRow, newCol)
            updatePlayerPosition(newRow, newCol, isTarget(newRow, newCol))
            moves++
            return true
        }

        // 箱子，尝试推动
        if (targetCell == CellType.BOX.ordinal || targetCell == CellType.BOX_ON_TARGET.ordinal) {
            val boxNewRow = newRow + dr
            val boxNewCol = newCol + dc

            // 检查箱子目标位置边界
            if (boxNewRow < 0 || boxNewRow >= rows || boxNewCol < 0 || boxNewCol >= cols) return false

            val boxTargetCell = grid[boxNewRow][boxNewCol]

            // 箱子目标位置必须是地板或目标点
            if (boxTargetCell == CellType.FLOOR.ordinal || boxTargetCell == CellType.TARGET.ordinal) {
                val boxWasOnTarget = targetCell == CellType.BOX_ON_TARGET.ordinal
                recordMove(newRow, newCol, newRow, newCol, boxWasOnTarget, isTarget(playerRow, playerCol))

                // 移动箱子
                grid[newRow][newCol] = if (boxWasOnTarget) CellType.TARGET.ordinal else CellType.FLOOR.ordinal
                grid[boxNewRow][boxNewCol] = if (isTarget(boxNewRow, boxNewCol))
                    CellType.BOX_ON_TARGET.ordinal else CellType.BOX.ordinal

                // 移动玩家
                updatePlayerPosition(newRow, newCol, isTarget(newRow, newCol))
                moves++

                // 检查是否完成
                checkCompletion()
                return true
            }
        }

        return false
    }

    fun undo(): Boolean {
        if (moveHistory.isEmpty()) return false

        val record = moveHistory.removeAt(moveHistory.size - 1)

        // 恢复玩家位置
        val wasOnTarget = record.playerWasOnTarget
        grid[playerRow][playerCol] = if (isTarget(playerRow, playerCol))
            CellType.TARGET.ordinal else CellType.FLOOR.ordinal

        playerRow = record.playerRow
        playerCol = record.playerCol
        grid[playerRow][playerCol] = if (wasOnTarget)
            CellType.PLAYER_ON_TARGET.ordinal else CellType.PLAYER.ordinal

        // 恢复箱子位置（如果有）
        if (record.boxRow >= 0) {
            // 先清除箱子当前位置
            val currentBoxRow = record.boxRow + (record.boxRow - record.playerRow)
            val currentBoxCol = record.boxCol + (record.boxCol - record.playerCol)

            // 计算箱子移动前的位置
            val oldBoxRow = record.boxRow
            val oldBoxCol = record.boxCol

            // 箱子当前位置 = 推动后的位置
            val (dr, dc) = when {
                record.boxRow < record.playerRow -> Pair(-1, 0)
                record.boxRow > record.playerRow -> Pair(1, 0)
                record.boxCol < record.playerCol -> Pair(0, -1)
                else -> Pair(0, 1)
            }

            val pushedBoxRow = record.boxRow + dr
            val pushedBoxCol = record.boxCol + dc

            // 清除推动后的位置
            grid[pushedBoxRow][pushedBoxCol] = if (isTarget(pushedBoxRow, pushedBoxCol))
                CellType.TARGET.ordinal else CellType.FLOOR.ordinal

            // 恢复箱子到原位置
            grid[oldBoxRow][oldBoxCol] = if (record.boxWasOnTarget)
                CellType.BOX_ON_TARGET.ordinal else CellType.BOX.ordinal
        }

        moves--
        isCompleted = false
        return true
    }

    private fun recordMove(
        playerRow: Int,
        playerCol: Int,
        boxRow: Int = -1,
        boxCol: Int = -1,
        boxWasOnTarget: Boolean = false,
        playerWasOnTarget: Boolean = false
    ) {
        moveHistory.add(MoveRecord(playerRow, playerCol, boxRow, boxCol, boxWasOnTarget, playerWasOnTarget))
    }

    private fun updatePlayerPosition(newRow: Int, newCol: Int, onTarget: Boolean) {
        // 清除旧位置
        grid[playerRow][playerCol] = if (isTarget(playerRow, playerCol))
            CellType.TARGET.ordinal else CellType.FLOOR.ordinal

        playerRow = newRow
        playerCol = newCol
        grid[playerRow][playerCol] = if (onTarget)
            CellType.PLAYER_ON_TARGET.ordinal else CellType.PLAYER.ordinal
    }

    private fun checkCompletion() {
        // 检查所有目标点是否都有箱子
        for ((r, c) in targetPositions) {
            if (grid[r][c] != CellType.BOX_ON_TARGET.ordinal) {
                return
            }
        }
        isCompleted = true
    }
}
