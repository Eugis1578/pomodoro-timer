package com.sokoban.game

import android.content.Context
import org.json.JSONObject

object StarManager {

    private const val PREFS_NAME = "sokoban_stars"
    private const val KEY_STARS = "level_stars"

    fun getStars(context: Context, levelIndex: Int): Int {
        val json = loadJson(context)
        return json.optInt(levelIndex.toString(), 0)
    }

    fun saveStars(context: Context, levelIndex: Int, stars: Int) {
        val current = getStars(context, levelIndex)
        if (stars <= current) return // 只保存更高星级
        val json = loadJson(context)
        json.put(levelIndex.toString(), stars)
        getPrefs(context).edit().putString(KEY_STARS, json.toString()).apply()
    }

    fun calculateStars(levelIndex: Int, moves: Int): Int {
        if (levelIndex < 0 || levelIndex >= LevelData.levels.size) return 1
        val thresholds = LevelData.levels[levelIndex].starThresholds
        return when {
            moves <= thresholds[0] -> 3
            moves <= thresholds[1] -> 2
            else -> 1
        }
    }

    fun starsToString(stars: Int): String {
        return "⭐".repeat(stars) + "☆".repeat(3 - stars)
    }

    private fun loadJson(context: Context): JSONObject {
        val str = getPrefs(context).getString(KEY_STARS, null) ?: return JSONObject()
        return try {
            JSONObject(str)
        } catch (e: Exception) {
            JSONObject()
        }
    }

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
