package com.sokoban.game

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var levelSelectLayout: LinearLayout
    private lateinit var gameLayout: LinearLayout
    private lateinit var winOverlay: LinearLayout
    private lateinit var gameView: GameView
    private lateinit var tvLevel: TextView
    private lateinit var tvMoves: TextView
    private lateinit var levelGrid: GridLayout

    private var currentLevel = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化视图
        levelSelectLayout = findViewById(R.id.levelSelectLayout)
        gameLayout = findViewById(R.id.gameLayout)
        winOverlay = findViewById(R.id.winOverlay)
        gameView = findViewById(R.id.gameView)
        tvLevel = findViewById(R.id.tvLevel)
        tvMoves = findViewById(R.id.tvMoves)
        levelGrid = findViewById(R.id.levelGrid)

        // 设置返回按钮
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            showLevelSelect()
        }

        // 设置重新开始按钮
        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            gameView.restart()
            updateMovesDisplay()
        }

        // 设置撤销按钮
        findViewById<Button>(R.id.btnUndo).setOnClickListener {
            gameView.undo()
            updateMovesDisplay()
        }

        // 设置下一关按钮
        findViewById<Button>(R.id.btnNextLevel).setOnClickListener {
            winOverlay.visibility = View.GONE
            currentLevel++
            if (currentLevel < LevelData.levels.size) {
                startLevel(currentLevel)
            } else {
                showLevelSelect()
            }
        }

        // 设置游戏回调
        gameView.setOnMoveCallback {
            updateMovesDisplay()
        }

        gameView.setOnCompleteCallback {
            showWinDialog()
        }

        // 创建关卡选择按钮
        createLevelButtons()

        // 显示关卡选择界面
        showLevelSelect()
    }

    private fun createLevelButtons() {
        levelGrid.removeAllViews()

        for (i in LevelData.levels.indices) {
            val button = Button(this).apply {
                text = "第 ${i + 1} 关"
                textSize = 16f
                setBackgroundColor(ContextCompat.getColor(context, R.color.player_color))
                setTextColor(ContextCompat.getColor(context, R.color.white))

                val params = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    setMargins(8, 8, 8, 8)
                }
                layoutParams = params

                setOnClickListener {
                    currentLevel = i
                    startLevel(i)
                }
            }
            levelGrid.addView(button)
        }
    }

    private fun startLevel(levelIndex: Int) {
        levelSelectLayout.visibility = View.GONE
        gameLayout.visibility = View.VISIBLE
        winOverlay.visibility = View.GONE

        tvLevel.text = getString(R.string.level_d, levelIndex + 1)

        // 延迟加载关卡，等待视图完成布局
        gameView.post {
            gameView.loadLevel(levelIndex)
            updateMovesDisplay()
        }
    }

    private fun showLevelSelect() {
        levelSelectLayout.visibility = View.VISIBLE
        gameLayout.visibility = View.GONE
        winOverlay.visibility = View.GONE
    }

    private fun showWinDialog() {
        winOverlay.visibility = View.VISIBLE
    }

    private fun updateMovesDisplay() {
        tvMoves.text = getString(R.string.moves, gameView.getMoves())
    }
}
