package com.sokoban.game

data class ValidationResult(val isValid: Boolean, val errorMessage: String?)

object LevelValidator {

    fun validate(grid: Array<IntArray>): ValidationResult {
        var playerCount = 0
        var boxCount = 0
        var targetCount = 0
        var floorCount = 0

        for (row in grid) {
            for (cell in row) {
                when (cell) {
                    CellType.PLAYER.ordinal -> playerCount++
                    CellType.PLAYER_ON_TARGET.ordinal -> {
                        playerCount++
                        targetCount++
                    }
                    CellType.BOX.ordinal -> boxCount++
                    CellType.BOX_ON_TARGET.ordinal -> {
                        boxCount++
                        targetCount++
                    }
                    CellType.TARGET.ordinal -> targetCount++
                    CellType.FLOOR.ordinal -> floorCount++
                }
            }
        }

        if (playerCount != 1) {
            return ValidationResult(false, "必须有且仅有一个玩家")
        }
        if (boxCount != targetCount) {
            return ValidationResult(false, "箱子数量($boxCount)必须等于目标点数量($targetCount)")
        }
        if (boxCount == 0) {
            return ValidationResult(false, "至少需要一个箱子和一个目标点")
        }
        if (floorCount == 0) {
            return ValidationResult(false, "关卡不能为空")
        }

        return ValidationResult(true, null)
    }
}
