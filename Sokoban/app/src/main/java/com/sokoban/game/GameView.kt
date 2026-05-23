package com.sokoban.game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
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

    private var onMoveCallback: (() -> Unit)? = null
    private var onCompleteCallback: (() -> Unit)? = null

    // 画笔
    private val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D2B48C")
        style = Paint.Style.FILL
    }
    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8B4513")
        style = Paint.Style.FILL
    }
    private val wallBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6B3410")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFA500")
        style = Paint.Style.FILL
    }
    private val boxOnTargetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#32CD32")
        style = Paint.Style.FILL
    }
    private val boxBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CC8400")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF4444")
        style = Paint.Style.FILL
    }
    private val playerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4169E1")
        style = Paint.Style.FILL
    }
    private val playerBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#3158C9")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40000000")
        style = Paint.Style.FILL
    }
    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2C2C2C")
        style = Paint.Style.FILL
    }

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
            gameLogic.loadLevel(LevelData.levels[levelIndex])
            calculateCellSize()
            invalidate()
        }
    }

    fun getMoves(): Int = gameLogic.moves

    fun move(direction: Direction) {
        if (gameLogic.move(direction)) {
            onMoveCallback?.invoke()
            invalidate()

            if (gameLogic.isCompleted) {
                onCompleteCallback?.invoke()
            }
        }
    }

    fun undo() {
        if (gameLogic.undo()) {
            onMoveCallback?.invoke()
            invalidate()
        }
    }

    fun restart() {
        // 重新加载当前关卡
        val currentLevel = findCurrentLevelIndex()
        if (currentLevel >= 0) {
            loadLevel(currentLevel)
            onMoveCallback?.invoke()
        }
    }

    private fun findCurrentLevelIndex(): Int {
        // 简单实现：通过比较玩家位置来确定当前关卡
        for (i in LevelData.levels.indices) {
            val level = LevelData.levels[i]
            if (level.playerStartRow == gameLogic.playerRow &&
                level.playerStartCol == gameLogic.playerCol &&
                level.map.size == gameLogic.rows) {
                return i
            }
        }
        return 0
    }

    private fun calculateCellSize() {
        if (gameLogic.rows == 0 || gameLogic.cols == 0) return

        val availableWidth = width.toFloat()
        val availableHeight = height.toFloat()

        cellSize = min(
            availableWidth / gameLogic.cols,
            availableHeight / gameLogic.rows
        )

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

        // 绘制背景
        canvas.drawColor(Color.parseColor("#2C2C2C"))

        // 绘制网格
        for (row in 0 until gameLogic.rows) {
            for (col in 0 until gameLogic.cols) {
                val x = offsetX + col * cellSize
                val y = offsetY + row * cellSize
                val rect = RectF(x, y, x + cellSize, y + cellSize)
                val cellType = gameLogic.getCell(row, col)

                when (cellType) {
                    CellType.EMPTY.ordinal -> {
                        canvas.drawRect(rect, emptyPaint)
                    }
                    CellType.FLOOR.ordinal -> {
                        drawFloor(canvas, rect)
                    }
                    CellType.WALL.ordinal -> {
                        drawWall(canvas, rect, row, col)
                    }
                    CellType.TARGET.ordinal -> {
                        drawFloor(canvas, rect)
                        drawTarget(canvas, rect)
                    }
                    CellType.BOX.ordinal -> {
                        drawFloor(canvas, rect)
                        drawBox(canvas, rect, false)
                    }
                    CellType.BOX_ON_TARGET.ordinal -> {
                        drawFloor(canvas, rect)
                        drawBox(canvas, rect, true)
                    }
                    CellType.PLAYER.ordinal -> {
                        drawFloor(canvas, rect)
                        drawPlayer(canvas, rect)
                    }
                    CellType.PLAYER_ON_TARGET.ordinal -> {
                        drawFloor(canvas, rect)
                        drawTarget(canvas, rect)
                        drawPlayer(canvas, rect)
                    }
                }
            }
        }
    }

    private fun drawFloor(canvas: Canvas, rect: RectF) {
        val padding = 1f
        canvas.drawRect(
            rect.left + padding,
            rect.top + padding,
            rect.right - padding,
            rect.bottom - padding,
            floorPaint
        )
    }

    private fun drawWall(canvas: Canvas, rect: RectF, row: Int, col: Int) {
        // 墙壁主体
        canvas.drawRect(rect, wallPaint)

        // 墙壁边框
        canvas.drawRect(rect, wallBorderPaint)

        // 墙壁纹理（简单的砖块效果）
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#7B4420")
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        // 水平线
        canvas.drawLine(
            rect.left,
            rect.top + cellSize / 3,
            rect.right,
            rect.top + cellSize / 3,
            linePaint
        )
        canvas.drawLine(
            rect.left,
            rect.top + cellSize * 2 / 3,
            rect.right,
            rect.top + cellSize * 2 / 3,
            linePaint
        )

        // 垂直线（错开）
        if (row % 2 == 0) {
            canvas.drawLine(
                rect.left + cellSize / 2,
                rect.top,
                rect.left + cellSize / 2,
                rect.top + cellSize / 3,
                linePaint
            )
            canvas.drawLine(
                rect.left + cellSize / 2,
                rect.top + cellSize * 2 / 3,
                rect.left + cellSize / 2,
                rect.bottom,
                linePaint
            )
        } else {
            canvas.drawLine(
                rect.left + cellSize / 4,
                rect.top,
                rect.left + cellSize / 4,
                rect.top + cellSize / 3,
                linePaint
            )
            canvas.drawLine(
                rect.left + cellSize * 3 / 4,
                rect.top + cellSize / 3,
                rect.left + cellSize * 3 / 4,
                rect.top + cellSize * 2 / 3,
                linePaint
            )
        }
    }

    private fun drawTarget(canvas: Canvas, rect: RectF) {
        val centerX = rect.centerX()
        val centerY = rect.centerY()
        val radius = cellSize * 0.2f

        // 外圈
        canvas.drawCircle(centerX, centerY, radius, targetPaint)

        // 内圈
        val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF6666")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, radius * 0.6f, innerPaint)
    }

    private fun drawBox(canvas: Canvas, rect: RectF, onTarget: Boolean) {
        val padding = cellSize * 0.1f
        val boxRect = RectF(
            rect.left + padding,
            rect.top + padding,
            rect.right - padding,
            rect.bottom - padding
        )

        // 阴影
        val shadowRect = RectF(
            boxRect.left + 3,
            boxRect.top + 3,
            boxRect.right + 3,
            boxRect.bottom + 3
        )
        canvas.drawRoundRect(shadowRect, 8f, 8f, shadowPaint)

        // 箱子主体
        val paint = if (onTarget) boxOnTargetPaint else boxPaint
        canvas.drawRoundRect(boxRect, 8f, 8f, paint)

        // 箱子边框
        canvas.drawRoundRect(boxRect, 8f, 8f, boxBorderPaint)

        // 箱子上的 X 装饰
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (onTarget) Color.parseColor("#228B22") else Color.parseColor("#CC8400")
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }

        val inset = cellSize * 0.25f
        canvas.drawLine(
            boxRect.left + inset,
            boxRect.top + inset,
            boxRect.right - inset,
            boxRect.bottom - inset,
            linePaint
        )
        canvas.drawLine(
            boxRect.right - inset,
            boxRect.top + inset,
            boxRect.left + inset,
            boxRect.bottom - inset,
            linePaint
        )
    }

    private fun drawPlayer(canvas: Canvas, rect: RectF) {
        val centerX = rect.centerX()
        val centerY = rect.centerY()
        val radius = cellSize * 0.35f

        // 阴影
        canvas.drawCircle(centerX + 2, centerY + 2, radius, shadowPaint)

        // 身体
        canvas.drawCircle(centerX, centerY, radius, playerPaint)

        // 边框
        canvas.drawCircle(centerX, centerY, radius, playerBorderPaint)

        // 眼睛
        val eyePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val eyeRadius = radius * 0.15f
        val eyeOffset = radius * 0.3f

        canvas.drawCircle(centerX - eyeOffset, centerY - eyeOffset * 0.5f, eyeRadius, eyePaint)
        canvas.drawCircle(centerX + eyeOffset, centerY - eyeOffset * 0.5f, eyeRadius, eyePaint)

        // 瞳孔
        val pupilPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
        val pupilRadius = eyeRadius * 0.6f
        canvas.drawCircle(centerX - eyeOffset, centerY - eyeOffset * 0.5f, pupilRadius, pupilPaint)
        canvas.drawCircle(centerX + eyeOffset, centerY - eyeOffset * 0.5f, pupilRadius, pupilPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}
