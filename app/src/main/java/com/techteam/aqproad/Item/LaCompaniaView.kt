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

        // Draw the main rectangular structure (moved an additional 50 pixels to the right)
        canvas.drawRect(210f, 35f, 962.528f, 455f, paint)

        // Sacristía
        canvas.drawRect(208.6f, 35f, 367.136f, 169.456f, paint)
        paint.textSize = 21f
        canvas.drawText("Sacristía", 252f, 105f, paint)

        // Retablo de crucifixion
        canvas.drawRect(367.136f, 35f, 537.537f, 169.456f, paint)
        canvas.drawText("Retablo del\ncrucificado", 420f, 105f, paint)

        // Fila de ambientes 1
        canvas.drawRect(537.537f, 35f, 645.582f, 169.456f, paint)
        canvas.drawText("Espacio 1", 560f, 105f, paint)

        canvas.drawRect(645.582f, 35f, 753.627f, 169.456f, paint)
        canvas.drawText("Espacio 2", 680f, 105f, paint)

        canvas.drawRect(753.627f, 35f, 861.672f, 169.456f, paint)
        canvas.drawText("Espacio 3", 780f, 105f, paint)

        canvas.drawRect(861.672f, 35f, 962.528f, 169.456f, paint)
        canvas.drawText("Espacio 4", 885f, 105f, paint)


        // Fila de ambientes 2
        canvas.drawRect(537.537f, 325.521f, 645.582f, 455f, paint)
        canvas.drawText("Espacio 5", 560f, 375f, paint)


        canvas.drawRect(645.582f, 325.521f, 753.627f, 455f, paint)
        canvas.drawText("Espacio 6", 680f, 375f, paint)

        canvas.drawRect(753.627f, 325.521f, 861.672f, 455f, paint)
        canvas.drawText("Espacio 7", 780f, 375f, paint)

        canvas.drawRect(861.672f, 325.521f, 962.528f, 455f, paint)
        canvas.drawText("Espacio 8", 885f, 375f, paint)

        // Capilla san Ignacio
        canvas.drawRect(68.67f, 325.521f, 244.89f, 501.74f, paint)
        canvas.drawText("Capilla de San Ignacio", 70f, 375f, paint)

        // Antesacristia antigua
        canvas.drawRect(244.89f, 325.521f, 394.41f, 501.74f, paint)
        canvas.drawText("Capilla de San Ignacio", 260f, 375f, paint)
        buttonDraw.draw(canvas)
    }
}
