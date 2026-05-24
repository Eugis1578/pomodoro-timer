package com.sokoban.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

object CellRenderer {

    val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D2B48C")
        style = Paint.Style.FILL
    }
    val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9E9E9E")
        style = Paint.Style.FILL
    }
    val wallBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#757575")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFA500")
        style = Paint.Style.FILL
    }
    val boxOnTargetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#32CD32")
        style = Paint.Style.FILL
    }
    val boxBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CC8400")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF4444")
        style = Paint.Style.FILL
    }
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40000000")
        style = Paint.Style.FILL
    }
    val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E8E0D8")
        style = Paint.Style.FILL
    }

    fun drawFloor(canvas: Canvas, rect: RectF) {
        val padding = 1f
        canvas.drawRect(
            rect.left + padding,
            rect.top + padding,
            rect.right - padding,
            rect.bottom - padding,
            floorPaint
        )
    }

    fun drawWall(canvas: Canvas, rect: RectF, row: Int, cellSize: Float) {
        canvas.drawRect(rect, wallPaint)
        canvas.drawRect(rect, wallBorderPaint)

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#8A8A8A")
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        canvas.drawLine(rect.left, rect.top + cellSize / 3, rect.right, rect.top + cellSize / 3, linePaint)
        canvas.drawLine(rect.left, rect.top + cellSize * 2 / 3, rect.right, rect.top + cellSize * 2 / 3, linePaint)

        if (row % 2 == 0) {
            canvas.drawLine(rect.left + cellSize / 2, rect.top, rect.left + cellSize / 2, rect.top + cellSize / 3, linePaint)
            canvas.drawLine(rect.left + cellSize / 2, rect.top + cellSize * 2 / 3, rect.left + cellSize / 2, rect.bottom, linePaint)
        } else {
            canvas.drawLine(rect.left + cellSize / 4, rect.top, rect.left + cellSize / 4, rect.top + cellSize / 3, linePaint)
            canvas.drawLine(rect.left + cellSize * 3 / 4, rect.top + cellSize / 3, rect.left + cellSize * 3 / 4, rect.top + cellSize * 2 / 3, linePaint)
        }
    }

    fun drawTarget(canvas: Canvas, rect: RectF, cellSize: Float) {
        val centerX = rect.centerX()
        val centerY = rect.centerY()
        val radius = cellSize * 0.2f

        canvas.drawCircle(centerX, centerY, radius, targetPaint)

        val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF6666")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, radius * 0.6f, innerPaint)
    }

    fun drawBox(canvas: Canvas, rect: RectF, onTarget: Boolean, cellSize: Float) {
        val padding = cellSize * 0.1f
        val boxRect = RectF(
            rect.left + padding,
            rect.top + padding,
            rect.right - padding,
            rect.bottom - padding
        )

        val shadowRect = RectF(
            boxRect.left + 3,
            boxRect.top + 3,
            boxRect.right + 3,
            boxRect.bottom + 3
        )
        canvas.drawRoundRect(shadowRect, 8f, 8f, shadowPaint)

        val paint = if (onTarget) boxOnTargetPaint else boxPaint
        canvas.drawRoundRect(boxRect, 8f, 8f, paint)
        canvas.drawRoundRect(boxRect, 8f, 8f, boxBorderPaint)

        if (onTarget) {
            // 在目标点上 → 画圆形
            val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#228B22")
                style = Paint.Style.STROKE
                strokeWidth = 3f
            }
            val radius = cellSize * 0.15f
            canvas.drawCircle(boxRect.centerX(), boxRect.centerY(), radius, circlePaint)
        } else {
            // 不在目标点上 → 画叉
            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#CC8400")
                strokeWidth = 3f
                style = Paint.Style.STROKE
            }
            val inset = cellSize * 0.25f
            canvas.drawLine(boxRect.left + inset, boxRect.top + inset, boxRect.right - inset, boxRect.bottom - inset, linePaint)
            canvas.drawLine(boxRect.right - inset, boxRect.top + inset, boxRect.left + inset, boxRect.bottom - inset, linePaint)
        }
    }

    fun drawPlayer(canvas: Canvas, rect: RectF, cellSize: Float) {
        val padding = cellSize * 0.12f
        val playerRect = RectF(
            rect.left + padding,
            rect.top + padding,
            rect.right - padding,
            rect.bottom - padding
        )

        val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(playerRect, 6f, 6f, whitePaint)

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#CCCCCC")
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(playerRect, 6f, 6f, borderPaint)

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
}
