package com.sokoban.game

import android.animation.ValueAnimator
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

    // 画笔
    private val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D2B48C")
        style = Paint.Style.FILL
    }
    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9E9E9E")
        style = Paint.Style.FILL
    }
    private val wallBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#757575")
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
        color = Color.parseColor("#E8E0D8")
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
            currentLevelIndex = levelIndex
            gameLogic.loadLevel(LevelData.levels[levelIndex])
            calculateCellSize()
            invalidate()
        }
    }

    fun getMoves(): Int = gameLogic.moves

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

    fun restart() {
        // 重新加载当前关卡
        val currentLevel = findCurrentLevelIndex()
        if (currentLevel >= 0) {
            loadLevel(currentLevel)
            onMoveCallback?.invoke()
        }
    }

    private fun findCurrentLevelIndex(): Int = currentLevelIndex

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
                        canvas.drawRect(rect, emptyPaint)
                    }
                    CellType.FLOOR.ordinal -> {
                        drawFloor(canvas, rect)
                    }
                    CellType.WALL.ordinal -> {
                        drawWall(canvas, rect, row)
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

        // 动画中：在插值位置绘制玩家和箱子
        if (isAnimating) {
            val t = animProgress

            // 玩家滑动
            val px = offsetX + (animPlayerFromCol + (animPlayerToCol - animPlayerFromCol) * t) * cellSize
            val py = offsetY + (animPlayerFromRow + (animPlayerToRow - animPlayerFromRow) * t) * cellSize
            val playerRect = RectF(px, py, px + cellSize, py + cellSize)
            drawPlayer(canvas, playerRect)

            // 箱子滑动
            if (animHasBox) {
                val bx = offsetX + (animBoxFromCol + (animBoxToCol - animBoxFromCol) * t) * cellSize
                val by = offsetY + (animBoxFromRow + (animBoxToRow - animBoxFromRow) * t) * cellSize
                val boxRect = RectF(bx, by, bx + cellSize, by + cellSize)
                drawBox(canvas, boxRect, animBoxDestOnTarget)
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

    private fun drawWall(canvas: Canvas, rect: RectF, row: Int) {
        // 墙壁主体
        canvas.drawRect(rect, wallPaint)

        // 墙壁边框
        canvas.drawRect(rect, wallBorderPaint)

        // 墙壁纹理（简单的砖块效果）
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#8A8A8A")
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
        val padding = cellSize * 0.12f
        val playerRect = RectF(
            rect.left + padding,
            rect.top + padding,
            rect.right - padding,
            rect.bottom - padding
        )

        // 白色方块身体
        val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(playerRect, 6f, 6f, whitePaint)

        // 边框
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#CCCCCC")
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(playerRect, 6f, 6f, borderPaint)

        // 两个小黑方块眼睛
        val eyePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
        val eyeSize = cellSize * 0.08f
        val eyeY = rect.centerY() - cellSize * 0.08f
        val leftEyeX = rect.centerX() - cellSize * 0.15f
        val rightEyeX = rect.centerX() + cellSize * 0.15f

        canvas.drawRect(leftEyeX - eyeSize, eyeY - eyeSize, leftEyeX + eyeSize, eyeY + eyeSize, eyePaint)
        canvas.drawRect(rightEyeX - eyeSize, eyeY - eyeSize, rightEyeX + eyeSize, eyeY + eyeSize, eyePaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}
