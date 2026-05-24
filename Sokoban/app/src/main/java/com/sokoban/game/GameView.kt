package com.sokoban.game

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.min

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val gameLogic = GameLogic()
    private var cellSize = 0f
    private var offsetX = 0f
    private var offsetY = 0f

    private var currentLevelIndex = 0
    private var customLevel: Level? = null

    private var onMoveCallback: (() -> Unit)? = null
    private var onCompleteCallback: (() -> Unit)? = null

    // 滑动动画
    private var isAnimating = false
    private var animProgress = 0f
    private var animPlayerFromRow = 0
    private var animPlayerFromCol = 0
    private var animPlayerToRow = 0
    private var animPlayerToCol = 0
    private var animBoxFromRow = -1
    private var animBoxFromCol = -1
    private var animBoxToRow = -1
    private var animBoxToCol = -1
    private var animHasBox = false
    private var animPlayerOnTarget = false
    private var animBoxOnTarget = false
    private var animPlayerDestOnTarget = false
    private var animBoxDestOnTarget = false
    private var animValueAnimator: ValueAnimator? = null

    // 计时器
    private var startTime = 0L
    private var elapsedSeconds = 0

    // 画笔委托给 CellRenderer

    // 手势检测
    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null) return false

            val dx = e2.x - e1.x
            val dy = e2.y - e1.y

            // 判断滑动方向
            if (Math.abs(dx) > Math.abs(dy)) {
                // 水平滑动
                if (Math.abs(dx) > 50) {
                    if (dx > 0) move(Direction.RIGHT) else move(Direction.LEFT)
                }
            } else {
                // 垂直滑动
                if (Math.abs(dy) > 50) {
                    if (dy > 0) move(Direction.DOWN) else move(Direction.UP)
                }
            }
            return true
        }

        override fun onDown(e: MotionEvent): Boolean = true
    })

    fun setOnMoveCallback(callback: () -> Unit) {
        onMoveCallback = callback
    }

    fun setOnCompleteCallback(callback: () -> Unit) {
        onCompleteCallback = callback
    }

    fun loadLevel(levelIndex: Int) {
        if (levelIndex in LevelData.levels.indices) {
            currentLevelIndex = levelIndex
            gameLogic.loadLevel(LevelData.levels[levelIndex])
            calculateCellSize()
            startTime = System.currentTimeMillis()
            elapsedSeconds = 0
            invalidate()
        }
    }

    fun getMoves(): Int = gameLogic.moves
    fun getElapsedSeconds(): Int = elapsedSeconds

    fun move(direction: Direction) {
        if (isAnimating) return

        // 记录移动前位置
        val oldPlayerRow = gameLogic.playerRow
        val oldPlayerCol = gameLogic.playerCol

        if (gameLogic.move(direction)) {
            onMoveCallback?.invoke()

            // 新位置
            val newPlayerRow = gameLogic.playerRow
            val newPlayerCol = gameLogic.playerCol

            val (dr, dc) = when (direction) {
                Direction.UP -> Pair(-1, 0)
                Direction.DOWN -> Pair(1, 0)
                Direction.LEFT -> Pair(0, -1)
                Direction.RIGHT -> Pair(0, 1)
            }

            // 判断是否推了箱子（新位置 = 旧箱子位置，再往前一格 = 箱子新位置）
            val boxOldRow = newPlayerRow
            val boxOldCol = newPlayerCol
            val boxNewRow = newPlayerRow + dr
            val boxNewCol = newPlayerCol + dc

            val pushedBox = (boxNewRow != oldPlayerRow || boxNewCol != oldPlayerCol) &&
                boxNewRow in 0 until gameLogic.rows &&
                boxNewCol in 0 until gameLogic.cols &&
                (gameLogic.getCell(boxNewRow, boxNewCol) == CellType.BOX.ordinal ||
                 gameLogic.getCell(boxNewRow, boxNewCol) == CellType.BOX_ON_TARGET.ordinal)

            // 设置动画参数
            animPlayerFromRow = oldPlayerRow
            animPlayerFromCol = oldPlayerCol
            animPlayerToRow = newPlayerRow
            animPlayerToCol = newPlayerCol
            animPlayerOnTarget = gameLogic.isTarget(oldPlayerRow, oldPlayerCol)
            animPlayerDestOnTarget = gameLogic.isTarget(newPlayerRow, newPlayerCol)

            animHasBox = pushedBox
            if (pushedBox) {
                animBoxFromRow = boxOldRow
                animBoxFromCol = boxOldCol
                animBoxToRow = boxNewRow
                animBoxToCol = boxNewCol
                animBoxOnTarget = gameLogic.isTarget(boxOldRow, boxOldCol)
                animBoxDestOnTarget = gameLogic.isTarget(boxNewRow, boxNewCol)
            }

            startSlideAnimation()

            if (gameLogic.isCompleted) {
                elapsedSeconds = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                onCompleteCallback?.invoke()
            }
        }
    }

    private fun startSlideAnimation() {
        isAnimating = true
        animProgress = 0f

        animValueAnimator?.cancel()
        animValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 80
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                animProgress = animation.animatedValue as Float
                invalidate()
                if (animProgress >= 1f) {
                    isAnimating = false
                }
            }
            start()
        }
    }

    fun loadLevelFromData(level: Level) {
        currentLevelIndex = -1
        customLevel = level
        gameLogic.loadLevel(level)
        calculateCellSize()
        startTime = System.currentTimeMillis()
        elapsedSeconds = 0
        invalidate()
    }

    fun restart() {
        if (currentLevelIndex >= 0) {
            loadLevel(currentLevelIndex)
        } else {
            customLevel?.let { loadLevelFromData(it) }
        }
        onMoveCallback?.invoke()
    }

    private fun calculateCellSize() {
        if (gameLogic.rows == 0 || gameLogic.cols == 0) return

        val availableWidth = width.toFloat()
        val availableHeight = height.toFloat()

        cellSize = min(
            availableWidth / gameLogic.cols,
            availableHeight / gameLogic.rows
        ) * 0.85f

        // 居中偏移
        offsetX = (availableWidth - cellSize * gameLogic.cols) / 2
        offsetY = (availableHeight - cellSize * gameLogic.rows) / 2
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateCellSize()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (gameLogic.rows == 0) return

        canvas.drawColor(Color.parseColor("#F5F0EB"))

        for (row in 0 until gameLogic.rows) {
            for (col in 0 until gameLogic.cols) {
                val x = offsetX + col * cellSize
                val y = offsetY + row * cellSize
                val rect = RectF(x, y, x + cellSize, y + cellSize)
                var cellType = gameLogic.getCell(row, col)

                // 动画中：把玩家/箱子的当前位置临时改为地板或目标
                if (isAnimating) {
                    if (row == animPlayerToRow && col == animPlayerToCol) {
                        cellType = if (animPlayerDestOnTarget) CellType.TARGET.ordinal else CellType.FLOOR.ordinal
                    }
                    if (animHasBox && row == animBoxToRow && col == animBoxToCol) {
                        cellType = if (animBoxDestOnTarget) CellType.TARGET.ordinal else CellType.FLOOR.ordinal
                    }
                }

                when (cellType) {
                    CellType.EMPTY.ordinal -> {
                        canvas.drawRect(rect, CellRenderer.emptyPaint)
                    }
                    CellType.FLOOR.ordinal -> {
                        CellRenderer.drawFloor(canvas, rect)
                    }
                    CellType.WALL.ordinal -> {
                        CellRenderer.drawWall(canvas, rect, row, cellSize)
                    }
                    CellType.TARGET.ordinal -> {
                        CellRenderer.drawFloor(canvas, rect)
                        CellRenderer.drawTarget(canvas, rect, cellSize)
                    }
                    CellType.BOX.ordinal -> {
                        CellRenderer.drawFloor(canvas, rect)
                        CellRenderer.drawBox(canvas, rect, false, cellSize)
                    }
                    CellType.BOX_ON_TARGET.ordinal -> {
                        CellRenderer.drawFloor(canvas, rect)
                        CellRenderer.drawBox(canvas, rect, true, cellSize)
                    }
                    CellType.PLAYER.ordinal -> {
                        CellRenderer.drawFloor(canvas, rect)
                        CellRenderer.drawPlayer(canvas, rect, cellSize)
                    }
                    CellType.PLAYER_ON_TARGET.ordinal -> {
                        CellRenderer.drawFloor(canvas, rect)
                        CellRenderer.drawTarget(canvas, rect, cellSize)
                        CellRenderer.drawPlayer(canvas, rect, cellSize)
                    }
                }
            }
        }

        // 动画中：在插值位置绘制玩家和箱子
        if (isAnimating) {
            val t = animProgress

            // 玩家滑动
            val px = offsetX + (animPlayerFromCol + (animPlayerToCol - animPlayerFromCol) * t) * cellSize
            val py = offsetY + (animPlayerFromRow + (animPlayerToRow - animPlayerFromRow) * t) * cellSize
            val playerRect = RectF(px, py, px + cellSize, py + cellSize)
            CellRenderer.drawPlayer(canvas, playerRect, cellSize)

            // 箱子滑动
            if (animHasBox) {
                val bx = offsetX + (animBoxFromCol + (animBoxToCol - animBoxFromCol) * t) * cellSize
                val by = offsetY + (animBoxFromRow + (animBoxToRow - animBoxFromRow) * t) * cellSize
                val boxRect = RectF(bx, by, bx + cellSize, by + cellSize)
                CellRenderer.drawBox(canvas, boxRect, animBoxDestOnTarget, cellSize)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}
