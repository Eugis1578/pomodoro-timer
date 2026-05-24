package com.sokoban.game

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var editorLayout: LinearLayout
    private lateinit var editorView: LevelEditorView
    private lateinit var paletteLayout: LinearLayout
    private lateinit var tvStars: TextView
    private lateinit var tvWinStats: TextView

    private var currentLevel = 0
    private var currentCustomLevelIndex = -1
    private var paletteViews = mutableListOf<View>()

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
        editorLayout = findViewById(R.id.editorLayout)
        editorView = findViewById(R.id.editorView)
        paletteLayout = findViewById(R.id.paletteLayout)
        tvStars = findViewById(R.id.tvStars)
        tvWinStats = findViewById(R.id.tvWinStats)

        // 开始游戏 → 直接进入第1关
        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            currentLevel = 0
            startLevel(0)
        }

        // 选择关卡 → 显示关卡列表
        findViewById<Button>(R.id.btnLevelSelect).setOnClickListener {
            showLevelSelect()
        }

        // 关卡编辑器
        findViewById<Button>(R.id.btnLevelEditor).setOnClickListener {
            showEditor()
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
            if (currentLevel >= 0) {
                currentLevel++
                if (currentLevel < LevelData.levels.size) {
                    startLevel(currentLevel)
                } else {
                    showMainMenu()
                }
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

        // 编辑器按钮
        findViewById<ImageButton>(R.id.btnEditorBack).setOnClickListener {
            showMainMenu()
        }

        findViewById<Button>(R.id.btnEditorClear).setOnClickListener {
            editorView.clearGrid()
        }

        findViewById<Button>(R.id.btnEditorTest).setOnClickListener {
            testEditorLevel()
        }

        findViewById<Button>(R.id.btnEditorSave).setOnClickListener {
            saveEditorLevel()
        }

        findViewById<Button>(R.id.btnEditorExport).setOnClickListener {
            exportEditorLevel()
        }

        createLevelButtons()
        buildPalette()
        showMainMenu()
    }

    private fun createLevelButtons() {
        levelGrid.removeAllViews()

        for (i in LevelData.levels.indices) {
            val stars = StarManager.getStars(this, i)
            val starText = if (stars > 0) " ${StarManager.starsToString(stars)}" else ""
            val button = Button(this).apply {
                text = "第 ${i + 1} 关$starText"
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
                    currentCustomLevelIndex = -1
                    startLevel(i)
                }
            }
            levelGrid.addView(button)
        }

        // 追加自定义关卡按钮
        val customLevels = CustomLevelManager.loadLevels(this)
        for (i in customLevels.indices) {
            val entry = customLevels[i]
            val button = Button(this).apply {
                text = entry.name
                textSize = 14f
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.box_color)
                setTextColor(ContextCompat.getColor(context, R.color.white))

                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(6, 6, 6, 6)
                }
                layoutParams = params

                setOnClickListener {
                    currentLevel = -1
                    currentCustomLevelIndex = i
                    startCustomLevel(entry)
                }
            }
            levelGrid.addView(button)
        }
    }

    private fun startLevel(levelIndex: Int) {
        mainMenuLayout.visibility = View.GONE
        levelSelectLayout.visibility = View.GONE
        gameLayout.visibility = View.VISIBLE
        editorLayout.visibility = View.GONE
        winOverlay.visibility = View.GONE

        tvLevel.text = getString(R.string.level_d, levelIndex + 1)

        gameView.post {
            gameView.loadLevel(levelIndex)
            updateMovesDisplay()
        }
    }

    private fun startCustomLevel(entry: CustomLevelEntry) {
        mainMenuLayout.visibility = View.GONE
        levelSelectLayout.visibility = View.GONE
        gameLayout.visibility = View.VISIBLE
        editorLayout.visibility = View.GONE
        winOverlay.visibility = View.GONE

        tvLevel.text = entry.name

        gameView.post {
            gameView.loadLevelFromData(entry.level)
            updateMovesDisplay()
        }
    }

    private fun showMainMenu() {
        mainMenuLayout.visibility = View.VISIBLE
        levelSelectLayout.visibility = View.GONE
        gameLayout.visibility = View.GONE
        editorLayout.visibility = View.GONE
        winOverlay.visibility = View.GONE
    }

    private fun showEditor() {
        mainMenuLayout.visibility = View.GONE
        levelSelectLayout.visibility = View.GONE
        gameLayout.visibility = View.GONE
        editorLayout.visibility = View.VISIBLE
        winOverlay.visibility = View.GONE
        editorView.clearGrid()
    }

    private fun buildPalette() {
        paletteLayout.removeAllViews()
        paletteViews.clear()

        val cellTypes = listOf(
            Pair(CellType.EMPTY.ordinal, "空地"),
            Pair(CellType.FLOOR.ordinal, "地板"),
            Pair(CellType.WALL.ordinal, "墙"),
            Pair(CellType.TARGET.ordinal, "目标"),
            Pair(CellType.BOX.ordinal, "箱子"),
            Pair(CellType.PLAYER.ordinal, "玩家")
        )

        for ((index, pair) in cellTypes.withIndex()) {
            val (type, label) = pair

            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                val lp = LinearLayout.LayoutParams(140, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams = lp
                setPadding(8, 8, 8, 8)
            }

            // 颜色预览方块
            val preview = View(this).apply {
                val size = 48
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    gravity = Gravity.CENTER
                    bottomMargin = 4
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 4f
                    setColor(getCellPreviewColor(type))
                    setStroke(2, Color.parseColor("#CCCCCC"))
                }
            }

            val labelView = TextView(this).apply {
                text = label
                textSize = 10f
                gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            }

            item.addView(preview)
            item.addView(labelView)

            item.setOnClickListener {
                editorView.selectedCellType = type
                highlightPaletteItem(index)
            }

            paletteLayout.addView(item)
            paletteViews.add(item)
        }

        // 默认选中墙壁
        highlightPaletteItem(2)
    }

    private fun highlightPaletteItem(selectedIndex: Int) {
        for (i in paletteViews.indices) {
            val view = paletteViews[i]
            if (i == selectedIndex) {
                view.background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 8f
                    setStroke(3, Color.parseColor("#4169E1"))
                }
            } else {
                view.background = null
            }
        }
    }

    private fun getCellPreviewColor(cellType: Int): Int {
        return when (cellType) {
            CellType.EMPTY.ordinal -> Color.parseColor("#E8E0D8")
            CellType.FLOOR.ordinal -> Color.parseColor("#D2B48C")
            CellType.WALL.ordinal -> Color.parseColor("#9E9E9E")
            CellType.TARGET.ordinal -> Color.parseColor("#FF4444")
            CellType.BOX.ordinal -> Color.parseColor("#FFA500")
            CellType.PLAYER.ordinal -> Color.WHITE
            else -> Color.LTGRAY
        }
    }

    private fun testEditorLevel() {
        val grid = editorView.getGrid()
        val validation = LevelValidator.validate(grid)
        if (!validation.isValid) {
            Toast.makeText(this, validation.errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        // 找到玩家位置
        var playerRow = 0
        var playerCol = 0
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                if (grid[r][c] == CellType.PLAYER.ordinal || grid[r][c] == CellType.PLAYER_ON_TARGET.ordinal) {
                    playerRow = r
                    playerCol = c
                }
            }
        }

        val level = Level(grid, playerRow, playerCol)

        mainMenuLayout.visibility = View.GONE
        levelSelectLayout.visibility = View.GONE
        gameLayout.visibility = View.VISIBLE
        editorLayout.visibility = View.GONE
        winOverlay.visibility = View.GONE

        tvLevel.text = "测试关卡"

        gameView.post {
            gameView.loadLevelFromData(level)
            updateMovesDisplay()
        }
    }

    private fun saveEditorLevel() {
        val grid = editorView.getGrid()
        val validation = LevelValidator.validate(grid)
        if (!validation.isValid) {
            Toast.makeText(this, validation.errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        val input = EditText(this).apply {
            hint = getString(R.string.enter_level_name)
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.save))
            .setView(input)
            .setPositiveButton("确定") { _, _ ->
                val name = input.text.toString().ifBlank { "自定义关卡" }

                var playerRow = 0
                var playerCol = 0
                for (r in grid.indices) {
                    for (c in grid[r].indices) {
                        if (grid[r][c] == CellType.PLAYER.ordinal || grid[r][c] == CellType.PLAYER_ON_TARGET.ordinal) {
                            playerRow = r
                            playerCol = c
                        }
                    }
                }

                val level = Level(grid, playerRow, playerCol)
                CustomLevelManager.saveLevel(this, name, level)
                Toast.makeText(this, getString(R.string.level_saved), Toast.LENGTH_SHORT).show()
                createLevelButtons() // 刷新关卡列表
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun exportEditorLevel() {
        val grid = editorView.getGrid()
        val validation = LevelValidator.validate(grid)
        if (!validation.isValid) {
            Toast.makeText(this, validation.errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        var playerRow = 0
        var playerCol = 0
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                if (grid[r][c] == CellType.PLAYER.ordinal || grid[r][c] == CellType.PLAYER_ON_TARGET.ordinal) {
                    playerRow = r
                    playerCol = c
                }
            }
        }

        val level = Level(grid, playerRow, playerCol)
        val text = CustomLevelManager.exportLevelToText(level)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("sokoban_level", text))
        Toast.makeText(this, getString(R.string.level_exported), Toast.LENGTH_SHORT).show()
    }

    private fun showLevelSelect() {
        mainMenuLayout.visibility = View.GONE
        levelSelectLayout.visibility = View.VISIBLE
        gameLayout.visibility = View.GONE
        editorLayout.visibility = View.GONE
        winOverlay.visibility = View.GONE
        createLevelButtons() // 刷新自定义关卡
    }

    private fun showWinDialog() {
        val moves = gameView.getMoves()
        val seconds = gameView.getElapsedSeconds()
        val minutes = seconds / 60
        val secs = seconds % 60

        // 计算并保存星级（仅内置关卡）
        if (currentLevel >= 0) {
            val stars = StarManager.calculateStars(currentLevel, moves)
            StarManager.saveStars(this, currentLevel, stars)
            tvStars.text = StarManager.starsToString(stars)
        } else {
            tvStars.text = "🎉"
        }

        tvWinStats.text = "${moves}步  ${minutes}分${secs}秒"
        winOverlay.visibility = View.VISIBLE
    }

    private fun updateMovesDisplay() {
        tvMoves.text = getString(R.string.moves, gameView.getMoves())
    }
}
