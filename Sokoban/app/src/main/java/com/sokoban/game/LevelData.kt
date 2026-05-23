package com.sokoban.game

/**
 * 推箱子关卡数据
 * 地图元素编码:
 * 0 = 空地 (floor)
 * 1 = 墙壁 (wall)
 * 2 = 目标点 (target)
 * 3 = 箱子 (box)
 * 4 = 箱子在目标点上 (box on target)
 * 5 = 玩家 (player)
 * 6 = 玩家在目标点上 (player on target)
 */

data class Level(
    val map: Array<IntArray>,
    val playerStartRow: Int,
    val playerStartCol: Int
)

object LevelData {

    val levels = listOf(
        // 关卡 1 - 入门
        Level(
            map = arrayOf(
                intArrayOf(1, 1, 1, 1, 1, 0, 0, 0),
                intArrayOf(1, 0, 0, 0, 1, 0, 0, 0),
                intArrayOf(1, 0, 3, 0, 1, 1, 1, 1),
                intArrayOf(1, 0, 3, 5, 0, 0, 0, 1),
                intArrayOf(1, 1, 1, 0, 3, 0, 1, 1),
                intArrayOf(0, 0, 1, 0, 2, 0, 1, 0),
                intArrayOf(0, 0, 1, 2, 2, 2, 1, 0),
                intArrayOf(0, 0, 1, 1, 1, 1, 1, 0)
            ),
            playerStartRow = 3,
            playerStartCol = 3
        ),
        // 关卡 2
        Level(
            map = arrayOf(
                intArrayOf(0, 1, 1, 1, 1, 0),
                intArrayOf(1, 1, 0, 0, 1, 0),
                intArrayOf(1, 0, 5, 3, 1, 0),
                intArrayOf(1, 1, 3, 0, 1, 1),
                intArrayOf(0, 1, 0, 3, 0, 1),
                intArrayOf(0, 1, 0, 2, 0, 1),
                intArrayOf(0, 1, 2, 2, 2, 1),
                intArrayOf(0, 1, 1, 1, 1, 1)
            ),
            playerStartRow = 2,
            playerStartCol = 2
        ),
        // 关卡 3
        Level(
            map = arrayOf(
                intArrayOf(1, 1, 1, 1, 1, 0, 0),
                intArrayOf(1, 2, 0, 0, 1, 0, 0),
                intArrayOf(1, 2, 3, 0, 1, 1, 1),
                intArrayOf(1, 2, 3, 5, 0, 0, 1),
                intArrayOf(1, 1, 1, 3, 0, 0, 1),
                intArrayOf(0, 0, 1, 0, 0, 1, 1),
                intArrayOf(0, 0, 1, 0, 0, 1, 0),
                intArrayOf(0, 0, 1, 1, 1, 1, 0)
            ),
            playerStartRow = 3,
            playerStartCol = 3
        ),
        // 关卡 4
        Level(
            map = arrayOf(
                intArrayOf(0, 1, 1, 1, 1, 0),
                intArrayOf(0, 1, 0, 0, 1, 0),
                intArrayOf(1, 1, 0, 1, 1, 1),
                intArrayOf(1, 0, 0, 0, 0, 1),
                intArrayOf(1, 0, 3, 3, 0, 1),
                intArrayOf(1, 1, 3, 0, 1, 1),
                intArrayOf(0, 1, 2, 2, 1, 0),
                intArrayOf(0, 1, 2, 2, 1, 0),
                intArrayOf(0, 1, 1, 1, 1, 0)
            ),
            playerStartRow = 3,
            playerStartCol = 2
        ),
        // 关卡 5
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 1, 1, 1, 1, 1, 0),
                intArrayOf(1, 1, 1, 0, 0, 0, 1, 0),
                intArrayOf(1, 2, 5, 3, 0, 0, 1, 0),
                intArrayOf(1, 2, 2, 3, 3, 0, 1, 0),
                intArrayOf(1, 2, 2, 0, 3, 1, 1, 0),
                intArrayOf(1, 1, 1, 1, 0, 1, 0, 0),
                intArrayOf(0, 0, 0, 1, 0, 1, 0, 0),
                intArrayOf(0, 0, 0, 1, 1, 1, 0, 0)
            ),
            playerStartRow = 2,
            playerStartCol = 2
        ),
        // 关卡 6
        Level(
            map = arrayOf(
                intArrayOf(1, 1, 1, 1, 1, 1),
                intArrayOf(1, 0, 0, 0, 0, 1),
                intArrayOf(1, 0, 3, 3, 0, 1),
                intArrayOf(1, 0, 3, 0, 0, 1),
                intArrayOf(1, 1, 0, 3, 1, 1),
                intArrayOf(0, 1, 0, 0, 1, 0),
                intArrayOf(0, 1, 5, 2, 1, 0),
                intArrayOf(0, 1, 2, 2, 1, 0),
                intArrayOf(0, 1, 2, 1, 1, 0),
                intArrayOf(0, 1, 1, 1, 0, 0)
            ),
            playerStartRow = 6,
            playerStartCol = 2
        ),
        // 关卡 7
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 0, 1, 1, 1, 1, 1, 1, 0),
                intArrayOf(0, 0, 1, 1, 0, 0, 0, 0, 1, 0),
                intArrayOf(0, 0, 1, 0, 0, 1, 0, 0, 1, 1),
                intArrayOf(0, 0, 1, 0, 0, 0, 3, 0, 0, 1),
                intArrayOf(1, 1, 1, 0, 1, 0, 1, 1, 0, 1),
                intArrayOf(1, 2, 0, 0, 1, 5, 0, 1, 0, 1),
                intArrayOf(1, 2, 0, 3, 0, 0, 3, 0, 0, 1),
                intArrayOf(1, 2, 2, 1, 1, 1, 0, 1, 1, 1),
                intArrayOf(1, 1, 1, 1, 0, 1, 1, 1, 0, 0),
                intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            playerStartRow = 5,
            playerStartCol = 5
        ),
        // 关卡 8 - 较难
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 1, 1, 1, 1, 0, 0),
                intArrayOf(1, 1, 1, 0, 0, 1, 1, 0),
                intArrayOf(1, 2, 0, 3, 0, 0, 1, 0),
                intArrayOf(1, 2, 2, 3, 3, 0, 1, 1),
                intArrayOf(1, 2, 0, 0, 3, 5, 0, 1),
                intArrayOf(1, 1, 1, 0, 0, 0, 0, 1),
                intArrayOf(0, 0, 1, 0, 0, 1, 1, 1),
                intArrayOf(0, 0, 1, 1, 1, 1, 0, 0)
            ),
            playerStartRow = 4,
            playerStartCol = 5
        ),
        // 关卡 9
        Level(
            map = arrayOf(
                intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
                intArrayOf(1, 0, 0, 1, 0, 0, 0, 1),
                intArrayOf(1, 0, 3, 0, 3, 0, 0, 1),
                intArrayOf(1, 0, 0, 5, 0, 3, 0, 1),
                intArrayOf(1, 0, 3, 0, 3, 0, 0, 1),
                intArrayOf(1, 0, 0, 1, 0, 0, 0, 1),
                intArrayOf(1, 1, 1, 1, 1, 1, 1, 1)
            ),
            playerStartRow = 3,
            playerStartCol = 3
        ),
        // 关卡 10 - 终极挑战
        Level(
            map = arrayOf(
                intArrayOf(0, 0, 0, 1, 1, 1, 1, 1, 0, 0),
                intArrayOf(0, 0, 0, 1, 0, 0, 0, 1, 0, 0),
                intArrayOf(1, 1, 1, 1, 0, 1, 0, 1, 1, 1),
                intArrayOf(1, 2, 2, 0, 3, 0, 0, 0, 2, 1),
                intArrayOf(1, 1, 1, 0, 3, 3, 1, 0, 1, 1),
                intArrayOf(0, 0, 1, 0, 3, 0, 1, 0, 1, 0),
                intArrayOf(0, 0, 1, 0, 0, 0, 0, 0, 1, 0),
                intArrayOf(0, 0, 1, 0, 0, 5, 1, 1, 1, 0),
                intArrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 0)
            ),
            playerStartRow = 7,
            playerStartCol = 5
        )
    )
}
