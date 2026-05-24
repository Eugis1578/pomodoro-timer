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

    private lateinit var mainMenuLayout: LinearLayout
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

        mainMenuLayout = findViewById(R.id.mainMenuLayout)
        levelSelectLayout = findViewById(R.id.levelSelectLayout)
        gameLayout = findViewById(R.id.gameLayout)
        winOverlay = findViewById(R.id.winOverlay)
        gameView = findViewById(R.id.gameView)
        tvLevel = findViewById(R.id.tvLevel)
        tvMoves = findViewById(R.id.tvMoves)
        levelGrid = findViewById(R.id.levelGrid)

        // 开始游戏 → 直接进入第1关
        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            currentLevel = 0
            startLevel(0)
        }

        // 选择关卡 → 显示关卡列表
        findViewById<Button>(R.id.btnLevelSelect).setOnClickListener {
            showLevelSelect()
        }

        // 关卡列表返回主菜单
        findViewById<Button>(R.id.btnBackToMenu).setOnClickListener {
            showMainMenu()
        }

        // 游戏内返回按钮
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            showMainMenu()
        }

        // 重新开始
        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            gameView.restart()
            updateMovesDisplay()
        }

        // 下一关
        findViewById<Button>(R.id.btnNextLevel).setOnClickListener {
            winOverlay.visibility = View.GONE
            currentLevel++
            if (currentLevel < LevelData.levels.size) {
                startLevel(currentLevel)
            } else {
                showMainMenu()
            }
        }

        gameView.setOnMoveCallback {
            updateMovesDisplay()
        }

        gameView.setOnCompleteCallback {
            showWinDialog()
        }

        createLevelButtons()
        showMainMenu()
    }

    private fun createLevelButtons() {
        levelGrid.removeAllViews()

        for (i in LevelData.levels.indices) {
            val button = Button(this).apply {
                text = "第 ${i + 1} 关"
                textSize = 14f
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.button_primary)
                setTextColor(ContextCompat.getColor(context, R.color.white))

                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(6, 6, 6, 6)
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
        mainMenuLayout.visibility = View.GONE
        levelSelectLayout.visibility = View.GONE
        gameLayout.visibility = View.VISIBLE
        winOverlay.visibility = View.GONE

        tvLevel.text = getString(R.string.level_d, levelIndex + 1)

        gameView.post {
            gameView.loadLevel(levelIndex)
            updateMovesDisplay()
        }
    }

    private fun showMainMenu() {
        mainMenuLayout.visibility = View.VISIBLE
        levelSelectLayout.visibility = View.GONE
        gameLayout.visibility = View.GONE
        winOverlay.visibility = View.GONE
    }

    private fun showLevelSelect() {
        mainMenuLayout.visibility = View.GONE
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
