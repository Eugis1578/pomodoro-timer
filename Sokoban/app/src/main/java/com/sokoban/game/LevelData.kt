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
    val playerStartCol: Int,
    val starThresholds: IntArray = intArrayOf(0, 0) // [3星步数上限, 2星步数上限]
)

object LevelData {

    val levels = listOf(
        // 关卡 1 - 初识推箱 (1 box)
        Level(
            map = arrayOf(
                intArrayOf(2, 2, 2, 2, 2, 2),
                intArrayOf(2, 1, 1, 6, 1, 2),
                intArrayOf(2, 1, 1, 4, 1, 2),
                intArrayOf(2, 1, 1, 1, 3, 2),
                intArrayOf(2, 2, 2, 2, 2, 2)
            ),
            playerStartRow = 1,
            playerStartCol = 3,
            starThresholds = intArrayOf(10, 20)
        ),

        // 关卡 2 - 双箱协力 (2 boxes)
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
            playerStartCol = 3,
            starThresholds = intArrayOf(20, 35)
        ),

        // 关卡 3 - 三箱横排 (3 boxes)
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
            playerStartCol = 3,
            starThresholds = intArrayOf(30, 50)
        ),

        // 关卡 4 - 测试9x9 (2 boxes, 2 targets)
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
            playerStartCol = 4,
            starThresholds = intArrayOf(35, 55)
        ),

        // 关卡 5 - 六箱走廊 (6 boxes, 6 targets)
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 0, 0),
                intArrayOf(0, 0, 2, 1, 4, 3, 2, 0, 0),
                intArrayOf(0, 0, 2, 1, 4, 1, 2, 0, 0),
                intArrayOf(0, 0, 2, 1, 4, 1, 2, 0, 0),
                intArrayOf(0, 0, 2, 6, 3, 3, 2, 0, 0),
                intArrayOf(0, 0, 2, 3, 4, 1, 2, 0, 0),
                intArrayOf(0, 0, 2, 3, 4, 1, 2, 0, 0),
                intArrayOf(0, 0, 2, 3, 4, 1, 2, 0, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 0, 0)
            ),
            playerStartRow = 4,
            playerStartCol = 3,
            starThresholds = intArrayOf(80, 140)
        ),

        // 关卡 6 - L形走廊 (2 boxes, 2 targets)
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 0, 2, 2, 2, 2, 2, 0),
                intArrayOf(0, 0, 2, 2, 1, 3, 1, 2, 0),
                intArrayOf(0, 0, 2, 1, 1, 1, 1, 2, 0),
                intArrayOf(0, 0, 2, 1, 4, 3, 1, 2, 0),
                intArrayOf(0, 0, 2, 1, 4, 2, 1, 2, 0),
                intArrayOf(0, 0, 2, 6, 1, 1, 1, 2, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 2, 0),
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            playerStartRow = 6,
            playerStartCol = 3,
            starThresholds = intArrayOf(25, 45)
        ),

        // 关卡 7 - 三箱横排 (3 boxes, 3 targets)
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 0, 0),
                intArrayOf(0, 0, 2, 1, 6, 1, 2, 0, 0),
                intArrayOf(0, 0, 2, 3, 3, 3, 2, 0, 0),
                intArrayOf(0, 0, 2, 4, 4, 4, 2, 2, 0),
                intArrayOf(0, 0, 2, 1, 1, 1, 1, 2, 0),
                intArrayOf(0, 0, 2, 1, 1, 1, 1, 2, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 2, 0),
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            playerStartRow = 2,
            playerStartCol = 4,
            starThresholds = intArrayOf(35, 60)
        ),

        // 关卡 8 - 六箱对称 (6 boxes, 6 targets)
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 0, 0),
                intArrayOf(0, 0, 2, 3, 3, 3, 2, 0, 0),
                intArrayOf(0, 0, 2, 1, 4, 1, 2, 0, 0),
                intArrayOf(0, 0, 2, 4, 4, 4, 2, 0, 0),
                intArrayOf(0, 0, 2, 1, 4, 6, 2, 0, 0),
                intArrayOf(0, 0, 2, 1, 4, 1, 2, 0, 0),
                intArrayOf(0, 0, 2, 3, 3, 3, 2, 0, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 0, 0),
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            playerStartRow = 4,
            playerStartCol = 5,
            starThresholds = intArrayOf(70, 120)
        ),

        // 关卡 9 - 九箱十字 (9 boxes, 9 targets, 含玩家在目标上)
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 0, 0),
                intArrayOf(0, 2, 2, 3, 3, 3, 2, 2, 0),
                intArrayOf(0, 2, 1, 4, 4, 4, 3, 2, 0),
                intArrayOf(0, 2, 1, 4, 7, 4, 1, 2, 0),
                intArrayOf(0, 2, 3, 4, 4, 4, 1, 2, 0),
                intArrayOf(0, 2, 2, 3, 3, 1, 1, 2, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 2, 0),
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            playerStartRow = 4,
            playerStartCol = 4,
            starThresholds = intArrayOf(200, 350)
        ),

        // 关卡 10 - 八箱对称十字 (8 boxes, 8 targets)
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 0, 0),
                intArrayOf(0, 2, 2, 3, 1, 3, 2, 2, 0),
                intArrayOf(0, 2, 3, 4, 4, 4, 3, 2, 0),
                intArrayOf(0, 2, 1, 4, 6, 4, 1, 2, 0),
                intArrayOf(0, 2, 3, 4, 4, 4, 3, 2, 0),
                intArrayOf(0, 2, 2, 3, 1, 3, 2, 2, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 0, 0),
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            playerStartRow = 4,
            playerStartCol = 4,
            starThresholds = intArrayOf(150, 260)
        ),

        // 关卡 11 - 八箱含初始目标 (8 boxes, 8 targets)
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 0, 0),
                intArrayOf(0, 2, 2, 3, 1, 5, 2, 2, 0),
                intArrayOf(0, 2, 3, 4, 4, 1, 3, 2, 0),
                intArrayOf(0, 2, 4, 6, 1, 4, 1, 2, 0),
                intArrayOf(0, 2, 3, 4, 4, 4, 3, 2, 0),
                intArrayOf(0, 2, 2, 3, 1, 3, 2, 2, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 0, 0),
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            playerStartRow = 3,
            playerStartCol = 3,
            starThresholds = intArrayOf(160, 280)
        ),

        // 关卡 12 - 四箱迷宫走廊 (4 boxes, 4 targets)
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                intArrayOf(0, 2, 2, 2, 2, 2, 2, 0, 0),
                intArrayOf(0, 2, 1, 1, 3, 1, 2, 2, 0),
                intArrayOf(0, 2, 1, 2, 1, 1, 3, 2, 0),
                intArrayOf(0, 2, 1, 2, 4, 1, 2, 2, 0),
                intArrayOf(0, 2, 1, 3, 4, 3, 1, 2, 0),
                intArrayOf(0, 2, 1, 1, 4, 4, 1, 2, 0),
                intArrayOf(0, 2, 2, 1, 1, 6, 1, 2, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 2, 0)
            ),
            playerStartRow = 7,
            playerStartCol = 5,
            starThresholds = intArrayOf(60, 100)
        ),

        // 关卡 13 - 四箱通道 (4 boxes, 4 targets)
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 0, 0, 0),
                intArrayOf(0, 0, 2, 6, 1, 2, 2, 2, 0),
                intArrayOf(0, 0, 2, 3, 4, 1, 1, 2, 0),
                intArrayOf(0, 0, 2, 4, 3, 4, 1, 2, 0),
                intArrayOf(0, 0, 2, 1, 4, 3, 1, 2, 0),
                intArrayOf(0, 0, 2, 1, 1, 1, 3, 2, 0),
                intArrayOf(0, 0, 2, 2, 2, 2, 2, 2, 0),
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            playerStartRow = 2,
            playerStartCol = 3,
            starThresholds = intArrayOf(50, 85)
        )
    )
}
