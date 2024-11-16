package com.techteam.aqproad.Item

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.techteam.aqproad.ManageButton.ButtonManagment

class LaCompaniaView constructor(
    context: Context, attrs: AttributeSet
) : View(context, attrs) {

    private val buttonDraw = ButtonManagment(context)

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    init {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val buttonText = buttonDraw.findButton(event.x, event.y)
                if (buttonText != "none") {
                    //aqui va la funcion que recibe el texto del boton
                    Toast.makeText(context, "Clic en botón con texto: $buttonText", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Set paint properties
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = 2f

        // Desplazamiento de 300 px a la derecha y 50 px hacia abajo
        val offsetX = 300f
        val offsetY = 50f

        // Draw the main rectangular structure
        canvas.drawRect(172f + offsetX, 35f + offsetY, 924.528f + offsetX, 455f + offsetY, paint)

        // Sacristía
        canvas.drawRect(170.6f + offsetX, 35f + offsetY, 329.136f + offsetX, 169.456f + offsetY, paint)
        paint.textSize = 21f
        canvas.drawText("Sacristía", 214f + offsetX, 105f + offsetY, paint)

        // Retablo de crucifixion
        canvas.drawRect(329.136f + offsetX, 35f + offsetY, 499.537f + offsetX, 169.456f + offsetY, paint)
        canvas.drawText("Retablo del\ncrucificado", 382f + offsetX, 105f + offsetY, paint)

        // Fila de ambientes 1
        canvas.drawRect(499.537f + offsetX, 35f + offsetY, 607.582f + offsetX, 169.456f + offsetY, paint)
        canvas.drawText("Espacio 1", 522f + offsetX, 105f + offsetY, paint)

        canvas.drawRect(607.582f + offsetX, 35f + offsetY, 715.627f + offsetX, 169.456f + offsetY, paint)
        canvas.drawText("Espacio 2", 642f + offsetX, 105f + offsetY, paint)

        canvas.drawRect(715.627f + offsetX, 35f + offsetY, 823.672f + offsetX, 169.456f + offsetY, paint)
        canvas.drawText("Espacio 3", 742f + offsetX, 105f + offsetY, paint)

        canvas.drawRect(823.672f + offsetX, 35f + offsetY, 924.528f + offsetX, 169.456f + offsetY, paint)
        canvas.drawText("Espacio 4", 847f + offsetX, 105f + offsetY, paint)

        // Fila de ambientes 2
        canvas.drawRect(499.537f + offsetX, 325.521f + offsetY, 607.582f + offsetX, 455f + offsetY, paint)
        canvas.drawText("Espacio 5", 522f + offsetX, 375f + offsetY, paint)

        canvas.drawRect(607.582f + offsetX, 325.521f + offsetY, 715.627f + offsetX, 455f + offsetY, paint)
        canvas.drawText("Espacio 6", 642f + offsetX, 375f + offsetY, paint)

        canvas.drawRect(715.627f + offsetX, 325.521f + offsetY, 823.672f + offsetX, 455f + offsetY, paint)
        canvas.drawText("Espacio 7", 742f + offsetX, 375f + offsetY, paint)

        canvas.drawRect(823.672f + offsetX, 325.521f + offsetY, 924.528f + offsetX, 455f + offsetY, paint)
        canvas.drawText("Espacio 8", 847f + offsetX, 375f + offsetY, paint)

        // Capilla san Ignacio
        canvas.drawRect(30.67f + offsetX, 325.521f + offsetY, 206.89f + offsetX, 501.74f + offsetY, paint)
        canvas.drawText("Capilla de San Ignacio", 32f + offsetX, 375f + offsetY, paint)

        // Antesacristia antigua
        canvas.drawRect(206.89f + offsetX, 325.521f + offsetY, 356.41f + offsetX, 501.74f + offsetY, paint)
        canvas.drawText("Antesacristia antigua", 222f + offsetX, 375f + offsetY, paint)

        buttonDraw.draw(canvas)
    }
}

