package com.sokoban.game

/**
 * 推箱子关卡数据
 * 使用 CellType 枚举值编码:
 * 0 = EMPTY (不可达区域)
 * 1 = FLOOR (地板)
 * 2 = WALL (墙壁)
 * 3 = TARGET (目标点)
 * 4 = BOX (箱子)
 * 5 = BOX_ON_TARGET (箱子在目标点上)
 * 6 = PLAYER (玩家)
 * 7 = PLAYER_ON_TARGET (玩家在目标点上)
 *
 * 每个关卡均经过推演验证可解
 */

data class Level(
    val map: Array<IntArray>,
    val playerStartRow: Int,
    val playerStartCol: Int
)

object LevelData {

    val levels = listOf(
        // ===== 第一阶段: 基础入门 (1-3) =====

        // 关卡 1 - 初识推箱 (1 box, ~5 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 1, 6, 1, 2),
                intArrayOf(2, 1, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 3, 2),
                intArrayOf(2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 1,
            playerStartCol = 3
        ),

        // 关卡 2 - 双箱协力 (2 boxes, ~10 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 3, 1, 3, 2),
                intArrayOf(2, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 4, 1, 4, 2),
                intArrayOf(2, 1, 1, 6, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 4,
            playerStartCol = 3
        ),

        // 关卡 3 - 三箱横排 (3 boxes, ~12 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 3, 3, 3, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 4, 4, 4, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 5,
            playerStartCol = 3
        ),

        // ===== 第二阶段: 简单提升 (4-6) =====

        // 关卡 4 - L形迷局 (2 boxes, ~13 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 3, 1, 3, 2),
                intArrayOf(2, 1, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 2, 2),
                intArrayOf(2, 1, 4, 1, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 5,
            playerStartCol = 3
        ),

        // 关卡 5 - 中央壁垒 (2 boxes, ~15 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 3, 1, 3, 1, 2),
                intArrayOf(2, 1, 1, 2, 1, 1, 2),
                intArrayOf(2, 1, 1, 4, 1, 1, 2),
                intArrayOf(2, 1, 1, 4, 1, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 6,
            playerStartCol = 3
        ),

        // 关卡 6 - 纵列推进 (3 boxes, ~20 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 3, 1, 1, 2),
                intArrayOf(2, 1, 3, 1, 1, 2),
                intArrayOf(2, 1, 3, 1, 1, 2),
                intArrayOf(2, 1, 4, 1, 1, 2),
                intArrayOf(2, 1, 4, 1, 1, 2),
                intArrayOf(2, 1, 4, 1, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 7,
            playerStartCol = 3
        ),

        // ===== 第三阶段: 中等难度 (7-10) =====

        // 关卡 7 - 十字路口 (2 boxes, 2 targets, ~14 moves)
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 2, 2, 2, 0, 0),
                intArrayOf(2, 2, 2, 3, 2, 2, 2),
                intArrayOf(2, 1, 1, 1, 1, 3, 2),
                intArrayOf(2, 1, 4, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 5,
            playerStartCol = 3
        ),

        // 关卡 8 - 回廊迷踪 (2 boxes, 2 targets, ~19 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 2, 1, 3, 3, 2, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 4, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 2, 1, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 6,
            playerStartCol = 3
        ),

        // 关卡 9 - 双室穿梭 (2 boxes, ~21 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 3, 1, 1, 1, 3, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 2, 1, 2, 1, 2, 2),
                intArrayOf(2, 1, 4, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 6,
            playerStartCol = 3
        ),

        // 关卡 10 - 三箱阶梯 (3 boxes, ~18 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 3, 1, 3, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 4, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 4, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 3, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 7,
            playerStartCol = 3
        ),

        // ===== 第四阶段: 进阶挑战 (11-15) =====

        // 关卡 11 - 双箱直推 (2 boxes, 2 targets, ~12 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 3, 1, 3, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 4, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 6,
            playerStartCol = 3
        ),

        // 关卡 12 - 三箱斜排 (3 boxes, ~22 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 3, 1, 3, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 4, 1, 1, 2),
                intArrayOf(2, 1, 1, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 4, 2),
                intArrayOf(2, 1, 1, 6, 1, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 3, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 6,
            playerStartCol = 3
        ),

        // 关卡 13 - 交叉密室 (2 boxes, 2 targets, ~17 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 2, 1, 1, 1, 2, 2),
                intArrayOf(2, 1, 3, 1, 3, 1, 2),
                intArrayOf(2, 1, 1, 2, 1, 1, 2),
                intArrayOf(2, 1, 4, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 6,
            playerStartCol = 3
        ),

        // 关卡 14 - 双室迷阵 (2 boxes, 2 targets, ~20 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 3, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 2, 1, 4, 1, 2, 2),
                intArrayOf(2, 1, 1, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 3, 2),
                intArrayOf(2, 1, 1, 6, 1, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 7,
            playerStartCol = 3
        ),

        // 关卡 15 - 终极迷宫 (2 boxes, ~20 moves)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 2, 1, 1, 1, 1, 2, 2),
                intArrayOf(2, 1, 3, 1, 1, 3, 1, 2),
                intArrayOf(2, 1, 1, 2, 2, 1, 1, 2),
                intArrayOf(2, 2, 1, 1, 1, 1, 2, 2),
                intArrayOf(2, 1, 1, 4, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 6, 1, 1, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 7,
            playerStartCol = 3
        ),

        // 关卡 16 - 测试9x9 (2 boxes, 2 targets)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 3, 1, 1, 1, 3, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 1, 2, 1, 1, 1, 2),
                intArrayOf(2, 1, 1, 1, 1, 1, 1, 1, 2),
                intArrayOf(2, 1, 4, 1, 1, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 6, 1, 1, 1, 2),
                intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 7,
            playerStartCol = 4
        )
    )
}
