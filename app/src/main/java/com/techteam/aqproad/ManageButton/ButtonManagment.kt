package com.techteam.aqproad.ManageButton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.TypedValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.techteam.aqproad.R
import java.io.InputStreamReader

class ButtonManagment(context: Context) {

    private val buttonMap: Map<String, List<ButtonRegion>> = readJson(context)

    private val buttonPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val buttonText = Paint().apply {
        color = Color.WHITE
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6f, context.resources.displayMetrics)
        isAntiAlias = true
    }
    private val buttonRadio = 11f
    private val movX = 10.5f
    private val movY = 7f

    fun draw(canvas: Canvas) {
        for ((_, buttonList) in buttonMap) {
            for (button in buttonList) {
                button.draw(canvas)
            }
        }
    }

    fun findButton(x: Float, y: Float): String {
        for ((_, buttonList) in buttonMap) {
            for (button in buttonList) {
                if (button.contains(x, y)) {
                    return button.text
                }
            }
        }
        return "none"
    }

    private fun readJson(context: Context): Map<String, List<ButtonRegion>> {
        val inputStream = context.resources.openRawResource(R.raw.cords)
        val reader = InputStreamReader(inputStream)

        val gson = Gson()
        val rawMapType = object : TypeToken<Map<String, List<ButtonData>>>() {}.type
        val rawMap = gson.fromJson<Map<String, List<ButtonData>>>(reader, rawMapType)

        reader.close()

        val circleMap = mutableMapOf<String, List<ButtonRegion>>()
        for ((spaceKey, circles) in rawMap) {
            val circleRegions = circles.map { circleData ->
                ButtonRegion(
                    centerX = circleData.centerX,
                    centerY = circleData.centerY,
                    text = circleData.text,
                    context = context
                )
            }
            circleMap[spaceKey] = circleRegions
        }

        return circleMap
    }
}