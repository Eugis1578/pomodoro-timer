package com.sokoban.game

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class CustomLevelEntry(val name: String, val level: Level)

object CustomLevelManager {

    private const val PREFS_NAME = "sokoban_custom_levels"
    private const val KEY_LEVELS = "custom_levels"

    fun saveLevel(context: Context, name: String, level: Level): Boolean {
        val levels = loadLevelsRaw(context)
        val json = JSONObject().apply {
            put("name", name)
            put("map", jsonArrayFromMap(level.map))
            put("playerRow", level.playerStartRow)
            put("playerCol", level.playerStartCol)
        }
        levels.put(json)
        getPrefs(context).edit().putString(KEY_LEVELS, levels.toString()).apply()
        return true
    }

    fun loadLevels(context: Context): List<CustomLevelEntry> {
        val raw = loadLevelsRaw(context)
        val result = mutableListOf<CustomLevelEntry>()
        for (i in 0 until raw.length()) {
            val obj = raw.getJSONObject(i)
            val mapArray = obj.getJSONArray("map")
            val map = Array(mapArray.length()) { r ->
                val row = mapArray.getJSONArray(r)
                IntArray(row.length()) { c -> row.getInt(c) }
            }
            val level = Level(
                map = map,
                playerStartRow = obj.getInt("playerRow"),
                playerStartCol = obj.getInt("playerCol")
            )
            result.add(CustomLevelEntry(obj.getString("name"), level))
        }
        return result
    }

    fun deleteLevel(context: Context, index: Int) {
        val levels = loadLevelsRaw(context)
        if (index in 0 until levels.length()) {
            levels.remove(index)
            getPrefs(context).edit().putString(KEY_LEVELS, levels.toString()).apply()
        }
    }

    fun exportLevelToText(level: Level): String {
        val sb = StringBuilder()
        for (row in level.map) {
            sb.appendLine(row.joinToString(","))
        }
        return sb.toString().trimEnd()
    }

    private fun loadLevelsRaw(context: Context): JSONArray {
        val str = getPrefs(context).getString(KEY_LEVELS, null) ?: return JSONArray()
        return try {
            JSONArray(str)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun jsonArrayFromMap(map: Array<IntArray>): JSONArray {
        val arr = JSONArray()
        for (row in map) {
            val rowArr = JSONArray()
            for (cell in row) {
                rowArr.put(cell)
            }
            arr.put(rowArr)
        }
        return arr
    }
}
