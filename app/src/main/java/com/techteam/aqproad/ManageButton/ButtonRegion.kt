package com.techteam.aqproad.ManageButton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue

class ButtonRegion(
    val centerX: Float,
    val centerY: Float,
    val text: String,
    private val context: Context
) {

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

    //radio boton
    private val buttonRadio = 11f
    //mov del texto respecto al centro del circulo
    private val movX = 10.5f
    private val movY = 7f


    fun draw(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, buttonRadio, buttonPaint)
        //val textY = centerY - (buttonText.descent() + buttonText.ascent()) / 2
        //canvas.drawText("47", centro47.x - movX, centro47.y + movY, buttonText)
        canvas.drawText(text, centerX - movX, centerY + movY, buttonText)
    }

    fun contains(x: Float, y: Float): Boolean {
        val dx = x - centerX
        val dy = y - centerY
        return dx * dx + dy * dy <= buttonRadio * buttonRadio
    }
}
