package com.sokoban.game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class LevelEditorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val GRID_SIZE = 9
    }

    private val grid = Array(GRID_SIZE) { IntArray(GRID_SIZE) { CellType.EMPTY.ordinal } }
    var selectedCellType = CellType.WALL.ordinal

    private var cellSize = 0f
    private var offsetX = 0f
    private var offsetY = 0f

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4169E1")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    fun getGrid(): Array<IntArray> = Array(GRID_SIZE) { grid[it].copyOf() }

    fun loadGrid(map: Array<IntArray>) {
        for (r in 0 until min(GRID_SIZE, map.size)) {
            for (c in 0 until min(GRID_SIZE, map[r].size)) {
                grid[r][c] = map[r][c]
            }
        }
        invalidate()
    }

    fun clearGrid() {
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                grid[r][c] = CellType.EMPTY.ordinal
            }
        }
        invalidate()
    }

    private fun calculateCellSize() {
        if (width == 0 || height == 0) return
        cellSize = min(width.toFloat() / GRID_SIZE, height.toFloat() / GRID_SIZE) * 0.85f
        offsetX = (width - cellSize * GRID_SIZE) / 2
        offsetY = (height - cellSize * GRID_SIZE) / 2
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateCellSize()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#F5F0EB"))

        if (cellSize == 0f) return

        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                val x = offsetX + col * cellSize
                val y = offsetY + row * cellSize
                val rect = RectF(x, y, x + cellSize, y + cellSize)
                val cellType = grid[row][col]

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

        // 绘制网格线
        val gridLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#30000000")
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }
        for (i in 0..GRID_SIZE) {
            canvas.drawLine(offsetX + i * cellSize, offsetY, offsetX + i * cellSize, offsetY + GRID_SIZE * cellSize, gridLinePaint)
            canvas.drawLine(offsetX, offsetY + i * cellSize, offsetX + GRID_SIZE * cellSize, offsetY + i * cellSize, gridLinePaint)
        }
    }

    private fun resolveCellType(current: Int, selected: Int): Int {
        return when {
            // 放玩家：如果当前是目标点 → 玩家在目标上
            selected == CellType.PLAYER.ordinal && current == CellType.TARGET.ordinal ->
                CellType.PLAYER_ON_TARGET.ordinal
            // 放目标：如果玩家在目标上 → 保留玩家（去掉目标没意义，直接变玩家）
            selected == CellType.TARGET.ordinal && current == CellType.PLAYER_ON_TARGET.ordinal ->
                CellType.PLAYER_ON_TARGET.ordinal
            // 放目标：如果是玩家 → 玩家在目标上
            selected == CellType.TARGET.ordinal && current == CellType.PLAYER.ordinal ->
                CellType.PLAYER_ON_TARGET.ordinal
            // 放箱子：如果当前是目标点 → 箱子在目标上
            selected == CellType.BOX.ordinal && current == CellType.TARGET.ordinal ->
                CellType.BOX_ON_TARGET.ordinal
            // 放目标：如果是箱子 → 箱子在目标上
            selected == CellType.TARGET.ordinal && current == CellType.BOX.ordinal ->
                CellType.BOX_ON_TARGET.ordinal
            // 放地板/空地：清除复合类型
            selected == CellType.FLOOR.ordinal && current == CellType.PLAYER_ON_TARGET.ordinal ->
                CellType.TARGET.ordinal
            selected == CellType.FLOOR.ordinal && current == CellType.BOX_ON_TARGET.ordinal ->
                CellType.TARGET.ordinal
            selected == CellType.EMPTY.ordinal && current == CellType.PLAYER_ON_TARGET.ordinal ->
                CellType.EMPTY.ordinal
            selected == CellType.EMPTY.ordinal && current == CellType.BOX_ON_TARGET.ordinal ->
                CellType.EMPTY.ordinal
            // 默认：直接放置
            else -> selected
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            val col = ((event.x - offsetX) / cellSize).toInt()
            val row = ((event.y - offsetY) / cellSize).toInt()

            if (row in 0 until GRID_SIZE && col in 0 until GRID_SIZE) {
                val newType = resolveCellType(grid[row][col], selectedCellType)
                if (grid[row][col] != newType) {
                    grid[row][col] = newType
                    invalidate()
                }
            }
        }
        return true
    }
}
